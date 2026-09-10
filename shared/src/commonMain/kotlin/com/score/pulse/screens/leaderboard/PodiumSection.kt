package com.score.pulse.screens.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.score.pulse.model.AccentColor
import com.score.pulse.model.PlayerEmblem
import com.score.pulse.model.RankingEntry
import com.score.pulse.theme.ScorePulseTheme

/** Top-3 podium row: #2 (left), #1 elevated (center), #3 (right). */
@Composable
fun PodiumSection(top3: List<RankingEntry>, modifier: Modifier = Modifier) {
    val first = top3.getOrNull(0)
    val second = top3.getOrNull(1)
    val third = top3.getOrNull(2)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        second?.let { PodiumPlaceCard(entry = it, isChampion = false, modifier = Modifier.weight(1f)) }
        first?.let {
            PodiumPlaceCard(
                entry = it,
                isChampion = true,
                modifier = Modifier.weight(1f).offset(y = (-12).dp),
            )
        }
        third?.let { PodiumPlaceCard(entry = it, isChampion = false, modifier = Modifier.weight(1f)) }
    }
}

@Preview
@Composable
private fun PodiumSectionPreview() {
    ScorePulseTheme {
        PodiumSection(
            top3 = listOf(
                RankingEntry(1, "Alex \"Viper\"", "Alex", PlayerEmblem.Thunder, AccentColor.Emerald, wins = 22, matches = 26, score = 2840),
                RankingEntry(2, "Sarah \"Nova\"", "Sarah", PlayerEmblem.Gamepad, AccentColor.Cyan, wins = 20, matches = 26, score = 2490),
                RankingEntry(3, "Marcus \"Rex\"", "Marcus", PlayerEmblem.Sniper, AccentColor.Magenta, wins = 18, matches = 25, score = 2150),
            ),
        )
    }
}
