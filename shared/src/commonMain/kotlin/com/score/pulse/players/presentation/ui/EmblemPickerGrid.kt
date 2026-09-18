package com.score.pulse.players.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.score.pulse.core.presentation.theme.ScorePulseTheme
import com.score.pulse.players.domain.model.AccentColor
import com.score.pulse.players.domain.model.PlayerEmblem
import com.score.pulse.players.domain.model.containerColor

@Composable
fun EmblemPickerGrid(
    modifier: Modifier = Modifier,
    selected: PlayerEmblem,
    onSelect: (PlayerEmblem) -> Unit,
    accentColor: AccentColor
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PlayerEmblem.entries.chunked(4).forEach { rowEmblems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowEmblems.forEach { emblem ->
                    EmblemPickerCell(
                        emblem = emblem,
                        isSelected = emblem == selected,
                        onClick = { onSelect(emblem) },
                        modifier = Modifier.weight(1f),
                        accentColor = accentColor
                    )
                }
                repeat(4 - rowEmblems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun EmblemPickerCell(
    modifier: Modifier = Modifier,
    emblem: PlayerEmblem,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: AccentColor
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer,
            )
            .clickable(onClick = onClick)
            .padding(6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = emblem.icon,
                contentDescription = emblem.label,
                tint = accentColor.containerColor(),
                modifier = Modifier.size(26.dp),
            )
            Text(
                text = emblem.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(accentColor.containerColor()),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun EmblemPickerGridPreview() {
    ScorePulseTheme {
        EmblemPickerGrid(
            selected = PlayerEmblem.Gamepad,
            onSelect = {},
            accentColor = AccentColor.Emerald
        )
    }
}
