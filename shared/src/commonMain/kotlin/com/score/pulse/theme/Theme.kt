package com.score.pulse.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/** Wraps [content] in the ScorePulse Dark Neon Material3 theme — colors, typography and shapes. */
@Composable
fun ScorePulseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ScorePulseDarkColorScheme,
        typography = scorePulseTypography(),
        shapes = ScorePulseShapes,
        content = content,
    )
}
