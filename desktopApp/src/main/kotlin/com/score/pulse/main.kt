package com.score.pulse

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main(args: Array<String>) = application {
    val width = args.getOrNull(0)?.toIntOrNull() ?: 1080
    val height = args.getOrNull(1)?.toIntOrNull() ?: 2400
    val windowState = rememberWindowState(
        size = DpSize(width.dp, height.dp),
        position = WindowPosition.Aligned(Alignment.CenterEnd),
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "ScorePulse",
        state = windowState,
        alwaysOnTop = true
    ) {
        App()
    }
}