package com.score.pulse.game.domain.usecase.observe

import com.score.pulse.game.domain.model.MatchRecord
import com.score.pulse.game.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveMatchHistoryUseCase(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke(): Flow<List<MatchRecord>> = historyRepository.observeMatchHistory()
}
