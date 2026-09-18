package com.score.pulse.game.domain.usecase

import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow

class ObservePlayersUseCase(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<List<Player>> = playerRepository.observePlayers()
}
