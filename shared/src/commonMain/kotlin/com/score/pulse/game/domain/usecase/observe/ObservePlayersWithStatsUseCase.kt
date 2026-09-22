package com.score.pulse.game.domain.usecase.observe

import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.repository.PlayerGameRepository
import kotlinx.coroutines.flow.Flow

class ObservePlayersWithStatsUseCase(
    private val playerGameRepository: PlayerGameRepository
) {
    operator fun invoke(): Flow<List<Player>> = playerGameRepository.observePlayersForActiveGame()
}
