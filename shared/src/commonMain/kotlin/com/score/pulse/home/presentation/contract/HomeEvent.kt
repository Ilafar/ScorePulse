package com.score.pulse.home.presentation.contract

import com.score.pulse.core.base.UiEvent

sealed class HomeEvent : UiEvent {

    //Open
    data object OpenStartGameDialog : HomeEvent()
    data object OpenLockRoundDialog : HomeEvent()
    data object OpenGameResultDialog : HomeEvent()
    data class OpenEditScoreDialog(val playerId: Int) : HomeEvent()

    //Click
    data class AdjustScoreClicked(val playerId: Int, val delta: Int) : HomeEvent()
    data class StartNewGameClicked(val gameName: String, val totalRounds: Int) : HomeEvent()
    data object ConfirmLockRoundClicked : HomeEvent()
    data object AddPlayerClicked : HomeEvent()

    //Dismiss
    data object DismissEdit : HomeEvent()
    data object DismissLockRound : HomeEvent()
    data object DismissStartGame : HomeEvent()
}
