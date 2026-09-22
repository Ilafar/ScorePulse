package com.score.pulse.game.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val emblem: String,
    val accent: String,
    val score: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val rank: Int = 0,
)