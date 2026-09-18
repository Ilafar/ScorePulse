package com.score.pulse.players.data.repository

import com.score.pulse.core.data.local.safeDbCall
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.error.EmptyResult
import com.score.pulse.core.domain.error.asEmptyDataResult
import com.score.pulse.players.data.local.PlayerDao
import com.score.pulse.players.data.mapper.toDomain
import com.score.pulse.players.data.mapper.toEntity
import com.score.pulse.players.domain.model.Player
import com.score.pulse.players.domain.repository.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class PlayerRepositoryImpl(
    private val playerDao: PlayerDao
) : PlayerRepository {

    override fun observePlayers(): Flow<List<Player>> {
        return playerDao.observeAll()
            .map { entities -> entities.toDomain() }
            .flowOn(Dispatchers.Default)
    }

    override suspend fun addPlayer(player: Player): EmptyResult<DataError> {
        return safeDbCall {
            playerDao.upsert(player.toEntity())
        }.asEmptyDataResult()
    }

    override suspend fun removePlayer(playerId: Int): EmptyResult<DataError> {
        return safeDbCall {
            playerDao.deleteById(playerId)
        }.asEmptyDataResult()
    }

    override suspend fun clearPlayers(): EmptyResult<DataError> {
        return safeDbCall {
            playerDao.clearAll()
        }.asEmptyDataResult()
    }
}
