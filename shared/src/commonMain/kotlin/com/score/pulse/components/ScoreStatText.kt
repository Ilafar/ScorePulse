package com.score.pulse.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/** Big `stat-counter` score number with a small "PTS" caption underneath. */
@Composable
fun ScoreStatText(
    score: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    caption: String = "PTS",
    horizontalAlignment: Alignment.Horizontal = Alignment.End,
) {
    Column(modifier = modifier, horizontalAlignment = horizontalAlignment) {
        Text(
            text = score.grouped(),
            style = MaterialTheme.typography.displaySmall,
            color = color,
        )
        Text(
            text = caption,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Manual thousands-separator formatting — `java.text.NumberFormat` isn't available in commonMain. */
internal fun Int.grouped(): String {
    val digits = kotlin.math.abs(this).toString()
    val grouped = digits.reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
    return if (this < 0) "-$grouped" else grouped
}
