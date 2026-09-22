package com.score.pulse.game.presentation.leaderboard.viewmodel

import androidx.lifecycle.viewModelScope
import com.score.pulse.core.base.BaseViewModel
import com.score.pulse.game.domain.usecase.observe.ObserveLeaderboardUseCase
import com.score.pulse.game.presentation.leaderboard.contract.LeaderboardEffect
import com.score.pulse.game.presentation.leaderboard.contract.LeaderboardEvent
import com.score.pulse.game.presentation.leaderboard.contract.LeaderboardState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LeaderboardViewModel(
    private val observeLeaderboardUseCase: ObserveLeaderboardUseCase
) : BaseViewModel<LeaderboardEvent, LeaderboardState, LeaderboardEffect>() {

    init {
        observeLeaderboard()
    }

    override fun createInitialState(): LeaderboardState = LeaderboardState()

    override fun handleEvent(event: LeaderboardEvent) {
        when (event) {
            LeaderboardEvent.StartMatchClicked -> {
                setEffect { LeaderboardEffect.NavigateToMatch }
            }
            LeaderboardEvent.AddPlayerClicked -> {
                setEffect { LeaderboardEffect.NavigateToAddPlayer }
            }
        }
    }

    private fun observeLeaderboard() {
        observeLeaderboardUseCase()
            .catch { sendSnackbar("Failed to fetch leaderboard") }
            .onEach { rankings ->
                setState { copy(rankings = rankings) }
            }
            .launchIn(viewModelScope)
    }
}
