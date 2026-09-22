package com.score.pulse.game.domain.model

data class Player(
    val id: Int = 0,
    val name: String,
    val emblem: PlayerEmblem,
    val accent: AccentColor,
    val score: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val rank: Int = 0,
)

fun List<Player>.calculateRanks(): List<Player> {
    return sortedByDescending { it.score }
        .mapIndexed { index, player -> player.copy(rank = index + 1) }
}
