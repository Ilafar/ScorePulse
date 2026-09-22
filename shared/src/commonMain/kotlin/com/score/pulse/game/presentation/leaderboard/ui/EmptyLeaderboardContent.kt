package com.score.pulse.game.presentation.leaderboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.score.pulse.core.presentation.components.GlassCard
import com.score.pulse.core.presentation.components.GlassSecondaryButton
import com.score.pulse.core.presentation.components.GradientPrimaryButton
import com.score.pulse.core.presentation.theme.ScorePulseTheme

@Composable
fun EmptyLeaderboardContent(
    modifier: Modifier = Modifier,
    onStartMatch: () -> Unit = {},
    onAddPlayers: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(modifier = modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Hero Trophy Icon with Glow Effect
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .size(96.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                                        Color.Transparent,
                                    ),
                                ),
                            ),
                    )
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                }

                // Headline & Value Proposition
                Text(
                    text = "No Rankings Recorded Yet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Complete matches in the Active Arena to unlock the live leaderboard, player tier badges, and victory standings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(20.dp))

                // Checklist Steps
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    EmptyLeaderboardStepItem(
                        icon = Icons.Filled.Group,
                        iconTint = MaterialTheme.colorScheme.primary,
                        title = "Play matches with 2+ players",
                        subtitle = null,
                        trailingIcon = Icons.Filled.LockOpen,
                    )
                    EmptyLeaderboardStepItem(
                        icon = Icons.Filled.Timeline,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        title = "Track real-time points & rounds",
                        subtitle = "High-speed telemetry engine",
                        trailingIcon = Icons.Filled.RadioButtonUnchecked,
                    )
                    EmptyLeaderboardStepItem(
                        icon = Icons.Filled.MilitaryTech,
                        iconTint = MaterialTheme.colorScheme.tertiary,
                        title = "Climb the podium ranks",
                        subtitle = "Claim Gold, Silver & Bronze tiers",
                        trailingIcon = Icons.Filled.Flag,
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Action Buttons
                GradientPrimaryButton(
                    text = "Start a Match",
                    icon = Icons.Filled.SportsEsports,
                    onClick = onStartMatch,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(8.dp))

                GlassSecondaryButton(
                    text = "Add Players & Roster",
                    icon = Icons.Filled.PersonAdd,
                    onClick = onAddPlayers,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun EmptyLeaderboardStepItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String?,
    trailingIcon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp),
                )
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Preview
@Composable
private fun EmptyLeaderboardContentPreview() {
    ScorePulseTheme {
        EmptyLeaderboardContent()
    }
}
