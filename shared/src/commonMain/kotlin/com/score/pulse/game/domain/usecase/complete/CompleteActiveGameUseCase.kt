package com.score.pulse.game.domain.usecase.complete

import com.score.pulse.core.domain.error.AppResult
import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.usecase.BaseUseCase
import com.score.pulse.core.domain.usecase.NoParams
import com.score.pulse.game.domain.repository.GameRepository

class CompleteActiveGameUseCase(
    private val gameRepository: GameRepository
) : BaseUseCase<NoParams, Unit, DataError>() {
    override suspend fun invoke(params: NoParams): AppResult<Unit, DataError> {
        return gameRepository.completeActiveGame()
    }
}