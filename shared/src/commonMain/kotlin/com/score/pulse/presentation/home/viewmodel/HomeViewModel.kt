package com.score.pulse.presentation.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.score.pulse.core.base.BaseViewModel
import com.score.pulse.core.snackbar.SnackbarController
import com.score.pulse.core.snackbar.SnackbarEvent
import com.score.pulse.domain.model.Game
import com.score.pulse.domain.model.Player
import com.score.pulse.presentation.home.contract.HomeEffect
import com.score.pulse.presentation.home.contract.HomeEvent
import com.score.pulse.presentation.home.contract.HomeState
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel<HomeEvent, HomeState, HomeEffect>() {

    override fun createInitialState(): HomeState = HomeState()

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.AdjustScoreClicked -> {
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

            is HomeEvent.OpenEditScoreDialog -> {
                setState { copy(editingPlayerId = event.playerId) }
            }

            HomeEvent.DismissEdit -> {
                setState { copy(editingPlayerId = null) }
            }

            HomeEvent.OpenGameResultDialog -> {
                setState { copy(isGameResultVisible = true) }
            }

            HomeEvent.OpenLockRoundDialog -> {
                setState { copy(isLockRoundConfirmationVisible = true) }
            }

            HomeEvent.ConfirmLockRoundClicked -> {
                setState { copy(isLockRoundConfirmationVisible = false) }
            }

            HomeEvent.DismissLockRound -> {
                setState { copy(isLockRoundConfirmationVisible = false) }
            }

            HomeEvent.OpenStartGameDialog -> {
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

            HomeEvent.DismissStartGame -> {
                setState { copy(isStartGameDialogVisible = false) }
            }

            is HomeEvent.StartNewGameClicked -> {
                setState {
                    copy(
                        isStartGameDialogVisible = false,
                        game = Game(
                            name = event.gameName,
                            maxRounds = event.totalRounds
                        ),
                        currentRound = 1,
                        players = players.map { it.copy(score = 0) },
                    )
                }
                sendSnackbar("Game started")
            }

            HomeEvent.AddPlayerClicked -> {
                setEffect { HomeEffect.NavigateToAddPlayer }
            }
        }
    }

    private fun sendSnackbar(message: String) {
        viewModelScope.launch {
            SnackbarController.sendEvent(SnackbarEvent(message))
        }
    }

    private fun adjustPlayerPt(
        players: List<Player>,
        editingPlayerId: String,
        delta: Int
    ): List<Player> {
        return players.map {
            if (it.id == editingPlayerId)
                it.copy(score = (it.score + delta).coerceAtLeast(0))
            else it
        }
    }
}