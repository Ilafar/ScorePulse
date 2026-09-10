package com.score.pulse.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.score.pulse.model.AccentColor
import com.score.pulse.model.Player
import com.score.pulse.model.PlayerEmblem
import com.score.pulse.theme.ScorePulseTheme

/**
 * Active game arena — live scorekeeping for the current match.
 * Holds all round/roster state locally; screen content is split into
 * [ArenaRadarCard], [PlayerScoreEntryCard] and [GameActionBar].
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onMessage: (String) -> Unit = {},
) {
    var players by remember { mutableStateOf(sampleHomePlayers()) }
    var round by remember { mutableStateOf(3) }
    val totalRounds = 5
    var editingPlayerId by remember { mutableStateOf<String?>(null) }

    fun adjust(playerId: String, delta: Int) {
        players = players.map {
            if (it.id == playerId) it.copy(score = (it.score + delta).coerceAtLeast(0)) else it
        }
        onMessage((if (delta > 0) "+$delta" else "$delta") + " PTS applied")
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ArenaRadarCard(
                gameTitle = "Cyberclash Shutdown",
                roundLabel = "Round $round/$totalRounds",
                players = players,
                onCenterTap = { onMessage("Nexus Clash Synced") },
            )
        }
        items(players, key = { it.id }) { player ->
            PlayerScoreEntryCard(
                player = player,
                onAdjust = { delta -> adjust(player.id, delta) },
                onCustomEdit = { editingPlayerId = player.id },
            )
        }
        item {
            GameActionBar(
                round = round,
                onFinishMatch = { onMessage("Finalizing Cyber Clash Match") },
                onLockRound = {
                    if (round < totalRounds) round += 1
                    onMessage("Round locked! Advancing to Round ${round.coerceAtMost(totalRounds)}")
                },
            )
        }
    }

    val editingPlayer = players.firstOrNull { it.id == editingPlayerId }
    if (editingPlayer != null) {
        CustomScoreDialog(
            currentScore = editingPlayer.score,
            onDismiss = { editingPlayerId = null },
            onConfirm = { newScore ->
                players = players.map { if (it.id == editingPlayer.id) it.copy(score = newScore) else it }
                onMessage("Score adjusted to $newScore")
                editingPlayerId = null
            },
        )
    }
}

@Composable
private fun CustomScoreDialog(
    currentScore: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var text by remember(currentScore) { mutableStateOf(currentScore.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter custom score") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { input -> if (input.all { it.isDigit() }) text = input },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        },
        confirmButton = {
            TextButton(onClick = { text.toIntOrNull()?.let(onConfirm) ?: onDismiss() }) {
                Text("Apply", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

private fun sampleHomePlayers(): List<Player> = listOf(
    Player("p1", "Alex \"Viper\"", PlayerEmblem.Gamepad, AccentColor.Emerald, score = 142),
    Player("p2", "Sarah \"Nova\"", PlayerEmblem.Thunder, AccentColor.Cyan, score = 118),
    Player("p3", "Marcus \"Rex\"", PlayerEmblem.Phoenix, AccentColor.Magenta, score = 95),
    Player("p4", "Elena \"Pulse\"", PlayerEmblem.Shield, AccentColor.Violet, score = 86),
)

@Preview
@Composable
private fun HomeScreenPreview() {
    ScorePulseTheme {
        HomeScreen()
    }
}
