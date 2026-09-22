package com.score.pulse.game.data.repository

import com.score.pulse.core.data.local.safeDbCall
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.error.EmptyResult
import com.score.pulse.core.domain.error.asEmptyDataResult
import com.score.pulse.game.data.local.dao.PlayerDao
import com.score.pulse.game.data.mapper.toDomain
import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.repository.PlayerGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerGameRepositoryImpl(
    private val playerDao: PlayerDao
) : PlayerGameRepository {
    override fun observePlayersForActiveGame(): Flow<List<Player>> =
        playerDao.observeAll().map { entities -> entities.toDomain() }

    override suspend fun adjustPlayerScore(
        playerId: Int,
        delta: Int
    ): EmptyResult<DataError> {
        return safeDbCall {
            playerDao.adjustScore(playerId, delta)
        }.asEmptyDataResult()
    }

    override suspend fun addPlayerWin(
        playerId: Int,
    ): EmptyResult<DataError> {
        return safeDbCall {
            playerDao.addWin(playerId)
        }.asEmptyDataResult()
    }

    override suspend fun addPlayerLose(
        playerId: Int,
    ): EmptyResult<DataError> {
        return safeDbCall {
            playerDao.addLoss(playerId)
        }.asEmptyDataResult()
    }
}
