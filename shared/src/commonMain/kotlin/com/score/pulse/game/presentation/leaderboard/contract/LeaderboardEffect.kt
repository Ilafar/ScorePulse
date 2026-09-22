package com.score.pulse.game.presentation.leaderboard.contract

import com.score.pulse.core.base.UiEffect

sealed interface LeaderboardEffect : UiEffect {
    data object NavigateToMatch : LeaderboardEffect
    data object NavigateToAddPlayer : LeaderboardEffect
}
