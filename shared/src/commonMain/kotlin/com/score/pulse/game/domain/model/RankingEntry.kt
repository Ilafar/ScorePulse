package com.score.pulse.game.domain.model

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

fun Player.toRankingEntry(): RankingEntry {
    return RankingEntry(
        rank = rank,
        name = name,
        tag = name,
        emblem = emblem,
        accent = accent,
        wins = wins,
        matches = wins + losses,
        score = score,
    )
}

fun List<Player>.toRankingEntries(): List<RankingEntry> {
    return map { it.toRankingEntry() }
}
