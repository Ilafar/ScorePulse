package com.score.pulse.game.domain.usecase.observe

import com.score.pulse.game.domain.model.RankingEntry
import com.score.pulse.game.domain.model.calculateRanks
import com.score.pulse.game.domain.model.toRankingEntries
import com.score.pulse.game.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveLeaderboardUseCase(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<List<RankingEntry>> {
        return playerRepository.observePlayers().map { players ->
            players.calculateRanks().toRankingEntries()
        }
    }
}
