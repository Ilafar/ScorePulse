package com.score.pulse.game.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.score.pulse.game.data.local.entity.ActiveGameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM active_game WHERE id = 1 LIMIT 1")
    fun observeActiveGame(): Flow<ActiveGameEntity?>

    @Query("SELECT * FROM active_game WHERE id = 1 LIMIT 1")
    suspend fun getActiveGame(): ActiveGameEntity?

    @Upsert
    suspend fun upsertActiveGame(game: ActiveGameEntity)

    @Query("DELETE FROM active_game ")
    suspend fun clearActiveGame()

    @Query("UPDATE active_game SET currentRound = currentRound + 1 WHERE id = 1")
    suspend fun lockRound()
}