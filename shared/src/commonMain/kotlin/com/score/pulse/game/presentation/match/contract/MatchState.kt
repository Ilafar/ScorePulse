package com.score.pulse.game.presentation.match.contract

import com.score.pulse.core.base.UiState
import com.score.pulse.game.domain.model.Game
import com.score.pulse.game.domain.model.GameStatus
import com.score.pulse.game.domain.model.PlayerWithStats

data class MatchState(
    val game: Game? = null,
    val players: List<PlayerWithStats> = emptyList(),
    val editingPlayerId: Int? = null,
    val isLockRoundConfirmationVisible: Boolean = false,
    val isGameResultVisible: Boolean = false,
    val isStartGameDialogVisible: Boolean = false,
) : UiState {
    val isGameStarted: Boolean get() = game.isStarted()
    val isFinalRound: Boolean get() = game.isFinalRound()
    val roundLabel: String get() = game.roundLabel()
    val hasMinimumPlayers: Boolean get() = players.size >= 2
    val editingPlayer: PlayerWithStats? get() = players.firstOrNull { it.player.id == editingPlayerId }
}

private fun Game?.isStarted(): Boolean =
    this != null && status == GameStatus.IN_PROGRESS

private fun Game?.isFinalRound(): Boolean =
    this != null && maxRounds > 0 && currentRound >= maxRounds

private fun Game?.roundLabel(): String =
    if (this != null && maxRounds > 0) "Round $currentRound/$maxRounds" else ""
