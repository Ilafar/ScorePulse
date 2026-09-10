package com.score.pulse.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.score.pulse.components.EmblemAvatar
import com.score.pulse.components.grouped
import com.score.pulse.model.MatchParticipant
import com.score.pulse.model.containerColor

/**
 * 2-column standings grid for one match's participants. Deliberately not a
 * LazyVerticalGrid: the item count is small and fixed (one match's roster),
 * and this already lives inside the outer match-feed LazyColumn's item scope —
 * nesting a second scrollable there buys nothing.
 */
@Composable
fun StandingsGrid(participants: List<MatchParticipant>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        participants.sortedBy { it.rank }.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowItems.forEach { participant ->
                    StandingsCell(participant = participant, modifier = Modifier.weight(1f))
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StandingsCell(participant: MatchParticipant, modifier: Modifier = Modifier) {
    val tint = participant.accent.containerColor()
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EmblemAvatar(emblem = participant.emblem, accent = participant.accent, size = 28.dp)
            Column {
                Text(
                    text = participant.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Rank #${participant.rank}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (participant.rank == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = participant.score.grouped(),
            style = MaterialTheme.typography.titleMedium,
            color = tint,
        )
    }
}
