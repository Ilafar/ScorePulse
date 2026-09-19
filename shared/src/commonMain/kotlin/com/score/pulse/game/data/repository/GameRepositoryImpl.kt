package com.score.pulse.game.data.repository

import com.score.pulse.core.data.local.safeDbCall
import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.error.EmptyResult
import com.score.pulse.core.domain.error.asEmptyDataResult
import com.score.pulse.game.data.local.dao.GameDao
import com.score.pulse.game.data.mapper.toDomain
import com.score.pulse.game.data.mapper.toEntity
import com.score.pulse.game.domain.model.Game
import com.score.pulse.game.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepositoryImpl(
    private val gameDao: GameDao
) : GameRepository {
    override suspend fun getAllGames(): AppResult<List<Game>, DataError> {
        return safeDbCall {
            gameDao.getAllGames().toDomain()
        }
    }

    override suspend fun startNewActiveGame(game: Game): EmptyResult<DataError> {
        return safeDbCall {
            gameDao.startNewActiveGame(game.toEntity())
        }.asEmptyDataResult()
    }

    override fun observeActiveGame(): Flow<Game?> {
        return gameDao.observeActiveGame().map { it?.toDomain() }
    }

    override suspend fun completeActiveGame(): EmptyResult<DataError> {
        return safeDbCall {
            gameDao.completeActiveGame()
        }.asEmptyDataResult()
    }
}