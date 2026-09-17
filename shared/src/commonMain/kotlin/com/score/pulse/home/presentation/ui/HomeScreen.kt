package com.score.pulse.home.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.score.pulse.history.domain.model.MatchParticipant
import com.score.pulse.history.domain.model.MatchRecord
import com.score.pulse.history.presentation.ui.MatchResultCard
import com.score.pulse.home.presentation.contract.HomeEffect
import com.score.pulse.home.presentation.contract.HomeEvent
import com.score.pulse.home.presentation.contract.HomeState
import com.score.pulse.home.presentation.viewmodel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoot(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    listState: LazyListState = rememberLazyListState(),
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToAddPlayer: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.NavigateToAddPlayer -> onNavigateToAddPlayer()
            }
        }
    }

    HomeScreen(
        modifier = modifier,
        contentPadding = contentPadding,
        listState = listState,
        state = state,
        onEvent = viewModel::setEvent,
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    listState: LazyListState = rememberLazyListState(),
    state: HomeState,
    onEvent: (HomeEvent) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ArenaRadarCard(
                gameTitle = state.game.name,
                roundLabel = state.roundLabel,
                players = state.players,
                onCenterTap = { onEvent(HomeEvent.OpenStartGameDialog) },
            )
        }
        items(state.players, key = { it.id }) { player ->
            PlayerScoreEntryCard(
                player = player,
                onAdjust = { delta -> onEvent(HomeEvent.AdjustScoreClicked(player.id, delta)) },
                onCustomEdit = { onEvent(HomeEvent.OpenEditScoreDialog(player.id)) },
            )
        }
        item {
            if (state.hasMinimumPlayers)
            GameActionBar(
                currentRound = state.currentRound,
                isFinalRound = state.isFinalRound,
                onFinishMatch = { onEvent(HomeEvent.OpenGameResultDialog) },
                onLockRound = { onEvent(HomeEvent.OpenLockRoundDialog) },
            )
        }
        item {
            if (!state.hasMinimumPlayers) {
                NoPlayerInfoBanner(onAddPlayerClick = { onEvent(HomeEvent.AddPlayerClicked) })
            }
        }
    }

    if (state.isStartGameDialogVisible)
        StartGame(
            onStartGame = { gameName, totalRounds ->
                onEvent(HomeEvent.StartNewGameClicked(gameName, totalRounds))
            },
            onDismiss = { onEvent(HomeEvent.DismissStartGame) }
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
                        title = state.game.name,
                        durationMinutes = 42,
                        dateLabel = "Today, 8:45 PM",
                        participants = state.players.map {
                            MatchParticipant(
                                name = it.name,
                                score = 0,
                                rank = 1,
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
                    onClick = { onEvent(HomeEvent.OpenStartGameDialog) }
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
            currentScore = 0,
            onDismiss = { onEvent(HomeEvent.DismissEdit) },
            onConfirm = { newScore ->
                onEvent(
                    HomeEvent.AdjustScoreClicked(
                        editingPlayer.id,
                        newScore
                    )
                )
                onEvent(HomeEvent.DismissEdit)
            },
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    ScorePulseTheme {
        HomeScreen(
            state = HomeState()
        )
    }
}
