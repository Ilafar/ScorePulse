package com.score.pulse.match.presentation.contract

import com.score.pulse.core.base.UiEffect

sealed class MatchEffect : UiEffect {
    data object NavigateToAddPlayer : MatchEffect()
}
