package com.score.pulse.game.presentation.leaderboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.score.pulse.core.presentation.theme.ScorePulseTheme
import com.score.pulse.game.domain.model.AccentColor
import com.score.pulse.game.domain.model.PlayerEmblem
import com.score.pulse.game.domain.model.RankingEntry
import com.score.pulse.game.presentation.leaderboard.contract.LeaderboardEffect
import com.score.pulse.game.presentation.leaderboard.contract.LeaderboardEvent
import com.score.pulse.game.presentation.leaderboard.contract.LeaderboardState
import com.score.pulse.game.presentation.leaderboard.viewmodel.LeaderboardViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LeaderboardRoot(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    viewModel: LeaderboardViewModel = koinViewModel(),
    onNavigateToMatch: () -> Unit = {},
    onNavigateToAddPlayer: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LeaderboardEffect.NavigateToMatch -> onNavigateToMatch()
                LeaderboardEffect.NavigateToAddPlayer -> onNavigateToAddPlayer()
            }
        }
    }

    LeaderboardScreen(
        modifier = modifier,
        contentPadding = contentPadding,
        state = state,
        onEvent = viewModel::setEvent,
    )
}

@Composable
private fun LeaderboardScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    state: LeaderboardState = LeaderboardState(),
    onEvent: (LeaderboardEvent) -> Unit = {},
) {
    val rankings = state.rankings

    if (rankings.isEmpty()) {
        EmptyLeaderboardContent(
            onStartMatch = { onEvent(LeaderboardEvent.StartMatchClicked) },
            onAddPlayers = { onEvent(LeaderboardEvent.AddPlayerClicked) },
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { PodiumSection(top3 = state.top3) }
            item { RankingTableHeader() }
            items(state.rest, key = { it.rank }) { entry -> RankingRow(entry = entry) }
        }
    }

}

@Composable
private fun RankingTableHeader(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
        Text(
            "RANK",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(2f)
        )
        Text(
            "PLAYER",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(5f)
        )
        Text(
            "W / M",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f),
        )
        Text(
            "SCORE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(3f),
        )
    }
}

private fun sampleRankings(): List<RankingEntry> = listOf(
    RankingEntry(
        1,
        "Alex \"Viper\"",
        "Alex",
        PlayerEmblem.Thunder,
        AccentColor.Emerald,
        wins = 22,
        matches = 26,
        score = 2840
    ),
    RankingEntry(
        2,
        "Sarah \"Nova\"",
        "Sarah",
        PlayerEmblem.Gamepad,
        AccentColor.Cyan,
        wins = 20,
        matches = 26,
        score = 2490
    ),
    RankingEntry(
        3,
        "Marcus \"Rex\"",
        "Marcus",
        PlayerEmblem.Sniper,
        AccentColor.Magenta,
        wins = 18,
        matches = 25,
        score = 2150
    ),
    RankingEntry(
        4,
        "Elena \"Pulse\"",
        "Elena",
        PlayerEmblem.Shield,
        AccentColor.Emerald,
        wins = 18,
        matches = 24,
        score = 1980
    ),
    RankingEntry(
        5,
        "Kai \"Ghost\"",
        "Kai",
        PlayerEmblem.Skull,
        AccentColor.Cyan,
        wins = 15,
        matches = 22,
        score = 1740
    ),
    RankingEntry(
        6,
        "Maya \"Zenith\"",
        "Maya",
        PlayerEmblem.Shield,
        AccentColor.Cyan,
        wins = 12,
        matches = 20,
        score = 1620
    ),
    RankingEntry(
        7,
        "Liam \"Strike\"",
        "Liam",
        PlayerEmblem.Strike,
        AccentColor.Magenta,
        wins = 10,
        matches = 19,
        score = 1410
    ),
    RankingEntry(
        8,
        "Zoe \"Volt\"",
        "Zoe",
        PlayerEmblem.Thunder,
        AccentColor.Emerald,
        wins = 9,
        matches = 17,
        score = 1290
    ),
)

@Preview
@Composable
private fun LeaderboardScreenPreview() {
    ScorePulseTheme {
        LeaderboardScreen(
            state = LeaderboardState(rankings = sampleRankings())
        )
    }
}
