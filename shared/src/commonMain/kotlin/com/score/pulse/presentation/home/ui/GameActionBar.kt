package com.score.pulse.presentation.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Flag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.score.pulse.core.components.GlassSecondaryButton
import com.score.pulse.core.components.GradientPrimaryButton

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
        if (!isFinalRound)
            GradientPrimaryButton(
                text = "Lock Round $currentRound",
                onClick = onLockRound,
                icon = Icons.Filled.DoneAll,
                modifier = Modifier.weight(1.4f),
            )
    }
}
