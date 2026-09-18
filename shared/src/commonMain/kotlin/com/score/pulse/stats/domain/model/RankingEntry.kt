package com.score.pulse.stats.domain.model

import com.score.pulse.players.domain.model.AccentColor
import com.score.pulse.players.domain.model.PlayerEmblem

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
