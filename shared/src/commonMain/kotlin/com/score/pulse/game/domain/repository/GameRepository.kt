package com.score.pulse.game.domain.repository

import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.error.EmptyResult
import com.score.pulse.game.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun observeActiveGame(): Flow<Game?>
    suspend fun getAllGames(): AppResult<List<Game>, DataError>
    suspend fun startNewActiveGame(game: Game): EmptyResult<DataError>
    suspend fun completeActiveGame(): EmptyResult<DataError>
}