package com.score.pulse.game.domain.usecase.complete

import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.usecase.BaseUseCase
import com.score.pulse.game.domain.repository.GameRepository

class CompleteActiveGameUseCase(
    private val gameRepository: GameRepository
) : BaseUseCase<Unit, Unit, DataError>() {
    override suspend fun invoke(params: Unit): AppResult<Unit, DataError> {
        return gameRepository.completeActiveGame()
    }

    suspend operator fun invoke(): AppResult<Unit, DataError> = invoke(Unit)
}
