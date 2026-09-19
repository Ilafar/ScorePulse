package com.score.pulse.game.domain.usecase.observe

import com.score.pulse.game.domain.repository.PlayerRepository

class ObservePlayersUseCase(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke() = playerRepository.observePlayers()
}
