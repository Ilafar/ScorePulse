package com.score.pulse.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.SportsEsports),
    Ranks("Ranks", Icons.Filled.Leaderboard),
    Add("Add", Icons.Filled.Add),
    History("History", Icons.Filled.History),
}
