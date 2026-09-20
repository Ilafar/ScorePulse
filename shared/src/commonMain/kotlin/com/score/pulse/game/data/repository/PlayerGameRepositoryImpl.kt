package com.score.pulse.game.data.repository

import com.score.pulse.core.data.local.safeDbCall
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.error.EmptyResult
import com.score.pulse.core.domain.error.asEmptyDataResult
import com.score.pulse.game.data.local.dao.PlayerGameDao
import com.score.pulse.game.data.mapper.toDomain
import com.score.pulse.game.domain.repository.PlayerGameRepository
import kotlinx.coroutines.flow.map

class PlayerGameRepositoryImpl(
    private val playerGameDao: PlayerGameDao
) : PlayerGameRepository {
    override fun observePlayersForActiveGame() =
        playerGameDao.observePlayersForActiveGame()
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun adjustPlayerScore(
        playerId: Int,
        delta: Int
    ): EmptyResult<DataError> {
        return safeDbCall {
            playerGameDao.adjustPlayerScore(playerId, delta)
        }.asEmptyDataResult()
    }

    override suspend fun addPlayerWin(
        playerId: Int,
    ): EmptyResult<DataError> {
        return safeDbCall {
            playerGameDao.addPlayerWin(playerId)
        }.asEmptyDataResult()
    }

    override suspend fun addPlayerLose(
        playerId: Int,
    ): EmptyResult<DataError> {
        return safeDbCall {
            playerGameDao.addPlayerLose(playerId)
        }.asEmptyDataResult()
    }
}
