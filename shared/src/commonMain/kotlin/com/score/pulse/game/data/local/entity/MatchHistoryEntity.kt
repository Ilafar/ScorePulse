package com.score.pulse.game.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock.System

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val durationMinutes: Int,
    val createdAt: Long = System.now().toEpochMilliseconds(),
    val participantsSnapshot: String,
)
