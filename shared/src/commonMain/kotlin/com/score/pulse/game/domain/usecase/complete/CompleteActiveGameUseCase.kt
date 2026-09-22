package com.score.pulse.game.domain.usecase.complete

import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.usecase.BaseUseCase
import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.model.calculateDurationMinutes
import com.score.pulse.game.domain.model.calculateRanks
import com.score.pulse.game.domain.repository.GameRepository
import kotlin.time.Clock

class CompleteActiveGameUseCase(
    private val gameRepository: GameRepository
) : BaseUseCase<List<Player>, Unit, DataError>() {

    override suspend fun invoke(params: List<Player>): AppResult<Unit, DataError> {
        val activeGame = when (val result = gameRepository.getActiveGame()) {
            is AppResult.Success -> result.data ?: return AppResult.Error(DataError.Local.NOT_FOUND)
            is AppResult.Error -> return result
        }

        val rankedPlayers = params.calculateRanks()
        val durationMinutes =
            activeGame.calculateDurationMinutes(Clock.System.now().toEpochMilliseconds())

        return gameRepository.completeActiveGame(
            players = rankedPlayers,
            durationMinutes = durationMinutes,
            gameName = activeGame.name
        )
    }
}