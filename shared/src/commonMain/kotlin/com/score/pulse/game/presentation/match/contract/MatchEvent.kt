package com.score.pulse.game.presentation.match.contract

import com.score.pulse.core.base.UiEvent

sealed class MatchEvent : UiEvent {
    // Open
    data object OpenStartGameDialog : MatchEvent()
    data object OpenLockRoundDialog : MatchEvent()
    data object OpenGameResultDialog : MatchEvent()
    data class OpenEditScoreDialog(val playerId: Int) : MatchEvent()

    // Click
    data class AdjustScoreClicked(val playerId: Int, val delta: Int) : MatchEvent()
    data class StartNewGameClicked(val gameName: String, val totalRounds: Int) : MatchEvent()
    data object ConfirmLockRoundClicked : MatchEvent()
    data object AddPlayerClicked : MatchEvent()

    // Dismiss
    data object DismissEdit : MatchEvent()
    data object DismissLockRound : MatchEvent()
    data object DismissStartGame : MatchEvent()
}
