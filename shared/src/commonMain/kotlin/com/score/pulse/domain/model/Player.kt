package com.score.pulse.domain.model

data class Player(
    val id: String,
    val name: String,
    val emblem: PlayerEmblem,
    val accent: AccentColor,
    val score: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
)
