package com.score.pulse.game.domain.usecase.add

import com.score.pulse.core.domain.error.DataError
import com.score.pulse.core.domain.usecase.BaseUseCase
import com.score.pulse.game.domain.model.Game
import com.score.pulse.game.domain.repository.GameRepository

class AddNewGameUseCase(
    private val gameRepository: GameRepository
) : BaseUseCase<Game, Unit, DataError>() {
    override suspend fun invoke(params: Game) =
        gameRepository.startNewActiveGame(params)
}