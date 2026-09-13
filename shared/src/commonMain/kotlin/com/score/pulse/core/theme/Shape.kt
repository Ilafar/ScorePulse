package com.score.pulse.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val ScorePulseShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // sm
    small = RoundedCornerShape(8.dp),        // DEFAULT
    medium = RoundedCornerShape(12.dp),      // md — rounded-xl (buttons, inputs)
    large = RoundedCornerShape(16.dp),       // lg — rounded-2xl (cards, sheets)
    extraLarge = RoundedCornerShape(24.dp),  // xl
)

val PillShape = RoundedCornerShape(50)
