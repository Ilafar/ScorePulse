package com.score.pulse.game.presentation.history.contract

import com.score.pulse.core.base.UiState
import com.score.pulse.game.domain.model.MatchRecord

data class HistoryState(
    val matchRecords: List<MatchRecord> = emptyList(),
): UiState