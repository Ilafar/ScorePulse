package com.score.pulse.game.presentation.history.contract

import com.score.pulse.core.base.UiEffect

sealed interface HistoryEffect : UiEffect {
    data object NavigateToMatch : HistoryEffect
    data object NavigateToAddPlayer : HistoryEffect
}
