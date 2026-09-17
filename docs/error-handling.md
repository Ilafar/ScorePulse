Here's the layout that fits your existing Result / Error / DataError / UiText pieces. I'll describe boundaries and give small snippets — not full implementations — since you asked for guidance.

1. Where each error type lives

DataSource   → throws real exceptions (SQLDelight/Room/Ktor) — never returns Result
Repository   → catches exceptions, returns Result<T, DataError.Local | .Network>
UseCase      → returns Result<T, DomainError>  (may re-map / add domain rules)
ViewModel    → consumes Result, maps error → UiText, keeps original for logging
UI           → only sees UiText via state; never sees DataError

Rule of thumb: Result starts at the repository and dies in the ViewModel. Layers above the repo never see raw exceptions; layers below never see Result.

2. The safeCall helper (repository layer)

Put it in domain/error/ (or data/util/):

// commonMain
suspend inline fun <T> safeDbCall(
crossinline block: suspend () -> T
): Result<T, DataError.Local> = try {
Result.Success(block())
} catch (e: CancellationException) {
throw e                                    // never swallow cancellation
} catch (e: Throwable) {
Logger.e("DB", e)                          // original stays in logcat
Result.Error(e.toLocalError())             // enum for the domain
}

fun Throwable.toLocalError(): DataError.Local = when {
message?.contains("disk", true) == true -> DataError.Local.DISK_FULL
else -> DataError.Local.UNKNOWN
}

For Flows: wrap with .catch { emit(Result.Error(...)) } and map inside map { Result.Success(it) }, or write a safeFlow {} builder that does the same.

Then PlayerRepositoryImpl becomes trivial:

override suspend fun addPlayer(player: Player): EmptyResult<DataError.Local> =
safeDbCall { dao.insert(player.toEntity()) }.asEmptyDataResult()

Change your interface to return Result — right now addPlayer returns Unit, which forces the VM to guess whether it worked:

suspend fun addPlayer(player: Player): EmptyResult<DataError.Local>
fun getPlayers(): Flow<Result<List<Player>, DataError.Local>>

3. UseCase

Your BaseUseCase is incomplete (Result<> with no params). Two options — pick one and stay consistent:

- Drop BaseUseCase. Each use case is just a class with suspend operator fun invoke(...): Result<T, E>. Simple, no generics gymnastics.
- Keep it generic:
  abstract class BaseUseCase<in P, out T, E : Error> {
  abstract suspend operator fun invoke(params: P): Result<T, E>
  }

Use cases mostly forward the repo's Result. Only remap when the domain adds rules (e.g. PlayerAlreadyExists becomes a domain error, not a data error).

4. launchSafely in the ViewModel

You don't need try/catch for Result-returning calls (they can't throw for domain reasons). What you do need is a helper that:

- runs on viewModelScope,
- routes Result.Error → UiText via a mapper,
- lets Result.Success flow to a handler,
- catches truly unexpected throwables as a last resort.

// presentation/common/
fun <T, E : Error> CoroutineScope.launchWithResult(
block: suspend () -> Result<T, E>,
onSuccess: (T) -> Unit,
onError: (E) -> Unit,          // VM decides UiText here
) = launch {
block()
.onSuccess(onSuccess)
.onError(onError)
}

In AddPlayerViewModel:

private fun save(player: Player) = viewModelScope.launchWithResult(
block   = { addPlayerUseCase(player) },
onSuccess = { sendEffect(AddPlayerEffect.Saved) },
onError = { err ->
_state.update { it.copy(error = err.toUiText()) }
},
)

private fun DataError.Local.toUiText(): UiText = when (this) {
DataError.Local.DISK_FULL -> UiText.StringResourceId(Res.string.err_disk_full)
DataError.Local.UNKNOWN   -> UiText.StringResourceId(Res.string.err_unknown)
}

Put the enum→UiText mapper in presentation/ (per-feature or a shared ErrorMessages.kt). Never let DataError cross into @Composable code directly.

5. Logging

Do it once, at the throw site (inside safeDbCall). Don't re-log in the VM — that produces duplicate stack traces and the enum has already lost the exception anyway. If you need the exception in the VM for crash reporting, extend DataError.Local to a data class carrying an optional cause: Throwable? — but the enum-only form is fine and lighter.

Recommended order to implement

1. Add safeDbCall + Throwable.toLocalError().
2. Change PlayerRepository methods to return Result / Flow<Result<..>>.
3. Fix BaseUseCase (or delete it) and update AddPlayerUseCase.
4. Add launchWithResult extension.
5. In AddPlayerViewModel, add a DataError.Local.toUiText() mapper and wire the state's error: UiText? field.
6. Extend DataError with Network / Validation sub-enums as those cases arrive — don't preemptively.

Want me to implement any of these steps? I'd start with #1–#3 in one pass so the repo compiles end-to-end.

✻ Worked for 41s

❯ if in feature I would connect remote api and fetch data from there too will I need to change this logic or this flow is correct for remote fetch too

The flow is correct — it was designed for exactly this. You only add, you don't change.

What stays the same

- Result<T, E> sealed interface
- launchWithResult in the VM
- UiText mapping in the VM
- Repository returns Result, never throws
- UseCase forwards Result

What you add

1. New error branch in DataError:

sealed interface DataError : Error {
enum class Local : DataError { DISK_FULL, UNKNOWN }

    enum class Network : DataError {
        NO_INTERNET,
        REQUEST_TIMEOUT,
        UNAUTHORIZED,        // 401
        FORBIDDEN,           // 403
        NOT_FOUND,           // 404
        TOO_MANY_REQUESTS,   // 429
        SERVER_ERROR,        // 5xx
        SERIALIZATION,       // JSON parse fail
        UNKNOWN,
    }
}

2. A sibling safeApiCall helper:

suspend inline fun <T> safeApiCall(
crossinline block: suspend () -> T,
): Result<T, DataError.Network> = try {
Result.Success(block())
} catch (e: CancellationException) {
throw e
} catch (e: Throwable) {
Logger.e("API", e)
Result.Error(e.toNetworkError())
}

fun Throwable.toNetworkError(): DataError.Network = when (this) {
is IOException             -> DataError.Network.NO_INTERNET
is HttpRequestTimeoutException -> DataError.Network.REQUEST_TIMEOUT
is ClientRequestException  -> when (response.status.value) {
401 -> DataError.Network.UNAUTHORIZED
403 -> DataError.Network.FORBIDDEN
404 -> DataError.Network.NOT_FOUND
429 -> DataError.Network.TOO_MANY_REQUESTS
else -> DataError.Network.UNKNOWN
}
is ServerResponseException -> DataError.Network.SERVER_ERROR
is SerializationException  -> DataError.Network.SERIALIZATION
else -> DataError.Network.UNKNOWN
}

Exact exception types depend on your HTTP client — the pattern is the same for Ktor / Retrofit / anything else.

Repository shape for mixed local + remote

Two common patterns — pick per feature:

A. Remote-only fetch (e.g. leaderboard):
override suspend fun getLeaderboard(): Result<List<Entry>, DataError.Network> =
safeApiCall { api.leaderboard().toDomain() }

B. Remote-first, cache locally (offline-first):
override fun observePlayers(): Flow<Result<List<Player>, DataError>> = flow {
dao.observeAll()                             // Flow<List<PlayerEntity>>
.map { Result.Success(it.toDomain()) as Result<_, DataError> }
.catch { emit(Result.Error(it.toLocalError())) }
.collect(::emit)

    safeApiCall { api.players() }
        .onSuccess { dao.upsertAll(it.toEntities()) }   // updates the Flow above
        .onError { /* already logged; UI keeps showing cached data */ }
}

The Flow's error type widens from DataError.Local to DataError (its parent) so both branches fit. That's the whole reason DataError is a sealed interface — you can widen without losing exhaustiveness.

The one thing to be careful about

Your toUiText() mapper in the VM must handle both enums now:

private fun DataError.toUiText(): UiText = when (this) {
is DataError.Local   -> when (this) { … }
is DataError.Network -> when (this) {
DataError.Network.NO_INTERNET -> UiText.StringResourceId(Res.string.err_offline)
DataError.Network.UNAUTHORIZED -> UiText.StringResourceId(Res.string.err_session_expired)
// …
}
}

when on a sealed interface is exhaustive, so the compiler will nag you when you add a new error variant later — that's the whole point.

Bonus: retries belong in safeApiCall, not the VM

If you want retry-with-backoff for transient failures, wrap the block inside safeApiCall (Ktor has HttpRequestRetry, or roll a simple retry(times = 3) { block() }). Do not put retry logic in the ViewModel — it belongs to the data layer, and doing it there keeps the VM stateless about network mechanics.

So: yes, same architecture holds. Add Network to DataError, add safeApiCall, extend the toUiText mapper. Nothing above the repository changes.

✻ Sautéed for 19s

❯ is it good to use flow in observePlayers

Yes — but the pattern I showed earlier is actually a bit muddled. Let me correct it.

Flow is right for the local read

For anything backed by Room/SQLDelight, Flow is the correct return type. The DB emits on every write automatically, so the UI reactively updates without you wiring anything. That part is not up for debate — use it.

What I got wrong: mixing remote fetch into the same Flow

Doing dao.observe() + api.fetch() inside one flow { } builder mixes two very different things:

- Observation — continuous, cancellation-safe, cold, emits many times.
- Refresh — one-shot, needs to be triggerable (pull-to-refresh, screen open, retry button), can fail independently of what's cached.

Cramming both into one Flow means:
- You can't retry the network call without resubscribing the whole Flow (which loses UI state).
- A network failure emits Result.Error even though cached data is still valid and on screen.
- You can't tell the UI "showing cached data + refresh failed" — you get one or the other.

The better shape: Single Source of Truth (SSOT)

DB is the source of truth. UI observes DB. Refresh is a separate action that writes to the DB.

interface PlayerRepository {
// Continuous — UI subscribes once.
fun observePlayers(): Flow<List<Player>>

    // One-shot — call on screen enter, pull-to-refresh, retry.
    suspend fun refreshPlayers(): EmptyResult<DataError.Network>
}

class PlayerRepositoryImpl(
private val dao: PlayerDao,
private val api: PlayerApi,
) : PlayerRepository {

    override fun observePlayers(): Flow<List<Player>> =
        dao.observeAll().map { it.toDomain() }

    override suspend fun refreshPlayers(): EmptyResult<DataError.Network> =
        safeApiCall { api.players() }
            .onSuccess { dao.upsertAll(it.toEntities()) }
            .asEmptyDataResult()
}

Note observePlayers() no longer returns Result — a DB read that's cached in memory basically doesn't fail in practice, and if it does you have bigger problems than a UI error message. If you insist on safety, wrap it with .catch { } but return Result<List<Player>, DataError.Local> consistently and don't widen to DataError.

In the ViewModel

init {
repo.observePlayers()
.onEach { players -> _state.update { it.copy(players = players) } }
.launchIn(viewModelScope)

    refresh()   // initial fetch
}

fun refresh() = viewModelScope.launchWithResult(
block = { repo.refreshPlayers() },
onSuccess = { /* Flow already updated the list */ },
onError = { err -> _state.update { it.copy(refreshError = err.toUiText()) } },
)

Now the UI can show cached data + a "refresh failed" snackbar simultaneously, retry independently, and observation never stops because the network hiccuped.

When to skip Flow

- Pure remote, no cache (e.g. one-shot leaderboard fetch): just return suspend fun getLeaderboard(): Result<..., DataError.Network>. Don't invent a Flow for a single value.
- Never re-observed data (e.g. "app version from remote config, read once at boot"): plain suspend.

Rule: Flow when the source pushes changes to you (DB, WebSocket, DataStore). Suspend when you pull a value once.

TL;DR

Flow for observePlayers: yes. But split it — observe from local (Flow, no Result), refresh from remote (suspend, returns Result). Don't try to unify them into one Flow that does both.
