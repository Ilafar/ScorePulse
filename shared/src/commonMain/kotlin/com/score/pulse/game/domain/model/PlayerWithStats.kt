package com.score.pulse.game.domain.model

data class PlayerWithStats(
    val player: Player,
    val wins: Int,
    val losses: Int,
    val score: Int,
    val rank: Int
)
