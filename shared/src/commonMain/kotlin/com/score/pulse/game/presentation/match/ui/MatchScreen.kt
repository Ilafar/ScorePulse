package com.score.pulse.game.presentation.match.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.score.pulse.core.presentation.components.AlertDialogCompact
import com.score.pulse.core.presentation.components.GlassCard
import com.score.pulse.core.presentation.components.GradientPrimaryButton
import com.score.pulse.core.presentation.theme.ScorePulseTheme
import com.score.pulse.game.domain.model.MatchParticipant
import com.score.pulse.game.domain.model.MatchRecord
import com.score.pulse.game.presentation.history.ui.MatchResultCard
import com.score.pulse.game.presentation.match.contract.MatchEffect
import com.score.pulse.game.presentation.match.contract.MatchEvent
import com.score.pulse.game.presentation.match.contract.MatchState
import com.score.pulse.game.presentation.match.viewmodel.MatchViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MatchRoot(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    listState: LazyListState = rememberLazyListState(),
    viewModel: MatchViewModel = koinViewModel(),
    onNavigateToAddPlayer: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MatchEffect.NavigateToAddPlayer -> onNavigateToAddPlayer()
            }
        }
    }

    MatchScreen(
        modifier = modifier,
        contentPadding = contentPadding,
        listState = listState,
        state = state,
        onEvent = viewModel::setEvent,
    )
}

@Composable
private fun MatchScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    listState: LazyListState = rememberLazyListState(),
    state: MatchState,
    onEvent: (MatchEvent) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            ArenaRadarCard(
                gameTitle = state.game?.name.orEmpty(),
                roundLabel = state.roundLabel,
                players = state.players,
                isGameStarted = state.isGameStarted,
                onCenterTap = {
                    if (state.isGameStarted) {
                        onEvent(MatchEvent.FinishGameClicked)
                    } else {
                        onEvent(MatchEvent.OpenStartGameDialog)
                    }
                },
            )
        }

        if (state.isGameStarted)
            items(state.players, key = { it.player.id }) { player ->
                PlayerScoreEntryCard(
                    player = player,
                    onAdjust = { delta ->
                        onEvent(
                            MatchEvent.AdjustScoreClicked(
                                player.player.id,
                                delta
                            )
                        )
                    },
                    onCustomEdit = { onEvent(MatchEvent.OpenEditScoreDialog(player.player.id)) },
                )
            }
        if (state.isGameStarted && state.hasMinimumPlayers)
            item {
                if (!state.isFinalRound)
                    GradientPrimaryButton(
                        modifier = Modifier.fillMaxSize(),
                        text = "Lock Round ${state.game?.currentRound}",
                        onClick = { onEvent(MatchEvent.OpenLockRoundDialog) },
                        icon = Icons.Filled.DoneAll,
                    )
            }
        item {
            if (!state.hasMinimumPlayers) {
                NoPlayerInfoBanner(onAddPlayerClick = { onEvent(MatchEvent.AddPlayerClicked) })
            }
        }
    }

    if (state.isStartGameDialogVisible)
        StartGame(
            onStartGame = { gameName, totalRounds ->
                onEvent(MatchEvent.StartNewGameClicked(gameName, totalRounds))
            },
            onDismiss = { onEvent(MatchEvent.DismissStartGame) }
        )

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
                        title = state.game?.name.orEmpty(),
                        durationMinutes = 42,
                        dateLabel = "Today, 8:45 PM",
                        participants = state.players.map {
                            MatchParticipant(
                                name = it.player.name,
                                score = it.score,
                                rank = it.rank,
                                accent = it.player.accent,
                                emblem = it.player.emblem
                            )
                        }
                    )
                )
                GradientPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    text = "New game",
                    onClick = { onEvent(MatchEvent.OpenStartGameDialog) }
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
            onDismiss = { onEvent(MatchEvent.ConfirmLockRoundClicked(resetScores = false)) },
            onConfirm = { onEvent(MatchEvent.ConfirmLockRoundClicked(resetScores = true)) },
        )
    }

    val editingPlayer = state.editingPlayer
    if (editingPlayer != null) {
        CustomScoreDialog(
            currentScore = editingPlayer.score,
            onDismiss = { onEvent(MatchEvent.DismissEdit) },
            onConfirm = { newScore ->
                onEvent(
                    MatchEvent.AdjustScoreClicked(
                        editingPlayer.player.id,
                        newScore
                    )
                )
            },
        )
    }
}

@Preview
@Composable
private fun MatchScreenPreview() {
    ScorePulseTheme {
        MatchScreen(
            state = MatchState()
        )
    }
}
