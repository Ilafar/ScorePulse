package com.score.pulse.screens.addplayer

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.score.pulse.components.GradientPrimaryButton
import com.score.pulse.components.SectionHeader
import com.score.pulse.model.AccentColor
import com.score.pulse.model.Player
import com.score.pulse.model.PlayerEmblem
import com.score.pulse.theme.ScorePulseTheme

/**
 * Add-player form + roster management. Everything (form, emblem/color pickers,
 * roster rows, danger zone) lives in a single outer [LazyColumn] so the roster
 * itself can be a genuinely lazy, appendable list without nesting a scrollable
 * inside a scrollable.
 */
@Composable
fun AddPlayerScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onMessage: (String) -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var selectedEmblem by remember { mutableStateOf(PlayerEmblem.Gamepad) }
    var selectedAccent by remember { mutableStateOf(AccentColor.Emerald) }
    var roster by remember { mutableStateOf(sampleRoster()) }
    var nextId by remember { mutableStateOf(roster.size + 1) }

    fun addPlayer() {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        roster = listOf(Player("p$nextId", trimmed, selectedEmblem, selectedAccent)) + roster
        nextId += 1
        name = ""
        onMessage("$trimmed added to roster")
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 20) name = it },
                label = { Text("Player Name") },
                placeholder = { Text("e.g. ShadowHunter") },
                leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
                trailingIcon = {
                    if (name.isNotEmpty()) {
                        IconButton(onClick = { name = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            SectionHeader(title = "Choose Player Emblem", icon = Icons.Filled.SportsEsports)
        }
        item {
            EmblemPickerGrid(selected = selectedEmblem, onSelect = { selectedEmblem = it })
        }
        item {
            SectionHeader(title = "Neon HUD Accent", icon = Icons.Filled.Palette)
        }
        item {
            AccentColorPicker(selected = selectedAccent, onSelect = { selectedAccent = it })
        }
        item {
            GradientPrimaryButton(
                text = "Add Player to Roster",
                icon = Icons.Filled.PersonAdd,
                onClick = ::addPlayer,
                modifier = Modifier.fillMaxWidth(),
            )
        }
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
        rosterList(roster = roster, onRemove = { player -> roster = roster - player })
        item {
            DangerZoneCard(onConfirmClear = { onMessage("Score history cleared") })
        }
    }
}

private fun sampleRoster(): List<Player> = listOf(
    Player("r1", "ShadowHunter", PlayerEmblem.Gamepad, AccentColor.Emerald),
    Player("r2", "ViperPulse", PlayerEmblem.Thunder, AccentColor.Cyan),
    Player("r3", "NovaBlitz", PlayerEmblem.Phoenix, AccentColor.Magenta),
)

@Preview
@Composable
private fun AddPlayerScreenPreview() {
    ScorePulseTheme {
        AddPlayerScreen()
    }
}
