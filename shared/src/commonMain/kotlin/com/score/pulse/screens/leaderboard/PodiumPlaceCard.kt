package com.score.pulse.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.score.pulse.components.EmblemAvatar
import com.score.pulse.components.GlassCard
import com.score.pulse.components.ScoreStatText
import com.score.pulse.model.RankingEntry
import com.score.pulse.model.containerColor
import com.score.pulse.theme.PillShape

/** A single #1/#2/#3 podium place — [isChampion] drives the elevated, larger #1 styling. */
@Composable
fun PodiumPlaceCard(
    entry: RankingEntry,
    isChampion: Boolean,
    modifier: Modifier = Modifier,
) {
    val tint = entry.accent.containerColor()
    GlassCard(
        modifier = modifier,
        containerColor = if (isChampion) {
            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f)
        } else {
            MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f)
        },
    ) {
        Column(
            modifier = Modifier.padding(if (isChampion) 12.dp else 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                EmblemAvatar(
                    emblem = entry.emblem,
                    accent = entry.accent,
                    size = if (isChampion) 64.dp else 48.dp,
                    shape = MaterialTheme.shapes.medium,
                )
                if (isChampion) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 6.dp, y = 6.dp)
                            .clip(PillShape)
                            .background(tint)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.MilitaryTech,
                                contentDescription = "Champion",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(12.dp).padding(end = 2.dp),
                            )
                            Text("#1", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 3.dp, y = 3.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(tint),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = entry.rank.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
            Text(
                text = entry.name,
                style = if (isChampion) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp),
            )
            if (!isChampion) {
                Text(
                    text = entry.tag,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                ScoreStatText(
                    score = entry.score,
                    color = tint,
                    caption = if (isChampion) "CHAMPION PTS" else "PTS",
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
            }
        }
    }
}
