# Player persistence with Room — full implementation guide

## Context

Right now player data is not persisted anywhere:
- There is no `data/` package, no repository, no local database anywhere in `shared/`.
- `AddPlayerScreen.kt` has **no ViewModel** — the roster is `remember { mutableStateOf(sampleRoster()) }`, seeded from 3 hardcoded players, mutated with plain list ops. `DangerZoneCard(onConfirmClear = {})` is a no-op.
- `HomeViewModel` starts with `players = emptyList()` and never sees AddPlayer's roster. **The two screens are completely disconnected today.**
- Koin has one module (`di/AppModule.kt`), registering only `HomeViewModel`, started inline via `KoinApplication { koinConfiguration { modules(appModule) } }` inside `core/App.kt`. No platform bootstrap exists anywhere.

**Goal**: persist the player roster (identity: name, emblem, accent — not live match score/rounds) via Room, behind a `PlayerRepository`, so both `AddPlayerScreen` and `HomeScreen` read from one source of truth. Scores/rounds/match history stay exactly as-is (in-memory) — that's "game data" for a later pass.

## Web (js/wasmJs) — explicitly skipped for now

Room's Kotlin Multiplatform support (2.7+) covers **Android, iOS (native), and JVM** via the `BundledSQLiteDriver`. It has **no driver for js/wasmJs at all** — there's no "fallback to Room on web" option, so this plan doesn't try. `js`/`wasmJs` get a small hand-rolled `InMemoryPlayerRepository` instead, purely so the existing `webApp` module keeps compiling and running — player data there just won't survive a page reload. Nothing else about this plan depends on that decision; everything is written against the `PlayerRepository` interface, so revisiting web persistence later (e.g. with SQLDelight, which does have web drivers) would only touch that one file.

## Target file tree

```
commonMain
  domain/repository/PlayerRepository.kt
  data/mapper/PlayerMapper.kt
  di/PlatformRepositoryModule.kt          (expect val)
  di/AppModule.kt                          (edit)
  core/App.kt                              (edit)
  presentation/addplayer/contract/AddPlayerState.kt
  presentation/addplayer/contract/AddPlayerEvent.kt
  presentation/addplayer/contract/AddPlayerEffect.kt
  presentation/addplayer/viewmodel/AddPlayerViewModel.kt
  presentation/addplayer/ui/AddPlayerScreen.kt   (edit)
  presentation/home/viewmodel/HomeViewModel.kt   (edit)

nonWebMain (NEW intermediate source set: android + ios + jvm)
  data/local/entity/PlayerEntity.kt        (@Entity)
  data/local/dao/PlayerDao.kt              (@Dao)
  data/local/AppDatabase.kt                (@Database)
  data/local/DatabaseBuilder.kt            (expect fun)
  data/repository/PlayerRepositoryImpl.kt  (Room-backed)
  di/PlatformRepositoryModule.kt           (actual val — covers android+ios+jvm at once)

androidMain
  data/local/DatabaseBuilder.android.kt
  data/local/AppContextInitializer.kt

iosMain
  data/local/DatabaseBuilder.ios.kt

jvmMain
  data/local/DatabaseBuilder.jvm.kt

jsAndWasmJsMain (automatic — Kotlin's default hierarchy template creates this
                 whenever both js() and wasmJs() targets exist)
  data/repository/InMemoryPlayerRepository.kt
  di/PlatformRepositoryModule.kt           (actual val)
```

## Data model note

Existing `domain/model/Player.kt` stays untouched:
```kotlin
data class Player(
    val id: String,
    val name: String,
    val emblem: PlayerEmblem,
    val accent: AccentColor,
    val score: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
)
```
Persist only `id`, `name`, `emblem.name`, `accent.name`. `score`/`wins`/`losses` stay transient (default `0` from the mapper) — deferred "game data".

---

## 1. Gradle changes

### `gradle/libs.versions.toml`

```toml
[versions]
room = "2.7.0"                 # verify latest stable on Maven Central before use
ksp = "2.4.10-2.0.5"            # must match your exact Kotlin version line — check the KSP releases page
androidxSqlite = "2.6.0"
androidxStartup = "1.2.0"

[libraries]
androidx-room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
androidx-room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
androidx-sqlite-bundled = { module = "androidx.sqlite:sqlite-bundled", version.ref = "androidxSqlite" }
androidx-startup-runtime = { module = "androidx.startup:startup-runtime", version.ref = "androidxStartup" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
androidxRoom = { id = "androidx.room", version.ref = "room" }
```

### root `build.gradle.kts`

Add two lines to the existing `plugins { ... apply false }` block:
```kotlin
alias(libs.plugins.ksp) apply false
alias(libs.plugins.androidxRoom) apply false
```

### `shared/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)           // NEW
    alias(libs.plugins.androidxRoom)  // NEW
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    // ...existing target declarations (iosArm64/iosSimulatorArm64/jvm/js/wasmJs/android) unchanged...

    sourceSets {
        val nonWebMain by creating {
            dependsOn(commonMain.get())
        }
        androidMain.get().dependsOn(nonWebMain)
        iosMain.get().dependsOn(nonWebMain)
        jvmMain.get().dependsOn(nonWebMain)

        nonWebMain.dependencies {
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }
        androidMain.dependencies {
            // ...existing entries...
            implementation(libs.androidx.startup.runtime)
        }
        // jsMain / wasmJsMain: no new dependency needed — InMemoryPlayerRepository is plain Kotlin
    }
}

dependencies {
    // Room's KSP codegen must be wired per KMP compilation target.
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}
```
The `androidx.room` Gradle plugin mainly manages the schema export directory; recent versions can also help auto-wire KSP per target — if it doesn't fully cover your exact target set, the manual `add("ksp<Target>", ...)` calls above are the guaranteed-to-work fallback.

### `androidApp/src/main/AndroidManifest.xml`

Add inside `<application>` (this is the *only* android app-module change — `MainActivity.kt` stays untouched):
```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
        android:name="com.score.pulse.data.local.AppContextInitializer"
        android:value="androidx.startup" />
</provider>
```

---

## 2. `shared/src/commonMain/kotlin/com/score/pulse/domain/repository/PlayerRepository.kt`

```kotlin
package com.score.pulse.domain.repository

import com.score.pulse.domain.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun observePlayers(): Flow<List<Player>>
    suspend fun addPlayer(player: Player)
    suspend fun removePlayer(playerId: String)
    suspend fun clearAll()
}
```

## 3. `shared/src/nonWebMain/kotlin/com/score/pulse/data/local/entity/PlayerEntity.kt`

```kotlin
package com.score.pulse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val emblem: String,
    val accent: String,
)
```

## 4. `shared/src/nonWebMain/kotlin/com/score/pulse/data/local/dao/PlayerDao.kt`

```kotlin
package com.score.pulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.score.pulse.data.local.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players")
    fun observeAll(): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(player: PlayerEntity)

    @Query("DELETE FROM players WHERE id = :playerId")
    suspend fun deleteById(playerId: String)

    @Query("DELETE FROM players")
    suspend fun clearAll()
}
```

## 5. `shared/src/nonWebMain/kotlin/com/score/pulse/data/local/AppDatabase.kt`

```kotlin
package com.score.pulse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.score.pulse.data.local.dao.PlayerDao
import com.score.pulse.data.local.entity.PlayerEntity

@Database(entities = [PlayerEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
}
```

## 6. `shared/src/nonWebMain/kotlin/com/score/pulse/data/local/DatabaseBuilder.kt`

```kotlin
package com.score.pulse.data.local

import androidx.room.RoomDatabase

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
```

## 7. `shared/src/androidMain/kotlin/com/score/pulse/data/local/AppContextInitializer.kt`

Captures the Application context at process start via Jetpack App Startup — no `Application` subclass or `MainActivity` change needed.

```kotlin
package com.score.pulse.data.local

import android.content.Context
import androidx.startup.Initializer

class AppContextInitializer : Initializer<Context> {
    override fun create(context: Context): Context {
        appContext = context.applicationContext
        return appContext
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    companion object {
        lateinit var appContext: Context
    }
}
```

## 8. `shared/src/androidMain/kotlin/com/score/pulse/data/local/DatabaseBuilder.android.kt`

```kotlin
package com.score.pulse.data.local

import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val appContext = AppContextInitializer.appContext
    val dbFile = appContext.getDatabasePath("scorepulse.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
```

## 9. `shared/src/iosMain/kotlin/com/score/pulse/data/local/DatabaseBuilder.ios.kt`

```kotlin
package com.score.pulse.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFilePath = NSHomeDirectory() + "/scorepulse.db"
    return Room.databaseBuilder<AppDatabase>(name = dbFilePath)
}
```

## 10. `shared/src/jvmMain/kotlin/com/score/pulse/data/local/DatabaseBuilder.jvm.kt`

```kotlin
package com.score.pulse.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbDir = File(System.getProperty("user.home"), ".scorepulse")
    if (!dbDir.exists()) dbDir.mkdirs()
    val dbFile = File(dbDir, "scorepulse.db")
    return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
}
```

## 11. `shared/src/commonMain/kotlin/com/score/pulse/data/mapper/PlayerMapper.kt`

```kotlin
package com.score.pulse.data.mapper

import com.score.pulse.data.local.entity.PlayerEntity
import com.score.pulse.domain.model.AccentColor
import com.score.pulse.domain.model.Player
import com.score.pulse.domain.model.PlayerEmblem

fun PlayerEntity.toDomain(): Player = Player(
    id = id,
    name = name,
    emblem = PlayerEmblem.valueOf(emblem),
    accent = AccentColor.valueOf(accent),
)
```
(Placed in `commonMain` even though `PlayerEntity` only exists in `nonWebMain` — that's fine since this file is only ever *compiled* as part of the `nonWebMain`-descended targets' source tree; if your Gradle setup complains, just move it into `nonWebMain` instead — it makes no functional difference.)

## 12. `shared/src/nonWebMain/kotlin/com/score/pulse/data/repository/PlayerRepositoryImpl.kt`

One implementation, shared by android/ios/jvm.

```kotlin
package com.score.pulse.data.repository

import com.score.pulse.data.local.dao.PlayerDao
import com.score.pulse.data.local.entity.PlayerEntity
import com.score.pulse.data.mapper.toDomain
import com.score.pulse.domain.model.Player
import com.score.pulse.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerRepositoryImpl(
    private val dao: PlayerDao,
) : PlayerRepository {

    override fun observePlayers(): Flow<List<Player>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override suspend fun addPlayer(player: Player) {
        dao.upsert(
            PlayerEntity(
                id = player.id,
                name = player.name,
                emblem = player.emblem.name,
                accent = player.accent.name,
            )
        )
    }

    override suspend fun removePlayer(playerId: String) {
        dao.deleteById(playerId)
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }
}
```

## 13. `shared/src/jsAndWasmJsMain/kotlin/com/score/pulse/data/repository/InMemoryPlayerRepository.kt`

```kotlin
package com.score.pulse.data.repository

import com.score.pulse.domain.model.Player
import com.score.pulse.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class InMemoryPlayerRepository : PlayerRepository {
    private val players = MutableStateFlow<List<Player>>(emptyList())

    override fun observePlayers(): Flow<List<Player>> = players

    override suspend fun addPlayer(player: Player) {
        players.value = players.value.filterNot { it.id == player.id } + player
    }

    override suspend fun removePlayer(playerId: String) {
        players.value = players.value.filterNot { it.id == playerId }
    }

    override suspend fun clearAll() {
        players.value = emptyList()
    }
}
```

## 14. `shared/src/commonMain/kotlin/com/score/pulse/di/PlatformRepositoryModule.kt`

```kotlin
package com.score.pulse.di

import org.koin.core.module.Module

expect val platformRepositoryModule: Module
```

## 15. `shared/src/nonWebMain/kotlin/com/score/pulse/di/PlatformRepositoryModule.kt`

Satisfies android + ios + jvm at once (an `actual` declared in an intermediate source set covers every leaf target that depends on it).

```kotlin
package com.score.pulse.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.score.pulse.data.local.AppDatabase
import com.score.pulse.data.local.getDatabaseBuilder
import com.score.pulse.data.repository.PlayerRepositoryImpl
import com.score.pulse.domain.repository.PlayerRepository
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformRepositoryModule: Module = module {
    single<AppDatabase> {
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<PlayerRepository> { PlayerRepositoryImpl(get<AppDatabase>().playerDao()) }
}
```

## 16. `shared/src/jsAndWasmJsMain/kotlin/com/score/pulse/di/PlatformRepositoryModule.kt`

```kotlin
package com.score.pulse.di

import com.score.pulse.data.repository.InMemoryPlayerRepository
import com.score.pulse.domain.repository.PlayerRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformRepositoryModule: Module = module {
    single<PlayerRepository> { InMemoryPlayerRepository() }
}
```

## 17. `shared/src/commonMain/kotlin/com/score/pulse/di/AppModule.kt` (edit)

```kotlin
package com.score.pulse.di

import com.score.pulse.presentation.addplayer.viewmodel.AddPlayerViewModel
import com.score.pulse.presentation.home.viewmodel.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddPlayerViewModel)
}
```

## 18. `shared/src/commonMain/kotlin/com/score/pulse/core/App.kt` (edit)

Add the import `com.score.pulse.di.platformRepositoryModule`, and change:
```kotlin
KoinApplication(configuration = koinConfiguration { modules(appModule) }) {
```
to:
```kotlin
KoinApplication(configuration = koinConfiguration { modules(appModule, platformRepositoryModule) }) {
```
Nothing else in `App.kt` changes.

---

## 19. `shared/src/commonMain/kotlin/com/score/pulse/presentation/addplayer/contract/AddPlayerState.kt`

The draft input fields (`name`, `selectedEmblem`, `selectedAccent`) stay as local Compose `remember` state in the UI layer — same pattern already used for the "Start Game" dialog. Only the *persisted* roster belongs in `AddPlayerState`.

```kotlin
package com.score.pulse.presentation.addplayer.contract

import com.greentasty.core.base.UiState
import com.score.pulse.domain.model.Player

data class AddPlayerState(
    val players: List<Player> = emptyList(),
) : UiState
```

## 20. `shared/src/commonMain/kotlin/com/score/pulse/presentation/addplayer/contract/AddPlayerEvent.kt`

```kotlin
package com.score.pulse.presentation.addplayer.contract

import com.greentasty.core.base.UiEvent
import com.score.pulse.domain.model.AccentColor
import com.score.pulse.domain.model.PlayerEmblem

sealed class AddPlayerEvent : UiEvent {
    data class AddPlayerClicked(
        val name: String,
        val emblem: PlayerEmblem,
        val accent: AccentColor,
    ) : AddPlayerEvent()

    data class RemovePlayerClicked(val playerId: String) : AddPlayerEvent()

    data object ClearAllClicked : AddPlayerEvent()
}
```

## 21. `shared/src/commonMain/kotlin/com/score/pulse/presentation/addplayer/contract/AddPlayerEffect.kt`

No one-off navigation/side-effects needed yet (a real error snackbar can use the existing global `SnackbarController` directly from the ViewModel, no screen-scoped effect needed). Kept empty for symmetry with `HomeEffect`/the rest of the codebase's MVI trio — delete later if it stays unused.

```kotlin
package com.score.pulse.presentation.addplayer.contract

import com.greentasty.core.base.UiEffect

sealed class AddPlayerEffect : UiEffect
```

## 22. `shared/src/commonMain/kotlin/com/score/pulse/presentation/addplayer/viewmodel/AddPlayerViewModel.kt`

```kotlin
package com.score.pulse.presentation.addplayer.viewmodel

import androidx.lifecycle.viewModelScope
import com.score.pulse.core.base.BaseViewModel
import com.score.pulse.domain.model.Player
import com.score.pulse.domain.repository.PlayerRepository
import com.score.pulse.presentation.addplayer.contract.AddPlayerEffect
import com.score.pulse.presentation.addplayer.contract.AddPlayerEvent
import com.score.pulse.presentation.addplayer.contract.AddPlayerState
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddPlayerViewModel(
    private val playerRepository: PlayerRepository,
) : BaseViewModel<AddPlayerEvent, AddPlayerState, AddPlayerEffect>() {

    init {
        viewModelScope.launch {
            playerRepository.observePlayers().collect { players ->
                setState { copy(players = players) }
            }
        }
    }

    override fun createInitialState(): AddPlayerState = AddPlayerState()

    @OptIn(ExperimentalUuidApi::class)
    override fun handleEvent(event: AddPlayerEvent) {
        when (event) {
            is AddPlayerEvent.AddPlayerClicked -> {
                val trimmed = event.name.trim()
                if (trimmed.isEmpty()) return
                viewModelScope.launch {
                    playerRepository.addPlayer(
                        Player(
                            id = Uuid.random().toString(),
                            name = trimmed,
                            emblem = event.emblem,
                            accent = event.accent,
                        )
                    )
                }
            }

            is AddPlayerEvent.RemovePlayerClicked -> {
                viewModelScope.launch { playerRepository.removePlayer(event.playerId) }
            }

            AddPlayerEvent.ClearAllClicked -> {
                viewModelScope.launch { playerRepository.clearAll() }
            }
        }
    }
}
```
(`kotlin.uuid.Uuid` may still require the `@OptIn`/be experimental depending on your exact Kotlin stdlib version — check when you implement; if unavailable, swap in any other multiplatform-safe id generator.)

## 23. `shared/src/commonMain/kotlin/com/score/pulse/presentation/addplayer/ui/AddPlayerScreen.kt` (full replacement)

Same UI/layout as today; `roster`/`nextId` local state and `sampleRoster()` are gone — the roster now comes from the ViewModel. `RosterList`, `DangerZoneCard`, `EmblemPickerGrid`, `AccentColorPicker` are unchanged, reused as-is.

```kotlin
package com.score.pulse.presentation.addplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.score.pulse.core.components.GradientPrimaryButton
import com.score.pulse.core.components.SectionHeader
import com.score.pulse.core.theme.ScorePulseTheme
import com.score.pulse.domain.model.AccentColor
import com.score.pulse.domain.model.PlayerEmblem
import com.score.pulse.presentation.addplayer.contract.AddPlayerEvent
import com.score.pulse.presentation.addplayer.contract.AddPlayerState
import com.score.pulse.presentation.addplayer.viewmodel.AddPlayerViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddPlayerRoot(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    viewModel: AddPlayerViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AddPlayerScreen(
        modifier = modifier,
        contentPadding = contentPadding,
        state = state,
        onEvent = viewModel::setEvent,
    )
}

@Composable
private fun AddPlayerScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    state: AddPlayerState,
    onEvent: (AddPlayerEvent) -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var selectedEmblem by remember { mutableStateOf(PlayerEmblem.Gamepad) }
    var selectedAccent by remember { mutableStateOf(AccentColor.Emerald) }

    fun addPlayer() {
        onEvent(AddPlayerEvent.AddPlayerClicked(name, selectedEmblem, selectedAccent))
        name = ""
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 20) name = it },
                label = { Text("Player Name") },
                placeholder = { Text("e.g. ShadowHunter") },
                leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
                trailingIcon = {
                    if (name.isNotEmpty()) {
                        IconButton(onClick = { name = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            SectionHeader(title = "Choose Player Emblem", icon = Icons.Filled.SportsEsports)
        }
        item {
            EmblemPickerGrid(selected = selectedEmblem, onSelect = { selectedEmblem = it })
        }
        item {
            SectionHeader(title = "Neon HUD Accent", icon = Icons.Filled.Palette)
        }
        item {
            AccentColorPicker(selected = selectedAccent, onSelect = { selectedAccent = it })
        }
        item {
            GradientPrimaryButton(
                text = "Add Player to Roster",
                icon = Icons.Filled.PersonAdd,
                onClick = ::addPlayer,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            SectionHeader(
                title = "Player Management",
                icon = Icons.Filled.Groups,
                style = MaterialTheme.typography.headlineSmall,
                trailing = {
                    Text(
                        text = "${state.players.size} Player${if (state.players.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }
        rosterList(
            roster = state.players,
            onRemove = { player -> onEvent(AddPlayerEvent.RemovePlayerClicked(player.id)) },
        )
        item {
            DangerZoneCard(onConfirmClear = { onEvent(AddPlayerEvent.ClearAllClicked) })
        }
    }
}

@Preview
@Composable
private fun AddPlayerScreenPreview() {
    ScorePulseTheme {
        AddPlayerScreen(state = AddPlayerState())
    }
}
```

## 24. `shared/src/commonMain/kotlin/com/score/pulse/presentation/home/viewmodel/HomeViewModel.kt` (edit)

Only the constructor + `init` block change. **Important correctness detail**: don't blindly reset every player's score to `0` on every roster emission — that would wipe an in-progress match's scores the instant someone edits the roster on the Add Player tab while a game is running. Preserve scores for players already present; default only *new* players to `0`.

```kotlin
package com.score.pulse.presentation.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.score.pulse.core.base.BaseViewModel
import com.score.pulse.core.snackbar.SnackbarController
import com.score.pulse.core.snackbar.SnackbarEvent
import com.score.pulse.domain.model.Game
import com.score.pulse.domain.model.Player
import com.score.pulse.domain.repository.PlayerRepository
import com.score.pulse.presentation.home.contract.HomeEffect
import com.score.pulse.presentation.home.contract.HomeEvent
import com.score.pulse.presentation.home.contract.HomeState
import kotlinx.coroutines.launch

class HomeViewModel(
    private val playerRepository: PlayerRepository,
) : BaseViewModel<HomeEvent, HomeState, HomeEffect>() {

    init {
        viewModelScope.launch {
            playerRepository.observePlayers().collect { roster ->
                setState {
                    val existingScores = players.associate { it.id to it.score }
                    copy(players = roster.map { it.copy(score = existingScores[it.id] ?: 0) })
                }
            }
        }
    }

    override fun createInitialState(): HomeState = HomeState()

    override fun handleEvent(event: HomeEvent) {
        // ...unchanged from current implementation (AdjustScoreClicked, OpenEditScoreDialog,
        // DismissEdit, OpenGameResultDialog, OpenLockRoundDialog, ConfirmLockRoundClicked,
        // DismissLockRound, OpenStartGameDialog, DismissStartGame, StartNewGameClicked,
        // AddPlayerClicked, sendSnackbar helper, adjustPlayerPt helper)...
    }
}
```

---

## Suggested implementation order

1. Gradle: version catalog + root/shared build files + `nonWebMain` source set + AndroidManifest meta-data. Get `./gradlew :shared:compileAndroidMain :shared:compileKotlinJvm :shared:compileKotlinIosArm64 :shared:compileKotlinJs :shared:compileKotlinWasmJs` green with an empty `@Entity`/`@Dao`/`@Database` before writing the rest of the app code (Room+KSP version mismatches are the easiest thing to get wrong the first time — isolate that failure early).
2. `domain/repository/PlayerRepository.kt`.
3. `nonWebMain`: entity, DAO, database, `DatabaseBuilder` expect, `PlayerMapper`, `PlayerRepositoryImpl`.
4. `androidMain`/`iosMain`/`jvmMain`: `DatabaseBuilder` actuals (+ the Android startup initializer + manifest entry).
5. `jsAndWasmJsMain`: `InMemoryPlayerRepository` + its `PlatformRepositoryModule` actual.
6. Wire `platformRepositoryModule` into `App.kt`; add `AddPlayerViewModel` to `AppModule.kt`.
7. `AddPlayerViewModel` + the three contract files; refactor `AddPlayerScreen.kt`.
8. `HomeViewModel` repository injection (with the score-preserving merge logic above).
9. Compile every target; manually verify.

## Verification

- `./gradlew :shared:compileAndroidMain :shared:compileKotlinJvm :shared:compileKotlinIosArm64 :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinJs :shared:compileKotlinWasmJs` all green.
- Run Android or desktop (`/run` or `./gradlew :desktopApp:run`): add 2+ players on Add Player → confirm they appear in the Home arena circle (this is the disconnected-state bug getting fixed). Remove one → disappears from both. Restart the app → roster survived.
- While a match is in progress on Home, add a new player on the Add Player tab, come back to Home: existing players' scores must be unchanged; the new player shows up at `0`.
- Run the web target: still builds/launches; roster just won't survive a reload (expected — Room doesn't support js/wasmJs at all, by design for this pass).
