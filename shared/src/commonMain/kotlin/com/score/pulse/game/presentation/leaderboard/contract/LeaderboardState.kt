package com.score.pulse.game.presentation.leaderboard.contract

import com.score.pulse.core.base.UiState
import com.score.pulse.game.domain.model.RankingEntry

data class LeaderboardState(
    val rankings: List<RankingEntry> = emptyList(),
    val isLoading: Boolean = false,
) : UiState {
    val top3: List<RankingEntry> get() = rankings.take(3)
    val rest: List<RankingEntry> get() = rankings.drop(3)
}
