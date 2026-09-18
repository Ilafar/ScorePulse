package com.score.pulse.game.presentation.match.viewmodel

import androidx.lifecycle.viewModelScope
import com.score.pulse.core.base.BaseViewModel
import com.score.pulse.game.domain.model.Game
import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.usecase.ObservePlayersUseCase
import com.score.pulse.game.presentation.match.contract.MatchEffect
import com.score.pulse.game.presentation.match.contract.MatchEvent
import com.score.pulse.game.presentation.match.contract.MatchState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MatchViewModel(
    private val observePlayersUseCase: ObservePlayersUseCase
) : BaseViewModel<MatchEvent, MatchState, MatchEffect>() {

    init {
        observePlayers()
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

            is MatchEvent.StartNewGameClicked -> {
                setState {
                    copy(
                        isStartGameDialogVisible = false,
                        game = Game(
                            name = event.gameName,
                            maxRounds = event.totalRounds
                        ),
                        currentRound = 1,
                        players = players.map { it },
                    )
                }
                sendSnackbar("Game started")
            }

            MatchEvent.AddPlayerClicked -> {
                setEffect { MatchEffect.NavigateToAddPlayer }
            }
        }
    }

    private fun observePlayers() {
        observePlayersUseCase()
            .catch { sendSnackbar("Failed to observe players") }
            .onEach { roster ->
                setState { copy(players = roster) }
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
