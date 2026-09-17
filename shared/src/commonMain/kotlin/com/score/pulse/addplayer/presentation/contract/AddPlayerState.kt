package com.score.pulse.addplayer.presentation.contract

import com.score.pulse.core.base.UiState
import com.score.pulse.core.domain.model.AccentColor
import com.score.pulse.core.domain.model.Player
import com.score.pulse.core.domain.model.PlayerEmblem
import com.score.pulse.core.presentation.util.UiText

data class AddPlayerState(
    val name: String = "",
    val nameError: UiText? = null,
    val emblem: PlayerEmblem = PlayerEmblem.Gamepad,
    val accent: AccentColor = AccentColor.Emerald,
    val roster: List<Player> = emptyList(),
): UiState
