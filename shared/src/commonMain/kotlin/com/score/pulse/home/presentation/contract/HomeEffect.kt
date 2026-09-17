package com.score.pulse.home.presentation.contract

import com.score.pulse.core.base.UiEffect

sealed class HomeEffect : UiEffect {
    data object NavigateToAddPlayer : HomeEffect()
}
