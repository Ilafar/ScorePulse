package com.score.pulse.core.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
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
}
