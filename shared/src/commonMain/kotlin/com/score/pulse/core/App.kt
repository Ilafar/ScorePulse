package com.score.pulse.core

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.score.pulse.core.components.AppBottomNavBar
import com.score.pulse.core.components.AppDestination
import com.score.pulse.core.theme.ScorePulseTheme
import com.score.pulse.presentation.addplayer.ui.AddPlayerScreen
import com.score.pulse.presentation.history.ui.HistoryScreen
import com.score.pulse.presentation.home.ui.HomeScreen
import com.score.pulse.presentation.leaderboard.ui.LeaderboardScreen
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    ScorePulseTheme {
        var destination by remember { mutableStateOf(AppDestination.Home) }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val onMessage: (String) -> Unit = { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
        }

        Scaffold(
            bottomBar = {
                AppBottomNavBar(
                    selected = destination,
                    onSelect = { destination = it })
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { innerPadding ->
            val contentPadding = innerPadding + PaddingValues(16.dp)
            when (destination) {
                AppDestination.Home -> HomeScreen(
                    contentPadding = contentPadding,
                    onMessage = onMessage
                )

                AppDestination.Ranks -> LeaderboardScreen(
                    contentPadding = contentPadding
                )

                AppDestination.Add -> AddPlayerScreen(
                    onMessage = onMessage,
                    contentPadding = contentPadding
                )

                AppDestination.History -> HistoryScreen(
                    contentPadding = contentPadding
                )
            }
        }
    }
}
