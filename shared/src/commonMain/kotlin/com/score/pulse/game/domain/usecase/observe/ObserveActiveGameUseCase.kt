package com.score.pulse.game.domain.usecase.observe

import com.score.pulse.game.domain.repository.GameRepository

class ObserveActiveGameUseCase(
    private val gameRepository: GameRepository
) {
    operator fun invoke() = gameRepository.observeActiveGame()
}