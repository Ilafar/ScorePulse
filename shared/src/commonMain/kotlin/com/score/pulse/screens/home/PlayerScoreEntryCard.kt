package com.score.pulse.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.score.pulse.components.EmblemAvatar
import com.score.pulse.components.GlassCard
import com.score.pulse.components.ScoreStatText
import com.score.pulse.model.Player
import com.score.pulse.model.containerColor

/** One roster player's live score-input row: avatar/name/score chip + stepper controls. */
@Composable
fun PlayerScoreEntryCard(
    player: Player,
    onAdjust: (Int) -> Unit,
    onCustomEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = player.accent.containerColor()
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EmblemAvatar(
                    emblem = player.emblem,
                    accent = player.accent,
                    size = 36.dp,
                    shape = MaterialTheme.shapes.medium,
                    showStatusDot = true,
                )
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                ScoreStatText(score = player.score, color = accent)
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                StepperIconButton(icon = Icons.Filled.Remove, onClick = { onAdjust(-1) })
                StepperIconButton(icon = Icons.Filled.Add, onClick = { onAdjust(1) })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                QuickAddChip(label = "+5", accent = accent, onClick = { onAdjust(5) })
                QuickAddChip(label = "+10", accent = accent, filled = true, onClick = { onAdjust(10) })
                StepperIconButton(icon = Icons.Filled.Edit, onClick = onCustomEdit)
            }
        }
    }
}

@Composable
private fun StepperIconButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun QuickAddChip(label: String, accent: Color, onClick: () -> Unit, filled: Boolean = false) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(if (filled) accent.copy(alpha = 0.9f) else accent.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (filled) MaterialTheme.colorScheme.onPrimary else accent,
        )
    }
}
