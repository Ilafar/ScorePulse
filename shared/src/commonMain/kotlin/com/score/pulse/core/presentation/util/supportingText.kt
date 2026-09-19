package com.score.pulse.core.presentation.util

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

fun supportingText(
    modifier: Modifier = Modifier,
    message: String?
): @Composable (() -> Unit)? {
    return if (message != null) {
        { Text(text = message) }
    } else null
}