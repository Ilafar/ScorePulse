package com.score.pulse.game.data.repository

import com.score.pulse.core.data.local.safeDbCall
import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.error.EmptyResult
import com.score.pulse.core.domain.error.asEmptyDataResult
import com.score.pulse.game.data.local.dao.HistoryDao
import com.score.pulse.game.data.mapper.toDomain
import com.score.pulse.game.data.mapper.toHistoryEntity
import com.score.pulse.game.domain.model.MatchRecord
import com.score.pulse.game.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(
    private val historyDao: HistoryDao
) : HistoryRepository {

    override fun observeMatchHistory(): Flow<List<MatchRecord>> {
        return historyDao.observeMatchHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAllMatchHistory(): AppResult<List<MatchRecord>, DataError> {
        return safeDbCall {
            historyDao.getAllMatchHistory().map { it.toDomain() }
        }
    }

    override suspend fun insertMatchHistory(matchRecord: MatchRecord): EmptyResult<DataError> {
        return safeDbCall {
            historyDao.upsertMatchHistory(matchRecord.toHistoryEntity())
        }.asEmptyDataResult()
    }

    override suspend fun clearMatchHistory(): EmptyResult<DataError> {
        return safeDbCall {
            historyDao.clearMatchHistory()
        }.asEmptyDataResult()
    }
}
