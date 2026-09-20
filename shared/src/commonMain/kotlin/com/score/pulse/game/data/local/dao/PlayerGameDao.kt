package com.score.pulse.game.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.score.pulse.game.data.local.entity.PlayerEntity
import com.score.pulse.game.data.local.entity.PlayerGameStatsEntity
import kotlinx.coroutines.flow.Flow

private const val ACTIVE_GAME_SUBQUERY = """
    (SELECT id FROM games WHERE status = 'IN_PROGRESS' ORDER BY createdAt DESC LIMIT 1)
"""

data class PlayerWithStatsEntity(
    @Embedded val player: PlayerEntity,
    @Embedded(prefix = "stats_") val stats: PlayerGameStatsEntity
)

@Dao
interface PlayerGameDao {

    @Query(
        """
    SELECT 
        players.id AS id,
        players.name AS name,
        players.emblem AS emblem,
        players.accent AS accent,
        COALESCE(player_game_stats.playerId, players.id) AS stats_playerId,
        COALESCE(
            player_game_stats.gameId, 
            $ACTIVE_GAME_SUBQUERY, 
            0
        ) AS stats_gameId,
        COALESCE(player_game_stats.score, 0) AS stats_score,
        COALESCE(player_game_stats.wins, 0) AS stats_wins,
        COALESCE(player_game_stats.losses, 0) AS stats_losses,
        COALESCE(player_game_stats.rank, 0) AS stats_rank
    FROM players 
    LEFT JOIN player_game_stats 
        ON players.id = player_game_stats.playerId 
       AND player_game_stats.gameId = $ACTIVE_GAME_SUBQUERY
    """
    )
    fun observePlayersForActiveGame(): Flow<List<PlayerWithStatsEntity>>

    @Query("SELECT id FROM games WHERE status = 'IN_PROGRESS' ORDER BY createdAt DESC LIMIT 1")
    suspend fun getActiveGameId(): Int?

    @Query("SELECT * FROM player_game_stats WHERE playerId = :playerId AND gameId = $ACTIVE_GAME_SUBQUERY")
    suspend fun getPlayerGameStats(playerId: Int): PlayerGameStatsEntity?

    @Upsert
    suspend fun upsertPlayerGameStats(stats: PlayerGameStatsEntity)

    @Query(
        """
        UPDATE player_game_stats 
        SET score = 0 
        WHERE gameId = $ACTIVE_GAME_SUBQUERY
        """
    )
    suspend fun resetScoresForActiveGame()

    @Transaction
    suspend fun adjustPlayerScore(playerId: Int, delta: Int) {
        val activeGameId = getActiveGameId() ?: return
        val currentStats = getPlayerGameStats(playerId)
        if (currentStats != null) {
            upsertPlayerGameStats(currentStats.copy(score = currentStats.score + delta))
        } else {
            upsertPlayerGameStats(
                PlayerGameStatsEntity(
                    playerId = playerId,
                    gameId = activeGameId,
                    score = delta,
                    wins = 0,
                    losses = 0,
                    rank = 0
                )
            )
        }
    }

    @Transaction
    suspend fun addPlayerWin(playerId: Int) {
        val activeGameId = getActiveGameId() ?: return
        val currentStats = getPlayerGameStats(playerId)
        if (currentStats != null) {
            upsertPlayerGameStats(currentStats.copy(wins = currentStats.wins + 1))
        } else {
            upsertPlayerGameStats(
                PlayerGameStatsEntity(
                    playerId = playerId,
                    gameId = activeGameId,
                    score = 0,
                    wins = 1,
                    losses = 0,
                    rank = 0
                )
            )
        }
    }

    @Transaction
    suspend fun addPlayerLose(playerId: Int) {
        val activeGameId = getActiveGameId() ?: return
        val currentStats = getPlayerGameStats(playerId)
        if (currentStats != null) {
            upsertPlayerGameStats(currentStats.copy(losses = currentStats.losses + 1))
        } else {
            upsertPlayerGameStats(
                PlayerGameStatsEntity(
                    playerId = playerId,
                    gameId = activeGameId,
                    score = 0,
                    wins = 0,
                    losses = 1,
                    rank = 0
                )
            )
        }
    }
}
