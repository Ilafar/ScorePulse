package com.score.pulse.game.presentation.addplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.score.pulse.core.presentation.components.GradientPrimaryButton
import com.score.pulse.core.presentation.components.SectionHeader
import com.score.pulse.core.presentation.theme.ScorePulseTheme
import com.score.pulse.game.presentation.addplayer.contract.AddPlayerEvent
import com.score.pulse.game.presentation.addplayer.contract.AddPlayerState
import com.score.pulse.game.presentation.addplayer.viewmodel.AddPlayerViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddPlayerRoot(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    viewModel: AddPlayerViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AddPlayerScreen(
        modifier = modifier,
        contentPadding = contentPadding,
        state = state,
        onEvent = viewModel::setEvent,
    )
}

@Composable
private fun AddPlayerScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    state: AddPlayerState = AddPlayerState(),
    onEvent: (AddPlayerEvent) -> Unit = {},
) {
    val roster = state.roster

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            OutlinedTextField(
                value = state.name,
                onValueChange = { onEvent(AddPlayerEvent.OnNameChange(it)) },
                label = { Text("Player Name") },
                placeholder = { Text("e.g. ShadowHunter") },
                leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
                trailingIcon = {
                    if (state.name.isNotEmpty()) {
                        IconButton(onClick = { onEvent(AddPlayerEvent.ClearNameClick) }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear")
                        }
                    }
                },
                supportingText = {
                    if (state.nameError != null) {
                        Text(
                            text = state.nameError.asString(),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            SectionHeader(
                title = "Choose Player Emblem",
                icon = Icons.Filled.SportsEsports
            )
        }
        item {
            EmblemPickerGrid(
                selected = state.emblem,
                onSelect = { onEvent(AddPlayerEvent.OnEmblemChange(it)) },
                accentColor = state.accent
            )
        }
        item {
            SectionHeader(
                title = "Choose Accent",
                icon = Icons.Filled.Palette
            )
        }
        item {
            AccentColorPicker(
                selected = state.accent,
                onSelect = { onEvent(AddPlayerEvent.OnAccentChange(it)) }
            )
        }
        item {
            GradientPrimaryButton(
                text = "Add Player to Roster",
                icon = Icons.Filled.PersonAdd,
                onClick = { onEvent(AddPlayerEvent.AddPlayerClick) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (!state.isRosterEmpty){
            item {
                SectionHeader(
                    title = "Player Management",
                    icon = Icons.Filled.Groups,
                    style = MaterialTheme.typography.headlineSmall,
                    trailing = {
                        Text(
                            text = "${roster.size} Player${if (roster.size == 1) "" else "s"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
            }
            rosterList(roster = roster, onRemove = { onEvent(AddPlayerEvent.PlayerRemoveClick(it)) })
            item {
                DangerZoneCard(onConfirmClear = { onEvent(AddPlayerEvent.ClearAllPlayersClick) })
            }
        }
    }
}

@Preview
@Composable
private fun AddPlayerScreenPreview() {
    ScorePulseTheme {
        AddPlayerScreen()
    }
}
