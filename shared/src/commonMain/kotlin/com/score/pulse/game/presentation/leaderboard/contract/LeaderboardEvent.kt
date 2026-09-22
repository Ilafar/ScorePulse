package com.score.pulse.game.presentation.leaderboard.contract

import com.score.pulse.core.base.UiEvent

sealed interface LeaderboardEvent : UiEvent {
    data object StartMatchClicked : LeaderboardEvent
    data object AddPlayerClicked : LeaderboardEvent
}
