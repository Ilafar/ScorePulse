package com.score.pulse.game.presentation.match.contract

import com.score.pulse.core.base.UiEffect

sealed class MatchEffect : UiEffect {
    data object NavigateToAddPlayer : MatchEffect()
}
