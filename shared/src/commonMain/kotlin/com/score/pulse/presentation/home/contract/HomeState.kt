package com.score.pulse.presentation.home.contract

import com.greentasty.core.base.UiState
import com.score.pulse.domain.model.Game
import com.score.pulse.domain.model.Player

data class HomeState(
    val game: Game = Game(),
    val players: List<Player> = emptyList(),
    val currentRound: Int = 1,
    val editingPlayerId: String? = null,
    val isLockRoundConfirmationVisible: Boolean = false,
    val isGameResultVisible: Boolean = false,
    val isStartGameDialogVisible: Boolean = true,
) : UiState {
    val isFinalRound: Boolean get() = currentRound >= game.maxRounds
    val roundLabel: String get() = "Round $currentRound/${game.maxRounds}"
    val editingPlayer: Player? get() = players.firstOrNull { it.id == editingPlayerId }
}