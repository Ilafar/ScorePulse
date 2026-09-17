package com.score.pulse.addplayer.domain.usecase

import com.score.pulse.core.domain.repository.PlayerRepository

class ObservePlayersUseCase(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke() = playerRepository.observePlayers()
}