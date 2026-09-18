package com.score.pulse.players.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.score.pulse.core.base.BaseViewModel
import com.score.pulse.core.presentation.util.UiText
import com.score.pulse.core.presentation.util.toUiText
import com.score.pulse.players.domain.error.AddPlayerError
import com.score.pulse.players.domain.model.Player
import com.score.pulse.players.domain.usecase.AddPlayerUseCase
import com.score.pulse.players.domain.usecase.ObservePlayersUseCase
import com.score.pulse.players.presentation.contract.AddPlayerEffect
import com.score.pulse.players.presentation.contract.AddPlayerEvent
import com.score.pulse.players.presentation.contract.AddPlayerState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AddPlayerViewModel(
    private val addPlayerUseCase: AddPlayerUseCase,
    private val observePlayersUseCase: ObservePlayersUseCase
) : BaseViewModel<AddPlayerEvent, AddPlayerState, AddPlayerEffect>() {

    init {
        observePlayers()
    }

    override fun createInitialState(): AddPlayerState = AddPlayerState()

    override fun handleEvent(event: AddPlayerEvent) {
        when (event) {
            AddPlayerEvent.AddPlayerClick -> addPlayer()
            AddPlayerEvent.ClearHistoryClick -> TODO()
            is AddPlayerEvent.OnAccentChange -> {
                setState { copy(accent = event.accent) }
            }

            is AddPlayerEvent.OnEmblemChange -> {
                setState { copy(emblem = event.emblem) }
            }

            is AddPlayerEvent.OnNameChange -> {
                if (event.name.length <= 20) {
                    setState { copy(name = event.name, nameError = null) }
                }
            }

            AddPlayerEvent.PlayerRemoveClick -> TODO()
            AddPlayerEvent.ClearNameClick -> {
                setState { copy(name = "", nameError = null) }
            }
        }
    }

    private fun observePlayers() {
        observePlayersUseCase()
            .catch { sendSnackbar("Failed to observe players") }
            .onEach { roster ->
                setState { copy(roster = roster) }
            }
            .launchIn(viewModelScope)
    }

    private fun addPlayer() {
        launchWithResult(
            block = {
                addPlayerUseCase(
                    Player(
                        name = currentState.name,
                        emblem = currentState.emblem,
                        accent = currentState.accent
                    )
                )
            },
            onSuccess = {
                setState { copy(name = "", nameError = null) }
                sendSnackbar("Player added successfully")
            },
            onError = { error ->
                when (error) {
                    AddPlayerError.BlankName -> {
                        setState { copy(nameError = UiText.DynamicString("Name cannot be blank")) }
                    }

                    AddPlayerError.NameTooLong -> {
                        setState { copy(nameError = UiText.DynamicString("Name cannot be longer than 20 characters")) }
                    }

                    is AddPlayerError.Data -> sendSnackbar(error.error.toUiText())
                }
            }
        )
    }
}
