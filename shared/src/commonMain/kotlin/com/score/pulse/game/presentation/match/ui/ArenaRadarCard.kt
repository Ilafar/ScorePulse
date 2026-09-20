package com.score.pulse.game.presentation.match.ui

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Flag
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
import com.score.pulse.game.domain.model.AccentColor
import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.model.PlayerEmblem
import com.score.pulse.game.domain.model.PlayerWithStats
import com.score.pulse.game.domain.model.containerColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArenaRadarCard(
    gameTitle: String,
    roundLabel: String,
    players: List<PlayerWithStats>,
    isGameStarted: Boolean = false,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
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
                val displayPlayers = players.take(10)
                val slotCount = if (displayPlayers.isEmpty()) 4 else displayPlayers.size
                val radius = 100.dp

                ArenaRings(displayPlayers)

                for (i in 0 until slotCount) {
                    val angleInRadians = -PI / 2.0 + (i * 2.0 * PI / slotCount)
                    val xOffset = (cos(angleInRadians) * radius.value).dp
                    val yOffset = (sin(angleInRadians) * radius.value).dp

                    ArenaSlot(
                        player = displayPlayers.getOrNull(i),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = xOffset, y = yOffset),
                    )
                }

                val accentColors = displayPlayers.map { it.player.accent }

                ArenaVsBadge(
                    isGameStarted = isGameStarted,
                    accentColors =  accentColors,
                    onClick = onCenterTap
                )
            }

            Text(
                text = if (isGameStarted) "Tap center Finish to complete game" else "Tap center Start to start a new game",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ArenaRings(players: List<PlayerWithStats>) {
    val trackColor = MaterialTheme.colorScheme.outlineVariant
    val fallbackColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.tertiaryFixedDim,
    )
    val displayPlayers = players.take(10)
    val slotCount = if (displayPlayers.isEmpty()) 4 else displayPlayers.size
    val dirColors = List(slotCount) { i ->
        displayPlayers.getOrNull(i)?.player?.accent?.containerColor()
            ?: fallbackColors[i % fallbackColors.size]
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val outerRadius = this.size.minDimension * 0.36f
        val innerRadius = this.size.minDimension * 0.255f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        drawCircle(
            color = trackColor.copy(alpha = 0.4f),
            radius = outerRadius,
            style = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 10f))
            ),
        )
        drawCircle(
            color = trackColor.copy(alpha = 0.3f),
            radius = innerRadius,
            style = Stroke(width = 1.dp.toPx()),
        )
        val lineLength = this.size.minDimension * 0.14f
        val lineGap = this.size.minDimension * 0.17f
        val strokeWidth = 1.5.dp.toPx()

        for (i in 0 until slotCount) {
            val angleInRadians = -PI / 2.0 + (i * 2.0 * PI / slotCount)
            val cosVal = cos(angleInRadians).toFloat()
            val sinVal = sin(angleInRadians).toFloat()
            drawLine(
                color = dirColors[i].copy(alpha = 0.4f),
                start = Offset(
                    center.x + cosVal * (lineGap + lineLength),
                    center.y + sinVal * (lineGap + lineLength)
                ),
                end = Offset(center.x + cosVal * lineGap, center.y + sinVal * lineGap),
                strokeWidth = strokeWidth,
            )
        }
    }
}

@Composable
private fun ArenaVsBadge(
    isGameStarted: Boolean,
    accentColors: List<AccentColor>,
    onClick: () -> Unit
) {
    val gradientColors = if (accentColors.isNotEmpty())
        accentColors.map { it.containerColor().copy(alpha = 0.6f) }
    else
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f),
        )

    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(gradientColors))
            .padding(6.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (isGameStarted) Icons.Filled.Flag else Icons.Filled.Bolt,
                contentDescription = if (isGameStarted) "Finish game" else "Start new game",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp),
            )
            Text(
                text = if (isGameStarted) "Finish" else "Start",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ArenaSlot(player: PlayerWithStats?, modifier: Modifier = Modifier) {
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
private fun ArenaPlayerNode(player: PlayerWithStats, modifier: Modifier = Modifier) {
    val tint = player.player.accent.containerColor()
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        EmblemAvatar(
            emblem = player.player.emblem,
            accent = player.player.accent,
            size = 48.dp,
            showStatusDot = true,
        )
        Text(
            text = player.player.name,
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
                text = "${player.score} PTS",
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
                PlayerWithStats(
                    Player(
                        1,
                        "Alex \"Viper\"",
                        PlayerEmblem.Gamepad,
                        AccentColor.Emerald
                    ), wins = 0, losses = 0, score = 150, rank = 1
                ),
                PlayerWithStats(
                    Player(2, "Sarah \"Nova\"", PlayerEmblem.Thunder, AccentColor.Cyan),
                    wins = 0,
                    losses = 0,
                    score = 120,
                    rank = 2
                ),
                PlayerWithStats(
                    Player(
                        3,
                        "Marcus \"Rex\"",
                        PlayerEmblem.Phoenix,
                        AccentColor.Magenta
                    ), wins = 0, losses = 0, score = 80, rank = 3
                ),
                PlayerWithStats(
                    Player(
                        4,
                        "Elena \"Pulse\"",
                        PlayerEmblem.Shield,
                        AccentColor.Violet
                    ), wins = 0, losses = 0, score = 60, rank = 4
                ),
            ),
            isGameStarted = true,
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
                PlayerWithStats(
                    Player(
                        1,
                        "Alex \"Viper\"",
                        PlayerEmblem.Gamepad,
                        AccentColor.Emerald
                    ), wins = 0, losses = 0, score = 0, rank = 1
                ),
                PlayerWithStats(
                    Player(2, "Sarah \"Nova\"", PlayerEmblem.Thunder, AccentColor.Cyan),
                    wins = 0,
                    losses = 0,
                    score = 0,
                    rank = 2
                ),
            ),
            isGameStarted = false,
            onCenterTap = {},
        )
    }
}
