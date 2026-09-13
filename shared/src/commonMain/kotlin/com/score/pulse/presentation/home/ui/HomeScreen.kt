package com.score.pulse.presentation.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.score.pulse.core.components.AlertDialogCompact
import com.score.pulse.core.components.GlassCard
import com.score.pulse.core.components.GradientPrimaryButton
import com.score.pulse.core.theme.ScorePulseTheme
import com.score.pulse.domain.model.MatchParticipant
import com.score.pulse.domain.model.MatchRecord
import com.score.pulse.presentation.history.ui.MatchResultCard
import com.score.pulse.presentation.home.contract.HomeEvent
import com.score.pulse.presentation.home.contract.HomeState
import com.score.pulse.presentation.home.viewmodel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoot(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isStartGameDialogVisible)
        StartGame(
            state = state,
            onEvent = viewModel::setEvent
        )
    else
        GameContent(
            modifier = modifier,
            contentPadding = contentPadding,
            state = state,
            onEvent = viewModel::setEvent,
        )
}

@Composable
private fun GameContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    state: HomeState,
    onEvent: (HomeEvent) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ArenaRadarCard(
                gameTitle = state.game.name,
                roundLabel = state.roundLabel,
                players = state.players,
                onCenterTap = {},
            )
        }
        items(state.players, key = { it.id }) { player ->
            PlayerScoreEntryCard(
                player = player,
                onAdjust = { delta -> onEvent(HomeEvent.AdjustScoreClicked(player.id, delta)) },
                onCustomEdit = { onEvent(HomeEvent.EditScoreClicked(player.id)) },
            )
        }
        item {
            GameActionBar(
                currentRound = state.currentRound,
                isFinalRound = state.isFinalRound,
                onFinishMatch = { onEvent(HomeEvent.FinishMatchClicked) },
                onLockRound = { onEvent(HomeEvent.LockRoundClicked) },
            )
        }
    }

    if (state.isGameResultVisible) {
        Dialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
            onDismissRequest = {}
        ) {
            GlassCard {
                MatchResultCard(
                    match = MatchRecord(
                        id = "m1",
                        title = state.game.name,
                        durationMinutes = 42,
                        dateLabel = "Today, 8:45 PM",
                        participants = state.players.map {
                            MatchParticipant(
                                name = it.name,
                                score = it.score,
                                rank = it.score,
                                accent = it.accent,
                                emblem = it.emblem
                            )
                        }
                    )
                )
                GradientPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    text = "New game",
                    onClick = { onEvent(HomeEvent.NewGameClicked) }
                )
            }
        }
    }

    if (state.isLockRoundConfirmationVisible) {
        AlertDialogCompact(
            title = "Lock Round",
            text = "Reset player points for next round?",
            dismissText = "Continue without reset",
            confirmText = "Reset",
            onDismiss = { onEvent(HomeEvent.DismissLockRound) },
            onConfirm = { onEvent(HomeEvent.ConfirmLockRoundClicked) },
        )
    }

    val editingPlayer = state.editingPlayer
    if (editingPlayer != null) {
        CustomScoreDialog(
            currentScore = editingPlayer.score,
            onDismiss = { onEvent(HomeEvent.DismissEdit) },
            onConfirm = { newScore ->
                onEvent(
                    HomeEvent.AdjustScoreClicked(
                        editingPlayer.id,
                        newScore - editingPlayer.score
                    )
                )
                onEvent(HomeEvent.DismissEdit)
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

@Composable
private fun StartGame(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit = {},
) {
    Dialog(
        onDismissRequest = { onEvent(HomeEvent.DismissStartGame) }
    ){
        GlassCard {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = state.game.name,
                    onValueChange = { onEvent(HomeEvent.GameNameValueChange(it)) }
                )
                OutlinedTextField(
                    value = state.game.maxRounds.toString(),
                    onValueChange = { onEvent(HomeEvent.TotalRoundsValueChange(it)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun GameContentPreview() {
    ScorePulseTheme {
        GameContent(
            state = HomeState()
        )
    }
}
