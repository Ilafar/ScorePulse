package com.score.pulse.domain.model

data class MatchParticipant(
    val name: String,
    val emblem: PlayerEmblem,
    val accent: AccentColor,
    val score: Int,
    val rank: Int,
)

data class MatchRecord(
    val id: String,
    val title: String,
    val durationMinutes: Int,
    val dateLabel: String,
    val participants: List<MatchParticipant>,
) {
    val champion: MatchParticipant get() = participants.minBy { it.rank }
}
