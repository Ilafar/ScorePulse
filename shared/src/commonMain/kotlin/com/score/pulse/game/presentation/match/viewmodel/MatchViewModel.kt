package com.score.pulse.game.presentation.match.viewmodel

import androidx.lifecycle.viewModelScope
import com.score.pulse.core.base.BaseViewModel
import com.score.pulse.core.presentation.util.toUiText
import com.score.pulse.game.domain.model.Game
import com.score.pulse.game.domain.usecase.add.AddNewGameUseCase
import com.score.pulse.game.domain.usecase.add.AdjustScoreUseCase
import com.score.pulse.game.domain.usecase.add.LockRoundUseCase
import com.score.pulse.game.domain.usecase.complete.CompleteActiveGameUseCase
import com.score.pulse.game.domain.usecase.observe.ObserveActiveGameUseCase
import com.score.pulse.game.domain.usecase.observe.ObservePlayersWithStatsUseCase
import com.score.pulse.game.presentation.match.contract.MatchEffect
import com.score.pulse.game.presentation.match.contract.MatchEvent
import com.score.pulse.game.presentation.match.contract.MatchState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MatchViewModel(
    private val observePlayersWithStatsUseCase: ObservePlayersWithStatsUseCase,
    private val observeActiveGameUseCase: ObserveActiveGameUseCase,
    private val lockRoundUseCase: LockRoundUseCase,
    private val completeActiveGameUseCase: CompleteActiveGameUseCase,
    private val addNewGameUseCase: AddNewGameUseCase,
    private val adjustScoreUseCase: AdjustScoreUseCase
) : BaseViewModel<MatchEvent, MatchState, MatchEffect>() {

    init {
        observePlayers()
        observeActiveGame()
    }

    override fun createInitialState(): MatchState = MatchState()

    override fun handleEvent(event: MatchEvent) {
        when (event) {
            is MatchEvent.AdjustScoreClicked -> {
                adjustPlayerScore(
                    delta = event.delta,
                    playerId = event.playerId
                )
            }

            is MatchEvent.OpenEditScoreDialog -> {
                setState { copy(editingPlayerId = event.playerId) }
            }

            MatchEvent.DismissEdit -> {
                setState { copy(editingPlayerId = null) }
            }

            MatchEvent.OpenGameResultDialog -> {
                setState { copy(isGameResultVisible = true) }
            }

            MatchEvent.OpenLockRoundDialog -> {
                setState { copy(isLockRoundConfirmationVisible = true) }
            }

            is MatchEvent.ConfirmLockRoundClicked -> {
                setState { copy(isLockRoundConfirmationVisible = false) }
                lockRound(resetScores = event.resetScores)
            }

            MatchEvent.DismissLockRound -> {
                setState { copy(isLockRoundConfirmationVisible = false) }
            }

            MatchEvent.OpenStartGameDialog -> {
                if (currentState.hasMinimumPlayers) {
                    setState {
                        copy(
                            isGameResultVisible = false,
                            isStartGameDialogVisible = true
                        )
                    }
                } else {
                    sendSnackbar("Minimum 2 players required")
                }
            }

            MatchEvent.DismissStartGame -> {
                setState { copy(isStartGameDialogVisible = false) }
            }

            is MatchEvent.StartNewGameClicked -> addNewActiveGame(
                name = event.gameName,
                maxRounds = event.totalRounds
            )

            MatchEvent.AddPlayerClicked -> {
                setEffect { MatchEffect.NavigateToAddPlayer }
            }

            MatchEvent.FinishGameClicked -> {
                completeActiveGame()
            }
        }
    }

    private fun adjustPlayerScore(
        delta: Int,
        playerId: Int
    ) {
        launchWithResult(
            block = {
                adjustScoreUseCase(
                    AdjustScoreUseCase.Params(
                        playerId = playerId,
                        delta = delta
                    )
                )
            },
            onSuccess = {},
            onError = { error -> sendSnackbar(error.toUiText()) }
        )
    }

    private fun lockRound(resetScores: Boolean) {
        launchWithResult(
            block = {
                lockRoundUseCase(LockRoundUseCase.Params(resetScores = resetScores))
            },
            onSuccess = { sendSnackbar("Round locked successfully") },
            onError = { error -> sendSnackbar(error.toUiText()) }
        )
    }

    private fun completeActiveGame() {
        launchWithResult(
            block = {
                completeActiveGameUseCase()
            },
            onSuccess = {
                setState { copy(isGameResultVisible = true) }
                sendSnackbar("Game completed!")
            },
            onError = { error -> sendSnackbar(error.toUiText()) }
        )
    }

    private fun addNewActiveGame(
        name: String,
        maxRounds: String
    ) {
        launchWithResult(
            block = {
                addNewGameUseCase(
                    Game(
                        name = name,
                        maxRounds = maxRounds.toIntOrNull() ?: 0
                    )
                )
            },
            onSuccess = {
                setState {
                    copy(isStartGameDialogVisible = false)
                }
                sendSnackbar("Game created successfully")
            },
            onError = { error -> sendSnackbar(error.toUiText()) }
        )

    }

    private fun observePlayers() {
        observePlayersWithStatsUseCase()
            .catch { sendSnackbar("Failed to observe players") }
            .onEach { roster ->
                setState { copy(players = roster) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeActiveGame() {
        observeActiveGameUseCase()
            .catch {
                sendSnackbar("Failed to observe active game $it")
            }
            .onEach { game ->
                val activeGame = game ?: Game()
                setState {
                    copy(game = activeGame)
                }
            }
            .launchIn(viewModelScope)
    }
}
