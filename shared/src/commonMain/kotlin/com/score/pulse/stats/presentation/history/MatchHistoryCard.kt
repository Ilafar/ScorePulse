package com.score.pulse.stats.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.score.pulse.core.presentation.components.GlassCard
import com.score.pulse.core.presentation.theme.ScorePulseTheme
import com.score.pulse.players.domain.model.AccentColor
import com.score.pulse.players.domain.model.PlayerEmblem
import com.score.pulse.stats.domain.model.MatchParticipant
import com.score.pulse.stats.domain.model.MatchRecord

@Composable
fun MatchResultCard(match: MatchRecord, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = match.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MatchMetaTag(icon = Icons.Filled.Schedule, text = "${match.durationMinutes} mins duration")
                MatchMetaTag(icon = Icons.Filled.Event, text = match.dateLabel)
            }
            ChampionSpotlight(champion = match.champion)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "LEADERBOARD STANDINGS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                StandingsGrid(participants = match.participants)
            }
        }
    }
}

@Composable
private fun MatchMetaTag(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(15.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun MatchResultCardPreview() {
    ScorePulseTheme {
        MatchResultCard(
            match = MatchRecord(
                id = "m1",
                title = "Cyber Clash Showdown - Final 4",
                durationMinutes = 42,
                dateLabel = "Today, 8:45 PM",
                participants = listOf(
                    MatchParticipant("Alex", PlayerEmblem.Thunder, AccentColor.Emerald, score = 350, rank = 1),
                    MatchParticipant("Sarah", PlayerEmblem.Gamepad, AccentColor.Cyan, score = 310, rank = 2),
                    MatchParticipant("Marcus", PlayerEmblem.Phoenix, AccentColor.Magenta, score = 280, rank = 3),
                    MatchParticipant("Elena", PlayerEmblem.Shield, AccentColor.Violet, score = 215, rank = 4),
                ),
            ),
        )
    }
}
