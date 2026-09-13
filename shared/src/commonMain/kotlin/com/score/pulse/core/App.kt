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
import com.score.pulse.di.appModule
import com.score.pulse.presentation.addplayer.ui.AddPlayerRoot
import com.score.pulse.presentation.history.ui.HistoryRoot
import com.score.pulse.presentation.home.ui.HomeRoot
import com.score.pulse.presentation.leaderboard.ui.LeaderboardRoot
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(configuration = koinConfiguration { modules(appModule) }) {
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
                    AppDestination.Home -> HomeRoot(
                        contentPadding = contentPadding
                    )

                    AppDestination.Ranks -> LeaderboardRoot(
                        contentPadding = contentPadding
                    )

                    AppDestination.Add -> AddPlayerRoot(
                        contentPadding = contentPadding
                    )

                    AppDestination.History -> HistoryRoot(
                        contentPadding = contentPadding
                    )
                }
            }
        }
    }
}
