package com.score.pulse.game.domain.model

data class Game(
    val id: Int = 1,
    val name: String = "",
    val currentRound: Int = 1,
    val maxRounds: Int = 0,
    val createdAt: Long = 0L,
)

fun Game.calculateDurationMinutes(endTimeMillis: Long): Int {
    if (createdAt <= 0) return 1
    return ((endTimeMillis - createdAt) / 60000)
        .toInt()
        .coerceAtLeast(1)
}