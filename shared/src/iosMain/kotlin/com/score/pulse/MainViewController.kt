package com.score.pulse

import androidx.compose.ui.window.ComposeUIViewController
import com.score.pulse.core.presentation.App
import com.score.pulse.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) { App() }
