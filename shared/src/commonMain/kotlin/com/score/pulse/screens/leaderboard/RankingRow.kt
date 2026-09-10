package com.score.pulse.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.score.pulse.components.EmblemAvatar
import com.score.pulse.components.grouped
import com.score.pulse.model.RankingEntry
import com.score.pulse.model.containerColor

/** One rank-4-and-below leaderboard table row. */
@Composable
fun RankingRow(entry: RankingEntry, modifier: Modifier = Modifier) {
    val tint = entry.accent.containerColor()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = entry.rank.toString().padStart(2, '0'),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(2f),
        )
        Row(
            modifier = Modifier.weight(5f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EmblemAvatar(
                emblem = entry.emblem,
                accent = entry.accent,
                size = 32.dp,
                shape = MaterialTheme.shapes.small,
            )
            Text(
                text = entry.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Text(
            text = "${entry.wins} / ${entry.matches}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f),
        )
        Text(
            text = entry.score.grouped(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(3f),
        )
    }
}
