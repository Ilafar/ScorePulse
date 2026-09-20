package com.score.pulse.game.domain.usecase.observe

import com.score.pulse.game.domain.repository.PlayerGameRepository

class ObservePlayersWithStatsUseCase(
    private val playerGameRepository: PlayerGameRepository
) {
    operator fun invoke() = playerGameRepository.observePlayersForActiveGame()
}
