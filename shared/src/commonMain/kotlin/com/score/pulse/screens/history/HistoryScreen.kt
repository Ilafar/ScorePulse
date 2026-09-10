package com.score.pulse.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.score.pulse.model.AccentColor
import com.score.pulse.model.MatchParticipant
import com.score.pulse.model.MatchRecord
import com.score.pulse.model.PlayerEmblem
import com.score.pulse.theme.ScorePulseTheme

/** Completed match summaries — a lazily rendered feed of [MatchHistoryCard]s. */
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    val matches = remember { sampleMatches() }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Match History",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        items(matches, key = { it.id }) { match -> MatchHistoryCard(match = match) }
    }
}

private fun sampleMatches(): List<MatchRecord> = listOf(
    MatchRecord(
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
    MatchRecord(
        id = "m2",
        title = "Neon Kart Grand Prix",
        durationMinutes = 28,
        dateLabel = "Yesterday, 6:10 PM",
        participants = listOf(
            MatchParticipant("Zoe", PlayerEmblem.Thunder, AccentColor.Emerald, score = 190, rank = 1),
            MatchParticipant("Kai", PlayerEmblem.Skull, AccentColor.Cyan, score = 165, rank = 2),
            MatchParticipant("Liam", PlayerEmblem.Strike, AccentColor.Magenta, score = 140, rank = 3),
        ),
    ),
    MatchRecord(
        id = "m3",
        title = "Board Battle Royale",
        durationMinutes = 55,
        dateLabel = "Sep 8, 3:20 PM",
        participants = listOf(
            MatchParticipant("Maya", PlayerEmblem.Shield, AccentColor.Cyan, score = 410, rank = 1),
            MatchParticipant("Alex", PlayerEmblem.Thunder, AccentColor.Emerald, score = 375, rank = 2),
            MatchParticipant("Elena", PlayerEmblem.Shield, AccentColor.Violet, score = 320, rank = 3),
            MatchParticipant("Marcus", PlayerEmblem.Phoenix, AccentColor.Magenta, score = 260, rank = 4),
        ),
    ),
)

@Preview
@Composable
private fun HistoryScreenPreview() {
    ScorePulseTheme {
        HistoryScreen()
    }
}
