package com.score.pulse.game.presentation.match.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.score.pulse.core.presentation.components.GlassSecondaryButton

@Composable
fun GameActionBar(
    currentRound: Int,
    isFinalRound: Boolean,
    onFinishMatch: () -> Unit,
    onLockRound: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GlassSecondaryButton(
            text = "Finish Match",
            onClick = onFinishMatch,
            icon = Icons.Filled.Flag,
            modifier = Modifier.weight(1f),
        )

    }
}
