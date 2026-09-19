package com.score.pulse.game.domain.usecase.clear

import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.usecase.BaseUseCase
import com.score.pulse.game.domain.repository.PlayerRepository

class ClearPlayersUseCase(
    private val playerRepository: PlayerRepository
) : BaseUseCase<Unit, Unit, DataError>() {
    override suspend fun invoke(params: Unit): AppResult<Unit, DataError> =
        playerRepository.clearPlayers()

    suspend operator fun invoke(): AppResult<Unit, DataError> = invoke(Unit)
}