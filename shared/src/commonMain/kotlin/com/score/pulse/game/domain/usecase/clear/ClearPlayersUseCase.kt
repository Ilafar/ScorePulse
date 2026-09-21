package com.score.pulse.game.domain.usecase.clear

import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.usecase.BaseUseCase
import com.score.pulse.core.domain.usecase.NoParams
import com.score.pulse.game.domain.repository.PlayerRepository

class ClearPlayersUseCase(
    private val playerRepository: PlayerRepository
) : BaseUseCase<NoParams, Unit, DataError>() {
    override suspend fun invoke(params: NoParams): AppResult<Unit, DataError> =
        playerRepository.clearPlayers()
}