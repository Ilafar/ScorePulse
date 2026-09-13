package com.score.pulse.presentation.home.contract

import com.greentasty.core.base.UiEffect

sealed class HomeEffect : UiEffect {
    data object NavigateToAddPlayer : HomeEffect()
}