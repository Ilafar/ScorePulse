package com.score.pulse.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** The 5 "Neon HUD Accent" dots offered by the add-player color picker. */
enum class AccentColor(val label: String) {
    Emerald("Emerald Glow"),
    Cyan("Cyan Surge"),
    Magenta("Magenta Blaze"),
    Amber("Neon Amber"),
    Violet("Hyper Violet"),
}

/** Container/background color for this accent, read from the active [MaterialTheme.colorScheme]. */
@Composable
fun AccentColor.containerColor(): Color = when (this) {
    AccentColor.Emerald -> MaterialTheme.colorScheme.primary
    AccentColor.Cyan -> MaterialTheme.colorScheme.secondary
    AccentColor.Magenta -> MaterialTheme.colorScheme.tertiaryContainer
    AccentColor.Amber -> MaterialTheme.colorScheme.secondaryContainer
    AccentColor.Violet -> MaterialTheme.colorScheme.tertiaryFixedDim
}

/** The color that reads clearly on top of [containerColor]. */
@Composable
fun AccentColor.onContainerColor(): Color = when (this) {
    AccentColor.Emerald -> MaterialTheme.colorScheme.onPrimary
    AccentColor.Cyan -> MaterialTheme.colorScheme.onSecondary
    AccentColor.Magenta -> MaterialTheme.colorScheme.onTertiary
    AccentColor.Amber -> MaterialTheme.colorScheme.onSecondaryContainer
    AccentColor.Violet -> MaterialTheme.colorScheme.onTertiaryFixed
}
