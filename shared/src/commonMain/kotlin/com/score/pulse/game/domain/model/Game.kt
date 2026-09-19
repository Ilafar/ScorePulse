package com.score.pulse.game.domain.model

data class Game(
    val id: Int = 0,
    val name: String = "",
    val maxRounds: Int = 0,
    val status: GameStatus = GameStatus.COMPLETED,
    val createdAt: Long = 0L
)
