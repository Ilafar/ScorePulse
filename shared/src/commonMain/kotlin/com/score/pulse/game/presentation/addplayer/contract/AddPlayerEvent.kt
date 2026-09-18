package com.score.pulse.game.presentation.addplayer.contract

import com.score.pulse.core.base.UiEvent
import com.score.pulse.game.domain.model.AccentColor
import com.score.pulse.game.domain.model.PlayerEmblem

sealed class AddPlayerEvent : UiEvent {
    data class OnNameChange(val name: String) : AddPlayerEvent()
    data class OnEmblemChange(val emblem: PlayerEmblem) : AddPlayerEvent()
    data class OnAccentChange(val accent: AccentColor) : AddPlayerEvent()
    data class PlayerRemoveClick(val playerId: Int) : AddPlayerEvent()

    data object AddPlayerClick : AddPlayerEvent()
    data object ClearAllPlayersClick : AddPlayerEvent()
    data object ClearNameClick : AddPlayerEvent()
}
