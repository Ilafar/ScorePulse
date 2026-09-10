package com.score.pulse.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.score.pulse.model.AccentColor
import com.score.pulse.model.PlayerEmblem
import com.score.pulse.model.containerColor

/**
 * Icon-and-accent-color avatar used everywhere a player is shown (arena, roster,
 * podium, rankings, match history) — stands in for a network profile picture.
 */
@Composable
fun EmblemAvatar(
    emblem: PlayerEmblem,
    accent: AccentColor,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    shape: Shape = CircleShape,
    showStatusDot: Boolean = false,
) {
    val tint = accent.containerColor()
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(1.dp, tint.copy(alpha = 0.4f), shape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = emblem.icon,
                contentDescription = emblem.label,
                tint = tint,
                modifier = Modifier.size(size * 0.5f),
            )
        }
        if (showStatusDot) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(size * 0.28f)
                    .clip(CircleShape)
                    .background(tint)
                    .border(2.dp, MaterialTheme.colorScheme.surfaceContainerLow, CircleShape),
            )
        }
    }
}
