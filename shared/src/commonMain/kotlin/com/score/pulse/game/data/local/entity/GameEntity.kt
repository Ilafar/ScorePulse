package com.score.pulse.game.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.score.pulse.game.domain.model.GameStatus
import kotlin.time.Clock.System

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val maxRounds: Int,
    val status: GameStatus,
    val createdAt: Long = System.now().toEpochMilliseconds()
)