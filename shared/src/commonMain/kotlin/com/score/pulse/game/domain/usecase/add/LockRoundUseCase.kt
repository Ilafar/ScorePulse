package com.score.pulse.game.domain.usecase.add

import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.usecase.BaseUseCase
import com.score.pulse.game.domain.repository.GameRepository

class LockRoundUseCase(
    private val gameRepository: GameRepository
) : BaseUseCase<LockRoundUseCase.Params, Unit, DataError>() {
    override suspend fun invoke(params: Params): AppResult<Unit, DataError> {
        return gameRepository.lockRound(params.resetScores)
    }

    data class Params(
        val resetScores: Boolean
    )
}