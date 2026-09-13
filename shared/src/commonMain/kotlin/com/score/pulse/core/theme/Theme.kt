package com.score.pulse.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ScorePulseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ScorePulseDarkColorScheme,
        typography = scorePulseTypography(),
        shapes = ScorePulseShapes,
        content = content,
    )
}
