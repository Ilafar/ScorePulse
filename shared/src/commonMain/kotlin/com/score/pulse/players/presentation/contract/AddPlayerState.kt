package com.score.pulse.players.presentation.contract

import com.score.pulse.core.base.UiState
import com.score.pulse.core.presentation.util.UiText
import com.score.pulse.players.domain.model.AccentColor
import com.score.pulse.players.domain.model.Player
import com.score.pulse.players.domain.model.PlayerEmblem

data class AddPlayerState(
    val name: String = "",
    val nameError: UiText? = null,
    val emblem: PlayerEmblem = PlayerEmblem.Gamepad,
    val accent: AccentColor = AccentColor.Emerald,
    val roster: List<Player> = emptyList(),
) : UiState
