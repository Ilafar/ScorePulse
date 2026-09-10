package com.score.pulse.model

/** One participant's final standing within a completed [MatchRecord]. */
data class MatchParticipant(
    val name: String,
    val emblem: PlayerEmblem,
    val accent: AccentColor,
    val score: Int,
    val rank: Int,
)

/** A completed match summary shown on the History screen. */
data class MatchRecord(
    val id: String,
    val title: String,
    val durationMinutes: Int,
    val dateLabel: String,
    val participants: List<MatchParticipant>,
) {
    val champion: MatchParticipant get() = participants.minBy { it.rank }
}
