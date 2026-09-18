package com.score.pulse.game.presentation.addplayer.viewmodel

import androidx.lifecycle.viewModelScope
import com.score.pulse.core.base.BaseViewModel
import com.score.pulse.core.presentation.util.UiText
import com.score.pulse.core.presentation.util.toUiText
import com.score.pulse.game.domain.error.AddPlayerError
import com.score.pulse.game.domain.model.Player
import com.score.pulse.game.domain.usecase.AddPlayerUseCase
import com.score.pulse.game.domain.usecase.ClearPlayersUseCase
import com.score.pulse.game.domain.usecase.DeletePlayerUseCase
import com.score.pulse.game.domain.usecase.ObservePlayersUseCase
import com.score.pulse.game.presentation.addplayer.contract.AddPlayerEffect
import com.score.pulse.game.presentation.addplayer.contract.AddPlayerEvent
import com.score.pulse.game.presentation.addplayer.contract.AddPlayerState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AddPlayerViewModel(
    private val addPlayerUseCase: AddPlayerUseCase,
    private val deletePlayerUseCase: DeletePlayerUseCase,
    private val observePlayersUseCase: ObservePlayersUseCase,
    private val clearPlayersUseCase: ClearPlayersUseCase
) : BaseViewModel<AddPlayerEvent, AddPlayerState, AddPlayerEffect>() {

    init {
        observePlayers()
    }

    override fun createInitialState(): AddPlayerState = AddPlayerState()

    override fun handleEvent(event: AddPlayerEvent) {
        when (event) {
            AddPlayerEvent.AddPlayerClick -> addPlayer()
            AddPlayerEvent.ClearAllPlayersClick -> deleteAllPlayers()
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

            is AddPlayerEvent.PlayerRemoveClick -> {
                deletePlayer(event.playerId)
            }
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

    private fun deletePlayer(playerId: Int) {
        launchWithResult(
            block = {
                deletePlayerUseCase(playerId)
            },
            onSuccess = {
                sendSnackbar("Player removed successfully")
            },
            onError = { error -> sendSnackbar(error.toUiText()) },
        )
    }

    private fun deleteAllPlayers() {
        launchWithResult(
            block = {
                clearPlayersUseCase()
            },
            onSuccess = {
                sendSnackbar("All players removed successfully")
            },
            onError = { error -> sendSnackbar(error.toUiText()) },
        )
    }
}
