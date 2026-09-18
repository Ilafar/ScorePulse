package com.score.pulse.players.domain.usecase

import com.score.pulse.players.domain.model.Player
import com.score.pulse.players.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow

class ObservePlayersUseCase(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<List<Player>> = playerRepository.observePlayers()
}
