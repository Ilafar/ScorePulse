package com.score.pulse.game.presentation.history.viewmodel

import androidx.lifecycle.viewModelScope
import com.score.pulse.core.base.BaseViewModel
import com.score.pulse.game.domain.usecase.observe.ObserveMatchHistoryUseCase
import com.score.pulse.game.presentation.history.contract.HistoryEffect
import com.score.pulse.game.presentation.history.contract.HistoryEvent
import com.score.pulse.game.presentation.history.contract.HistoryState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HistoryViewModel(
    private val observeMatchHistoryUseCase: ObserveMatchHistoryUseCase
) : BaseViewModel<HistoryEvent, HistoryState, HistoryEffect>() {

    init {
        observeHistory()
    }

    override fun createInitialState(): HistoryState = HistoryState()

    override fun handleEvent(event: HistoryEvent) {
        when (event) {
            HistoryEvent.LaunchMatchClicked -> {
                setEffect { HistoryEffect.NavigateToMatch }
            }
            HistoryEvent.AddPlayerClicked -> {
                setEffect { HistoryEffect.NavigateToAddPlayer }
            }
        }
    }

    private fun observeHistory() {
        observeMatchHistoryUseCase()
            .catch { sendSnackbar("Failed to fetch history") }
            .onEach { matches ->
                setState { copy(matchRecords = matches) }
            }
            .launchIn(viewModelScope)
    }
}
