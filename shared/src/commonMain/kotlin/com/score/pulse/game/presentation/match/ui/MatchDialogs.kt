package com.score.pulse.game.presentation.match.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.score.pulse.core.presentation.components.GlassCard
import com.score.pulse.core.presentation.components.GradientPrimaryButton
import com.score.pulse.core.presentation.util.supportingText

@Composable
fun CustomScoreDialog(
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
fun StartGame(
    onStartGame: (String, String) -> Unit, onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        var gameName by rememberSaveable { mutableStateOf("") }
        var totalRounds by rememberSaveable { mutableStateOf("") }
        var nameError by rememberSaveable { mutableStateOf<String?>(null) }
        var totalRoundsError by rememberSaveable { mutableStateOf<String?>(null) }
        GlassCard {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Please enter game details")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = gameName,
                    onValueChange = {
                        gameName = it
                        nameError = null
                    },
                    singleLine = true,
                    label = { Text("Game name") },
                    isError = nameError != null,
                    supportingText = supportingText(
                        message = nameError
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = totalRounds,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            totalRounds = input
                            totalRoundsError = null
                        }
                    },
                    singleLine = true,
                    label = { Text("Total rounds") },
                    isError = totalRoundsError != null,
                    supportingText = supportingText(
                        message = totalRoundsError,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(Modifier.height(12.dp))
                GradientPrimaryButton(
                    modifier = Modifier.fillMaxWidth(), text = "Start new game", onClick = {
                        if (gameName.isBlank()) {
                            nameError = "Game name cannot be blank"
                            return@GradientPrimaryButton
                        }
                        if (totalRounds.toIntOrNull() == null
                            || totalRounds.toInt() < 1) {
                            totalRoundsError = "Enter a valid number"
                            return@GradientPrimaryButton
                        }
                        onStartGame(gameName, totalRounds)
                    }
                )
            }
        }
    }
}
