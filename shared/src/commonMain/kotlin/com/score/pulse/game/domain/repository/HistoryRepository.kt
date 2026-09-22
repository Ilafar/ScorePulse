package com.score.pulse.game.domain.repository

import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.error.EmptyResult
import com.score.pulse.game.domain.model.MatchRecord
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun observeMatchHistory(): Flow<List<MatchRecord>>
    suspend fun getAllMatchHistory(): AppResult<List<MatchRecord>, DataError>
    suspend fun insertMatchHistory(matchRecord: MatchRecord): EmptyResult<DataError>
    suspend fun clearMatchHistory(): EmptyResult<DataError>
}
