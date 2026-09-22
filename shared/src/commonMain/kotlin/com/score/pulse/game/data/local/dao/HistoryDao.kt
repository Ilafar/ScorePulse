package com.score.pulse.game.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.score.pulse.game.data.local.entity.MatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM match_history ORDER BY createdAt DESC")
    fun observeMatchHistory(): Flow<List<MatchHistoryEntity>>

    @Query("SELECT * FROM match_history ORDER BY createdAt DESC")
    suspend fun getAllMatchHistory(): List<MatchHistoryEntity>

    @Upsert
    suspend fun upsertMatchHistory(match: MatchHistoryEntity): Long

    @Query("DELETE FROM match_history")
    suspend fun clearMatchHistory()
}
