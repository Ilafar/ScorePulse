package com.score.pulse.game.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Upsert
import com.score.pulse.game.data.local.entity.PlayerEntity
import com.score.pulse.game.data.local.entity.PlayerGameStatsEntity
import kotlinx.coroutines.flow.Flow

data class PlayerGameStats(
    @Embedded val player: PlayerEntity,
    @Embedded val stats: PlayerGameStatsEntity
)

@Dao
interface PlayerGameDao {

    @Upsert
    suspend fun upsertPlayerStats(playerGameStats: PlayerGameStatsEntity)

    @Query("""
        SELECT * FROM players 
        INNER JOIN player_game_stats ON players.id = player_game_stats.playerId
        WHERE player_game_stats.gameId = :gameId
    """)
    fun observePlayersForGame(gameId: Long): Flow<List<PlayerGameStats>>

    @Query("""
        UPDATE player_game_stats 
        SET wins = wins + :addWin, losses = losses + :addLoss, score = score + :addScore 
        WHERE playerId = :playerId AND gameId = :gameId
    """)
    suspend fun updateStats(playerId: Long, gameId: Long, addWin: Int, addLoss: Int, addScore: Int)

}