package com.score.pulse.domain.model

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
