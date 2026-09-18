package com.score.pulse.game.domain.usecase

import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.usecase.BaseUseCase
import com.score.pulse.game.domain.repository.PlayerRepository

class DeletePlayerUseCase(
    private val playerRepository: PlayerRepository
) : BaseUseCase<Int, Unit, DataError>() {
    override suspend fun invoke(params: Int): AppResult<Unit, DataError> =
        playerRepository.deletePlayer(params)
}