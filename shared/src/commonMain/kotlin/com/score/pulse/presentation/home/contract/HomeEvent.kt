package com.score.pulse.presentation.home.contract

import com.greentasty.core.base.UiEvent

sealed class HomeEvent : UiEvent {

    //ValueChange
    data class GameNameValueChange(val gameName: String) : HomeEvent()
    data class TotalRoundsValueChange(val totalRounds: String) : HomeEvent()

    //Click
    data object NewGameClicked : HomeEvent()
    data class EditScoreClicked(val playerId: String) : HomeEvent()
    data class AdjustScoreClicked(val playerId: String, val delta: Int) : HomeEvent()
    data object ConfirmLockRoundClicked : HomeEvent()
    data object FinishMatchClicked : HomeEvent()
    data object LockRoundClicked : HomeEvent()

    //Dismiss
    data object DismissEdit : HomeEvent()
    data object DismissLockRound : HomeEvent()
    data object DismissStartGame : HomeEvent()
}