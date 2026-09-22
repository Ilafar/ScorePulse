package com.score.pulse.game.data.repository

import com.score.pulse.core.data.local.safeDbCall
import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.error.EmptyResult
import com.score.pulse.core.domain.error.asEmptyDataResult
import com.score.pulse.game.data.local.dao.GameDao
import com.score.pulse.game.data.local.dao.HistoryDao
import com.score.pulse.game.data.local.dao.PlayerDao
import com.score.pulse.game.data.local.entity.MatchHistoryEntity
import com.score.pulse.game.data.mapper.toActiveEntity
import com.score.pulse.game.data.mapper.toDomain
import com.score.pulse.game.data.mapper.toSnapshotString
import com.score.pulse.game.domain.model.Game
import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class GameRepositoryImpl(
    private val gameDao: GameDao,
    private val playerDao: PlayerDao,
    private val historyDao: HistoryDao
) : GameRepository {

    override fun observeActiveGame(): Flow<Game?> {
        return gameDao.observeActiveGame().map { it?.toDomain() }
    }

    override suspend fun getActiveGame(): AppResult<Game?, DataError> {
        return safeDbCall {
            gameDao.getActiveGame()?.toDomain()
        }
    }

    override suspend fun startNewActiveGame(game: Game): EmptyResult<DataError> {
        return safeDbCall {
            playerDao.resetScores()
            gameDao.upsertActiveGame(game.toActiveEntity())
        }.asEmptyDataResult()
    }

    override suspend fun completeActiveGame(
        players: List<Player>,
        durationMinutes: Int,
        gameName: String
    ): EmptyResult<DataError> {
        return safeDbCall {
            val matchHistory = MatchHistoryEntity(
                title = gameName,
                durationMinutes = durationMinutes,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                participantsSnapshot = players.toSnapshotString()
            )
            historyDao.upsertMatchHistory(matchHistory)
            gameDao.clearActiveGame()
            playerDao.resetScores()
        }.asEmptyDataResult()
    }

    override suspend fun lockRound(resetScores: Boolean): EmptyResult<DataError> {
        return safeDbCall {
            if (resetScores) {
                playerDao.resetScores()
            }
            gameDao.lockRound()
        }.asEmptyDataResult()
    }
}
