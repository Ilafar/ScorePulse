package com.score.pulse.game.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.score.pulse.game.data.local.entity.GameEntity
import com.score.pulse.game.domain.model.GameStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Upsert
    suspend fun upsertGame(game: GameEntity): Long

    @Query("SELECT * FROM games WHERE status = 'IN_PROGRESS' LIMIT 1")
    fun observeActiveGame(): Flow<GameEntity?>

    @Query("SELECT * FROM games ORDER BY createdAt DESC")
    suspend fun getAllGames(): List<GameEntity>

    @Query("UPDATE games SET status = 'COMPLETED' WHERE status = 'IN_PROGRESS'")
    suspend fun completeActiveGame()

    @Transaction
    suspend fun startNewActiveGame(game: GameEntity): Long {
        deleteActiveGame()
        return upsertGame(game.copy(status = GameStatus.IN_PROGRESS))
    }

    @Query("DELETE FROM games WHERE status = 'IN_PROGRESS'")
    suspend fun deleteActiveGame()
}