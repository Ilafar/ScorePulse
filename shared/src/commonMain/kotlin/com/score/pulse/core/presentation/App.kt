package com.score.pulse.core.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.score.pulse.addplayer.presentation.ui.AddPlayerRoot
import com.score.pulse.core.presentation.components.AppBottomNavBar
import com.score.pulse.core.presentation.components.AppDestination
import com.score.pulse.core.presentation.snackbar.ObserveAsEvents
import com.score.pulse.core.presentation.snackbar.SnackbarController
import com.score.pulse.core.presentation.theme.ScorePulseTheme
import com.score.pulse.history.presentation.ui.HistoryRoot
import com.score.pulse.home.presentation.ui.HomeRoot
import com.score.pulse.home.presentation.viewmodel.HomeViewModel
import com.score.pulse.leaderboard.presentation.ui.LeaderboardRoot
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    ScorePulseTheme {
        var destination by remember { mutableStateOf(AppDestination.Home) }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val homeViewModel: HomeViewModel = koinViewModel()
        val homeListState = rememberLazyListState()

        ObserveAsEvents(
            flow = SnackbarController.events,
            key1 = snackbarHostState,
        ) { event ->
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()

                val result = snackbarHostState.showSnackbar(
                    message = event.message.asStringAsync(),
                    actionLabel = event.action?.name,
                    duration = SnackbarDuration.Long,
                )

                if (result == SnackbarResult.ActionPerformed) {
                    event.action?.action?.invoke()
                }
            }
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
                    contentPadding = contentPadding,
                    listState = homeListState,
                    viewModel = homeViewModel,
                    onNavigateToAddPlayer = { destination = AppDestination.Add },
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
