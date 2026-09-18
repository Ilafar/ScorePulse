package com.score.pulse.match.presentation.viewmodel

import com.score.pulse.core.base.BaseViewModel
import com.score.pulse.match.domain.model.Game
import com.score.pulse.match.presentation.contract.MatchEffect
import com.score.pulse.match.presentation.contract.MatchEvent
import com.score.pulse.match.presentation.contract.MatchState
import com.score.pulse.players.domain.model.Player

class MatchViewModel : BaseViewModel<MatchEvent, MatchState, MatchEffect>() {

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
