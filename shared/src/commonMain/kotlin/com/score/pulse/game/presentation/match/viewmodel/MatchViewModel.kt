package com.score.pulse.game.presentation.match.viewmodel

import androidx.lifecycle.viewModelScope
import com.score.pulse.core.base.BaseViewModel
import com.score.pulse.core.presentation.util.toUiText
import com.score.pulse.game.domain.model.Game
import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.usecase.add.AddNewGameUseCase
import com.score.pulse.game.domain.usecase.observe.ObserveActiveGameUseCase
import com.score.pulse.game.domain.usecase.observe.ObservePlayersUseCase
import com.score.pulse.game.presentation.match.contract.MatchEffect
import com.score.pulse.game.presentation.match.contract.MatchEvent
import com.score.pulse.game.presentation.match.contract.MatchState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MatchViewModel(
    private val observePlayersUseCase: ObservePlayersUseCase,
    private val observeActiveGameUseCase: ObserveActiveGameUseCase,
    private val addNewGameUseCase: AddNewGameUseCase
) : BaseViewModel<MatchEvent, MatchState, MatchEffect>() {

    init {
        observePlayers()
        observeActiveGame()
    }

    override fun createInitialState(): MatchState = MatchState()

    override fun handleEvent(event: MatchEvent) {
        when (event) {
            is MatchEvent.AdjustScoreClicked -> {
                setState {
                    copy(
                        players = adjustPlayerPt(
                            players = players,
                            editingPlayerId = event.playerId,
                            delta = event.delta
                        )
                    )
                }
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

            MatchEvent.ConfirmLockRoundClicked -> {
                setState { copy(isLockRoundConfirmationVisible = false) }
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
        }
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
                        maxRounds = maxRounds.toIntOrNull()?:0
                    )
                )
            },
            onSuccess = {
                setState {
                    copy(isStartGameDialogVisible = false,)
                }
                sendSnackbar("Game created successfully")
            },
            onError = { error -> sendSnackbar(error.toUiText()) }
        )

    }

    private fun observePlayers() {
        observePlayersUseCase()
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
                    copy(
                        game = activeGame,
                        currentRound = 1,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun adjustPlayerPt(
        players: List<Player>,
        editingPlayerId: Int,
        delta: Int
    ): List<Player> {
        return players.map {
            if (it.id == editingPlayerId)
                it.copy()
            else it
        }
    }
}
