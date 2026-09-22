package com.score.pulse.game.presentation.history.contract

import com.score.pulse.core.base.UiEvent

sealed interface HistoryEvent : UiEvent {
    data object LaunchMatchClicked : HistoryEvent
    data object AddPlayerClicked : HistoryEvent
}
