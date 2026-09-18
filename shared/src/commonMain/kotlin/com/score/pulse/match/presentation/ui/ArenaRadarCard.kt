package com.score.pulse.match.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.score.pulse.core.presentation.components.EmblemAvatar
import com.score.pulse.core.presentation.components.GlassCard
import com.score.pulse.core.presentation.theme.ScorePulseTheme
import com.score.pulse.players.domain.model.AccentColor
import com.score.pulse.players.domain.model.Player
import com.score.pulse.players.domain.model.PlayerEmblem
import com.score.pulse.players.domain.model.containerColor

@Composable
fun ArenaRadarCard(
    gameTitle: String,
    roundLabel: String,
    players: List<Player>,
    onCenterTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Filled.TrackChanges,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = gameTitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = roundLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }

            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(280.dp),
                contentAlignment = Alignment.Center,
            ) {
                ArenaRings(players)

                ArenaSlot(players.getOrNull(0), Modifier.align(Alignment.TopCenter))
                ArenaSlot(players.getOrNull(1), Modifier.align(Alignment.CenterEnd))
                ArenaSlot(players.getOrNull(2), Modifier.align(Alignment.BottomCenter))
                ArenaSlot(players.getOrNull(3), Modifier.align(Alignment.CenterStart))

                ArenaVsBadge(onClick = onCenterTap)
            }

            Text(
                text = "Tap center VS to start a new game",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ArenaRings(players: List<Player>) {
    val trackColor = MaterialTheme.colorScheme.outlineVariant
    val dirColors = listOf(
        players.getOrNull(0)?.accent?.containerColor() ?: MaterialTheme.colorScheme.primary,
        players.getOrNull(1)?.accent?.containerColor() ?: MaterialTheme.colorScheme.secondary,
        players.getOrNull(2)?.accent?.containerColor() ?: MaterialTheme.colorScheme.tertiary,
        players.getOrNull(3)?.accent?.containerColor() ?: MaterialTheme.colorScheme.tertiaryFixedDim,
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val outerRadius = this.size.minDimension * 0.36f
        val innerRadius = this.size.minDimension * 0.255f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        drawCircle(
            color = trackColor.copy(alpha = 0.4f),
            radius = outerRadius,
            style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 10f))),
        )
        drawCircle(
            color = trackColor.copy(alpha = 0.3f),
            radius = innerRadius,
            style = Stroke(width = 1.dp.toPx()),
        )
        val lineLength = this.size.minDimension * 0.14f
        val lineGap = this.size.minDimension * 0.17f
        val strokeWidth = 1.5.dp.toPx()
        drawLine(
            dirColors[0].copy(alpha = 0.4f),
            Offset(center.x, center.y - lineGap - lineLength),
            Offset(center.x, center.y - lineGap),
            strokeWidth,
        )
        drawLine(
            dirColors[1].copy(alpha = 0.4f),
            Offset(center.x + lineGap + lineLength, center.y),
            Offset(center.x + lineGap, center.y),
            strokeWidth,
        )
        drawLine(
            dirColors[2].copy(alpha = 0.4f),
            Offset(center.x, center.y + lineGap + lineLength),
            Offset(center.x, center.y + lineGap),
            strokeWidth,
        )
        drawLine(
            dirColors[3].copy(alpha = 0.4f),
            Offset(center.x - lineGap - lineLength, center.y),
            Offset(center.x - lineGap, center.y),
            strokeWidth,
        )
    }
}

@Composable
private fun ArenaVsBadge(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f),
                    ),
                ),
            )
            .padding(6.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Bolt,
                contentDescription = "Start new game",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp),
            )
            Text(
                text = "Start",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ArenaSlot(player: Player?, modifier: Modifier = Modifier) {
    if (player != null) {
        ArenaPlayerNode(player, modifier)
    } else {
        ArenaEmptySlotNode(modifier)
    }
}

@Composable
private fun ArenaEmptySlotNode(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.QuestionMark,
                contentDescription = "Empty player slot",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = "Open",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun ArenaPlayerNode(player: Player, modifier: Modifier = Modifier) {
    val tint = player.accent.containerColor()
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        EmblemAvatar(
            emblem = player.emblem,
            accent = player.accent,
            size = 48.dp,
            showStatusDot = true,
        )
        Text(
            text = player.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp),
        )
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(horizontal = 6.dp, vertical = 1.dp),
        ) {
            Text(
                text = "0 PTS",
                style = MaterialTheme.typography.labelSmall,
                color = tint,
            )
        }
    }
}

@Preview
@Composable
private fun ArenaRadarCardPreview() {
    ScorePulseTheme {
        ArenaRadarCard(
            gameTitle = "Cyberclash Shutdown",
            roundLabel = "Round 3/5",
            players = listOf(
                Player(1, "Alex \"Viper\"", PlayerEmblem.Gamepad, AccentColor.Emerald),
                Player(2, "Sarah \"Nova\"", PlayerEmblem.Thunder, AccentColor.Cyan),
                Player(3, "Marcus \"Rex\"", PlayerEmblem.Phoenix, AccentColor.Magenta),
                Player(4, "Elena \"Pulse\"", PlayerEmblem.Shield, AccentColor.Violet),
            ),
            onCenterTap = {},
        )
    }
}

@Preview
@Composable
private fun ArenaRadarCardEmptySlotsPreview() {
    ScorePulseTheme {
        ArenaRadarCard(
            gameTitle = "Cyberclash Shutdown",
            roundLabel = "Round 1/5",
            players = listOf(
                Player(1, "Alex \"Viper\"", PlayerEmblem.Gamepad, AccentColor.Emerald),
                Player(2, "Sarah \"Nova\"", PlayerEmblem.Thunder, AccentColor.Cyan),
            ),
            onCenterTap = {},
        )
    }
}
