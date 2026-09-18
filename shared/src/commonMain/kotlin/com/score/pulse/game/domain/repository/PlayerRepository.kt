package com.score.pulse.game.domain.repository

import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.error.EmptyResult
import com.score.pulse.game.domain.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun observePlayers(): Flow<List<Player>>
    suspend fun addPlayer(player: Player): EmptyResult<DataError>
    suspend fun removePlayer(playerId: Int): EmptyResult<DataError>
    suspend fun clearPlayers(): EmptyResult<DataError>
}
