package com.score.pulse.game.domain.repository

import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.error.EmptyResult
import com.score.pulse.game.domain.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayerGameRepository {
    fun observePlayersForActiveGame(): Flow<List<Player>>

    suspend fun adjustPlayerScore(
        playerId: Int, delta: Int
    ): EmptyResult<DataError>

    suspend fun addPlayerWin(
        playerId: Int
    ): EmptyResult<DataError>

    suspend fun addPlayerLose(
        playerId: Int
    ): EmptyResult<DataError>
}
