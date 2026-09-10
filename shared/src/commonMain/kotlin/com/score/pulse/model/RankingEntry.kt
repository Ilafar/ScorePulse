package com.score.pulse.model

/** One row of the global standings shown on the Leaderboard screen. */
data class RankingEntry(
    val rank: Int,
    val name: String,
    val tag: String,
    val emblem: PlayerEmblem,
    val accent: AccentColor,
    val wins: Int,
    val matches: Int,
    val score: Int,
)
