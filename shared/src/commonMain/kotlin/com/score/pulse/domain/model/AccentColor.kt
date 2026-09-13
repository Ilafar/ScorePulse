package com.score.pulse.domain.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AccentColor(val label: String) {
    Emerald("Emerald Glow"),
    Cyan("Cyan Surge"),
    Magenta("Magenta Blaze"),
    Amber("Neon Amber"),
    Violet("Hyper Violet"),
}

@Composable
fun AccentColor.containerColor(): Color = when (this) {
    AccentColor.Emerald -> MaterialTheme.colorScheme.primary
    AccentColor.Cyan -> MaterialTheme.colorScheme.secondary
    AccentColor.Magenta -> MaterialTheme.colorScheme.tertiaryContainer
    AccentColor.Amber -> MaterialTheme.colorScheme.secondaryContainer
    AccentColor.Violet -> MaterialTheme.colorScheme.tertiaryFixedDim
}

@Composable
fun AccentColor.onContainerColor(): Color = when (this) {
    AccentColor.Emerald -> MaterialTheme.colorScheme.onPrimary
    AccentColor.Cyan -> MaterialTheme.colorScheme.onSecondary
    AccentColor.Magenta -> MaterialTheme.colorScheme.onTertiary
    AccentColor.Amber -> MaterialTheme.colorScheme.onSecondaryContainer
    AccentColor.Violet -> MaterialTheme.colorScheme.onTertiaryFixed
}
