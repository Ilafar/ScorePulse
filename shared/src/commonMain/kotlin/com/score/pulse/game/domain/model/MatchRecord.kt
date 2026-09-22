package com.score.pulse.game.domain.model


data class MatchRecord(
    val id: String,
    val title: String,
    val durationMinutes: Int,
    val createdAt: Long,
    val participants: List<Player>,
) {
    val champion: Player? get() = participants.minByOrNull { it.rank } ?: participants.firstOrNull()
}
