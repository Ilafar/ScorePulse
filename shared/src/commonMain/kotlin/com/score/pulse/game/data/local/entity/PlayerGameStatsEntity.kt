package com.score.pulse.game.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "player_game_stats",
    primaryKeys = ["playerId", "gameId"],
    foreignKeys = [
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("playerId"),
        Index("gameId")
    ]
)
data class PlayerGameStatsEntity(
    val playerId: Int,
    val gameId: Int,
    val score: Int,
    val wins: Int,
    val losses: Int,
    val rank: Int
)