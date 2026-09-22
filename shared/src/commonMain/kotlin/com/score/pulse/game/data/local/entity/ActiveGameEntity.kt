package com.score.pulse.game.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock.System

@Entity(tableName = "active_game")
data class ActiveGameEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val currentRound: Int = 1,
    val maxRounds: Int,
    val createdAt: Long = System.now().toEpochMilliseconds(),
)
