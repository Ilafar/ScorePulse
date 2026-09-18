package com.score.pulse.match.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.score.pulse.core.presentation.components.GlassCard
import com.score.pulse.core.presentation.components.GradientPrimaryButton

@Composable
fun NoPlayerInfoBanner(onAddPlayerClick: () -> Unit) {
    GlassCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "At least 2 players are required",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Please add at least 2 players to be able to start a game",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(12.dp))
            GradientPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Add player",
                onClick = onAddPlayerClick
            )
        }
    }
}
