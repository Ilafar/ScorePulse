package com.score.pulse.players.domain.model

data class Player(
    val id: Int = 0,
    val name: String,
    val emblem: PlayerEmblem,
    val accent: AccentColor
)
