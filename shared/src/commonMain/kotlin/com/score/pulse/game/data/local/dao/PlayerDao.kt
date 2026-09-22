package com.score.pulse.game.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.score.pulse.game.data.local.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players")
    fun observeAll(): Flow<List<PlayerEntity>>

    @Upsert
    suspend fun upsert(player: PlayerEntity)

    @Query("DELETE FROM players WHERE id = :playerId")
    suspend fun deleteById(playerId: Int)

    @Query("DELETE FROM players")
    suspend fun clearAll()

    @Query("UPDATE players SET score = score + :delta WHERE id = :playerId")
    suspend fun adjustScore(playerId: Int, delta: Int)

    @Query("UPDATE players SET score = 0")
    suspend fun resetScores()

    @Query("UPDATE players SET wins = wins + 1 WHERE id = :playerId")
    suspend fun addWin(playerId: Int)

    @Query("UPDATE players SET losses = losses + 1 WHERE id = :playerId")
    suspend fun addLoss(playerId: Int)
}