package com.score.pulse.game.domain.usecase.add

import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.usecase.BaseUseCase
import com.score.pulse.game.domain.repository.PlayerGameRepository

class AdjustScoreUseCase(
    private val playerGameRepository: PlayerGameRepository
) : BaseUseCase<AdjustScoreUseCase.Params, Unit, DataError>() {
    override suspend fun invoke(params: Params): AppResult<Unit, DataError> {
        return playerGameRepository.adjustPlayerScore(
            playerId = params.playerId,
            delta = params.delta
        )
    }

    data class Params(
        val playerId: Int,
        val delta: Int
    )
}
