package com.score.pulse.players.presentation.contract

import com.score.pulse.core.base.UiEvent
import com.score.pulse.players.domain.model.AccentColor
import com.score.pulse.players.domain.model.PlayerEmblem

sealed class AddPlayerEvent : UiEvent {
    data class OnNameChange(val name: String) : AddPlayerEvent()
    data class OnEmblemChange(val emblem: PlayerEmblem) : AddPlayerEvent()
    data class OnAccentChange(val accent: AccentColor) : AddPlayerEvent()

    data object AddPlayerClick : AddPlayerEvent()
    data object ClearHistoryClick : AddPlayerEvent()
    data object PlayerRemoveClick : AddPlayerEvent()
    data object ClearNameClick : AddPlayerEvent()
}
