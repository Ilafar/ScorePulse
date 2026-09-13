# ScorePulse — Project Overview

Purpose of this document: a complete, self-contained briefing for another AI (or a new contributor) with zero prior context on this codebase. It describes what exists today, exactly as of this writing — not aspirational architecture.

## What it is

ScorePulse is a Kotlin Multiplatform (KMP) app for tracking scores/rounds during tabletop or party games: add players, start a game with a name and round count, adjust each player's score during play, lock rounds, finish a match, and (eventually) browse match history and a leaderboard. It's a solo/portfolio project, currently mid-refactor from a scaffolded template into a proper MVI + clean-architecture app.

## Tech stack

| Concern | Choice | Version (from `gradle/libs.versions.toml`) |
|---|---|---|
| Language | Kotlin Multiplatform | `2.4.10` |
| UI | Compose Multiplatform | `1.11.1` |
| UI (Material) | Material3 | `1.11.0-alpha07` |
| Icons | `material-icons-extended` | `1.7.3` |
| DI | Koin (`koin-core`, `koin-compose`, `koin-compose-viewmodel`) | `4.2.2` |
| Async | kotlinx-coroutines | `1.11.0` |
| Lifecycle | `org.jetbrains.androidx.lifecycle` (viewmodel-compose, runtime-compose) | `2.11.0-beta01` |
| Android build | AGP | `9.0.1` |
| Android SDK | compileSdk 36 / minSdk 30 / targetSdk 36 | |
| Persistence | **none yet** — see "Known gaps" below | |

No test dependencies are actually used (they're catalogued in the version catalog — `kotlin-test`, `junit`, `androidx-espresso-core`, etc. — but zero test source sets or test files exist anywhere in the project).

## Modules / targets

Gradle modules (from `settings.gradle.kts`, root project name `ScorePulse`):
- `:shared` — the KMP module. All app logic and UI live here as `commonMain` Compose code, plus platform source sets: `androidMain`, `iosMain` (compiled for both `iosArm64` and `iosSimulatorArm64`), `jvmMain`, `jsMain`, `wasmJsMain`.
- `:androidApp` — thin Android entry point. `MainActivity.kt` just calls `setContent { App() }`.
- `:desktopApp` — thin JVM/Compose-Desktop entry point (`main.kt` opens a `Window` and calls `App()`).
- `:webApp` — thin Kotlin/Wasm-or-JS entry point (`ComposeViewport { App() }`).
- `iosApp/` exists as an **Xcode project**, not a Gradle module — `iosApp/iosApp/iOSApp.swift` hosts a SwiftUI `ContentView` that presumably embeds the Compose UI via the `shared` framework.

Every platform entry point calls the same zero-argument `App()` composable defined in `shared/src/commonMain/kotlin/com/score/pulse/core/App.kt`. There is no per-platform bootstrap code (no `Application` subclass override, no `startKoin` call anywhere) — Koin is started *inside* Compose via `KoinApplication(configuration = koinConfiguration { modules(appModule) }) { ... }` in `App()`.

## Architecture

**Pattern**: MVI (Model-View-Intent) via a small hand-rolled base class, one screen = one `State`/`Event`/`Effect`/`ViewModel` group, plus Compose UI that's a pure function of `State` and emits `Event`s.

`core/base/BaseViewModel.kt`:
```kotlin
abstract class BaseViewModel<Event : UiEvent, State : UiState, Effect : UiEffect> : ViewModel() {
    val uiState: StateFlow<State>       // current screen state, backed by MutableStateFlow
    val effect: Flow<Effect>            // one-shot effects (navigation etc.), backed by a Channel
    fun setEvent(event: Event)          // UI calls this; internally emits into a SharedFlow, collected in init
    protected fun setState(reducer: State.() -> State)  // pure state reducer
    protected fun setEffect(builder: () -> Effect)       // fire a one-shot effect
    protected val currentState: State   // synchronous read of uiState.value
    abstract fun createInitialState(): State
    abstract fun handleEvent(event: Event)
}
```
`UiState`, `UiEvent`, `UiEffect` are empty marker interfaces at `com.greentasty.core.base.*` (**note the package name mismatch** — `com.greentasty...` rather than `com.score.pulse...`; leftover from whatever template this was seeded from — see "Known gaps").

Each feature under `presentation/<feature>/` follows (or, for two features, *should* follow but doesn't yet — see below):
```
presentation/<feature>/
  contract/<Feature>State.kt     data class implementing UiState, with derived `val` properties for anything computable from other fields
  contract/<Feature>Event.kt     sealed class implementing UiEvent, grouped with //Open //Click //Dismiss comments
  contract/<Feature>Effect.kt    sealed class implementing UiEffect (one-shot things like navigation)
  viewmodel/<Feature>ViewModel.kt  extends BaseViewModel<Event, State, Effect>
  ui/<Feature>Screen.kt          `<Feature>Root` (resolves the ViewModel via koinViewModel(), collects state via collectAsStateWithLifecycle(), passes state+onEvent down) + private `<Feature>Screen` (pure UI)
```

**DI**: single Koin module today, `di/AppModule.kt`:
```kotlin
val appModule = module {
    viewModelOf(::HomeViewModel)
}
```
resolved via `koinViewModel()` inside each `*Root` composable (from `org.koin.compose.viewmodel`).

**Global snackbar** (`core/snackbar/`): a Channel-based singleton, decoupled from any specific screen/ViewModel:
```kotlin
object SnackbarController {
    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()
    suspend fun sendEvent(event: SnackbarEvent)
}
data class SnackbarEvent(val message: String, val action: SnackbarAction? = null)
data class SnackbarAction(val name: String, val action: suspend () -> Unit)
```
plus a lifecycle-aware collector `ObserveAsEvents(flow, key1, key2, onEvent)` (wraps `repeatOnLifecycle(STARTED)` + `Dispatchers.Main.immediate`). Wired once in `App()` against the single `Scaffold`'s `snackbarHostState`. Any ViewModel anywhere can call `SnackbarController.sendEvent(...)` inside `viewModelScope.launch { }` — no per-screen effect plumbing needed. `HomeViewModel` uses this today via a private `sendSnackbar(message: String)` helper.

## Domain models (`domain/model/`)

```kotlin
data class Player(
    val id: String, val name: String, val emblem: PlayerEmblem, val accent: AccentColor,
    val score: Int = 0, val wins: Int = 0, val losses: Int = 0,
)
data class Game(val name: String = "", val maxRounds: Int = 0)
data class MatchParticipant(val name: String, val emblem: PlayerEmblem, val accent: AccentColor, val score: Int, val rank: Int)
data class MatchRecord(val id: String, val title: String, val durationMinutes: Int, val dateLabel: String, val participants: List<MatchParticipant>) {
    val champion: MatchParticipant get() = participants.minBy { it.rank }
}
data class RankingEntry(val rank: Int, val name: String, val tag: String, val emblem: PlayerEmblem, val accent: AccentColor, val wins: Int, val matches: Int, val score: Int)
```
`PlayerEmblem` (enum, 12 values: Gamepad, Thunder, Phoenix, Mecha, Skull, Shield, Arcade, Sniper, Stealth, Saber, Strike, Apex) and `AccentColor` (enum, 5 values: Emerald, Cyan, Magenta, Amber, Violet) each carry Compose-specific data on the enum constants themselves (`PlayerEmblem` has an `icon: ImageVector` + `label: String` per constant; `AccentColor` has `@Composable` extension functions `containerColor()`/`onContainerColor()`). This means `domain/model/` currently has a layering leak — it imports `androidx.compose.material3`/`ImageVector` — see "Known gaps."

## Screens — current state, one by one

### Home (`presentation/home/`) — fully MVI, most mature screen

- **State** (`HomeState`): `game: Game`, `players: List<Player>`, `currentRound: Int`, `editingPlayerId: String?`, `isLockRoundConfirmationVisible/isGameResultVisible/isStartGameDialogVisible: Boolean`. Derived: `isFinalRound`, `hasMinimumPlayers` (`players.size >= 2`), `roundLabel`, `editingPlayer` (looked up by id, avoids storing a stale `Player` snapshot).
- **Events**: `OpenStartGameDialog`, `OpenLockRoundDialog`, `OpenGameResultDialog`, `OpenEditScoreDialog(playerId)`, `AdjustScoreClicked(playerId, delta)`, `StartNewGameClicked(gameName, totalRounds)`, `ConfirmLockRoundClicked`, `AddPlayerClicked`, `DismissEdit`, `DismissLockRound`, `DismissStartGame`.
- **Effect**: `NavigateToAddPlayer` (only one) — collected in `HomeRoot` via `LaunchedEffect(viewModel) { viewModel.effect.collect { ... } }`, calling an `onNavigateToAddPlayer: () -> Unit` passed down from `App()`, which flips the bottom-nav `destination` state to `AppDestination.Add`.
- **UI** (`HomeScreen.kt`): a `LazyColumn` of `ArenaRadarCard` (the circular 4-player "arena" visual — see below), one `PlayerScoreEntryCard` per player (+/- steppers and a custom-score dialog), a `GameActionBar` (finish match / lock round buttons), and a `NoPlayerInfoBanner` (shown when `!state.hasMinimumPlayers`, with an "Add player" button wired to `AddPlayerClicked`).
- Dialogs (`HomeDialogs.kt`): `StartGame` (name + round-count form, digit-filtered input, ignores submit if name blank or rounds not a positive int) and `CustomScoreDialog` (digit-filtered custom score entry).
- **`ArenaRadarCard.kt`**: draws 4 player "slots" around a circular radar visual with connecting rings; any slot without a real player renders `ArenaEmptySlotNode` (a muted circle with a question-mark icon + "Open" label) instead of being blank. The center "VS" badge (`ArenaVsBadge`) is tappable — tapping it fires `HomeEvent.OpenStartGameDialog` (there used to be a separate "Start New Game" FAB for this; it was removed and its behavior consolidated into the VS badge tap).
- `HomeViewModel` currently starts with an **empty** player list (`HomeState()`) — nothing seeds it from anywhere. `StartNewGameClicked` validates `hasMinimumPlayers` via `currentState` before mutating state, and sends a snackbar either way ("Game started" / "Minimum 2 players required").

### Add Player (`presentation/addplayer/`) — UI only, no ViewModel yet

`AddPlayerScreen.kt` has **no ViewModel, no State/Event/Effect contract**. Everything is local Compose state:
```kotlin
var name by remember { mutableStateOf("") }
var selectedEmblem by remember { mutableStateOf(PlayerEmblem.Gamepad) }
var selectedAccent by remember { mutableStateOf(AccentColor.Emerald) }
var roster by remember { mutableStateOf(sampleRoster()) }   // 3 hardcoded players
```
Add/remove are plain list operations (`roster - player`). `DangerZoneCard`'s "Clear Entire Score History" two-tap-confirm button calls `onConfirmClear = {}` — **currently a no-op**. Sub-composables reused here: `EmblemPickerGrid`, `AccentColorPicker`, `RosterList` (a `LazyListScope` extension `rosterList(...)`), `DangerZoneCard`.

**This means Home and Add Player are completely disconnected today** — players added on this screen never appear on Home, and vice versa. This is a known, real bug (not by design) and is the main motivation for the persistence work described below.

### Leaderboard (`presentation/leaderboard/`) — static mock data

`LeaderboardRoot` → private `LeaderboardScreen`, backed by `remember { sampleRankings() }` (8 hardcoded `RankingEntry` rows). No ViewModel, no Koin. Layout: podium section (`PodiumSection` — top 3, center one elevated/larger as "champion") + a ranked table (`RankingRow` per remaining player: rank, avatar+name, wins, matches, score).

### History (`presentation/history/`) — static mock data

`HistoryRoot` → private `HistoryScreen`, backed by `remember { sampleMatches() }` (3 hardcoded `MatchRecord`s). No ViewModel, no Koin. Each match renders as a `MatchResultCard` (lives in `MatchHistoryCard.kt` — filename doesn't match the composable name) inside a `GlassCard`: title, duration/date meta tags, a `ChampionSpotlight` (highlights `match.champion`, computed as `participants.minBy { it.rank }`), and a `StandingsGrid` (2-column grid of all participants sorted by rank).

## Shared UI infrastructure (`core/`)

- `core/theme/` — `ScorePulseTheme`, `Theme.kt`, `Color.kt`, `Type.kt`, `Shape.kt` (Material3 theming, dark "neon HUD" aesthetic based on the screenshots seen during development).
- `core/components/` — reusable composables: `GlassCard` (translucent card container used everywhere), `GradientPrimaryButton` (icon+text button with a horizontal gradient background, used for every primary CTA), `GlassSecondaryButton`, `AlertDialogCompact` (title/text/confirm/dismiss compact dialog), `EmblemAvatar` (circular emblem icon with optional status dot, used for player avatars everywhere), `ScoreStatText`, `SectionHeader`, `AppBottomNavBar` + `AppDestination` (enum: `Home`, `Ranks`, `Add`, `History` — drives the bottom nav and the `when` in `App()`'s content slot).
- `core/base/` — `BaseViewModel`, and the `UiState`/`UiEvent`/`UiEffect` marker interfaces (imported from `com.greentasty.core.base`, not `com.score.pulse.core.base` — see "Known gaps").
- `core/snackbar/` — `SnackbarController`, `SnackbarEvent`, `SnackbarAction`, `ObserveAsEvents` (described above).
- `Platform.kt` (+ `.android.kt`/`.ios.kt`/`.jvm.kt`/`.js.kt`/`.wasmJs.kt`) — the only current `expect`/`actual` in the project; a template leftover, minimal content.

## Known gaps / tech debt (as of this writing)

1. **No persistence anywhere.** Everything lives in in-memory Compose/ViewModel state and is lost on process death. A detailed implementation guide for adding this (Room-based, scoped to just the player roster — not scores/match history) lives at `docs/player-persistence-plan.md` in this repo — not yet implemented.
2. **Home and Add Player are disconnected** (see above) — direct consequence of #1 having no shared source of truth.
3. **Add Player has no ViewModel/MVI contract** — the only screen still fully in "template" style, inconsistent with Home's pattern.
4. **Leaderboard and History are 100% static/mock** — no real match data ever reaches them; `HomeViewModel`'s "Finish Match" flow (`isGameResultVisible` → `MatchResultCard`) doesn't write anything to a shared match-history store.
5. **`ConfirmLockRoundClicked`** only closes the confirmation dialog (`isLockRoundConfirmationVisible = false`) — despite the dialog's own text ("Reset player points for next round?"), it does **not** actually reset scores or increment `currentRound`. Likely an unfinished feature, not intentional.
6. **`DangerZoneCard`'s clear-all action is wired to `{}`** on the Add Player screen — a no-op.
7. **Package name leftover**: `UiState`/`UiEvent`/`UiEffect` live under `com.greentasty.core.base` instead of `com.score.pulse.core.base`, while `BaseViewModel` itself correctly uses `com.score.pulse.core.base`. Cosmetic but inconsistent.
8. **`AccentColor`/`PlayerEmblem` (domain enums) import Compose UI types directly** (`ImageVector`, `@Composable` extensions) — a clean-architecture layering leak; harmless in practice since nothing outside Compose UI code touches those members, but worth normalizing if the domain layer is ever reused outside Compose (e.g. a future persistence/mapping layer only ever touches `.name`).
9. **No tests** anywhere in the repo (no test source sets, no test files) despite test library coordinates being present in the version catalog.
10. **`README.md`** is still the unmodified stock JetBrains KMP template README — no project-specific documentation existed before this file and `docs/player-persistence-plan.md`.

## Planned next step

`docs/player-persistence-plan.md` (same repo, sibling to this file) is a full, ready-to-implement guide for adding Room-based persistence of the player roster (`id`/`name`/`emblem`/`accent` — explicitly not scores/rounds/match history, which stay in-memory for now). It covers: a new `nonWebMain` intermediate Gradle source set (Room only supports Android/iOS/JVM, not the `js`/`wasmJs` web targets, so web gets a stub in-memory `PlayerRepository` for now), the `PlayerRepository` interface, Room entity/DAO/database classes, Koin wiring (`expect val platformRepositoryModule: Module`), and the `AddPlayerViewModel`/contract files needed to bring Add Player up to the same MVI pattern as Home. That work has been planned but not yet started.
