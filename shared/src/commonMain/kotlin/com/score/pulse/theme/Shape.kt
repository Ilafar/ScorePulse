package com.score.pulse.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** rounded scale → Material3 [Shapes] slots. */
val ScorePulseShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // sm
    small = RoundedCornerShape(8.dp),        // DEFAULT
    medium = RoundedCornerShape(12.dp),      // md — rounded-xl (buttons, inputs)
    large = RoundedCornerShape(16.dp),       // lg — rounded-2xl (cards, sheets)
    extraLarge = RoundedCornerShape(24.dp),  // xl
)

/** M3 [Shapes] has no pill slot — used directly for chips/badges/nav items/avatars. */
val PillShape = RoundedCornerShape(50)
