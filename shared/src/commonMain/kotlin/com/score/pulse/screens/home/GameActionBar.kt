package com.score.pulse.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Flag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.score.pulse.components.GlassSecondaryButton
import com.score.pulse.components.GradientPrimaryButton

/** "Finish Match" + "Lock Round N" primary action row anchoring the Home screen. */
@Composable
fun GameActionBar(
    round: Int,
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
        GradientPrimaryButton(
            text = "Lock Round $round",
            onClick = onLockRound,
            icon = Icons.Filled.DoneAll,
            modifier = Modifier.weight(1.4f),
        )
    }
}
