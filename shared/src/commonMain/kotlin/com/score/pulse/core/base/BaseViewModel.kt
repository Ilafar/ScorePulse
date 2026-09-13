package com.score.pulse.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greentasty.core.base.UiEffect
import com.greentasty.core.base.UiEvent
import com.greentasty.core.base.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event : UiEvent, State : UiState, Effect : UiEffect> : ViewModel() {

    private val initialState: State by lazy { createInitialState() }
    private val mutableState: MutableStateFlow<State> by lazy { MutableStateFlow(initialState) }
    private val mutableEvent = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    private val mutableEffect = Channel<Effect>(Channel.BUFFERED)

    val uiState: StateFlow<State> by lazy { mutableState.asStateFlow() }
    val event: Flow<Event> = mutableEvent.asSharedFlow()
    val effect: Flow<Effect> = mutableEffect.receiveAsFlow()

    protected val currentState: State
        get() = uiState.value

    init {
        subscribeEvents()
    }

    abstract fun createInitialState(): State

    abstract fun handleEvent(event: Event)

    fun setEvent(event: Event) {
        viewModelScope.launch {
            mutableEvent.emit(event)
        }
    }

    protected fun setState(reducer: State.() -> State) {
        mutableState.update { currentState -> currentState.reducer() }
    }

    protected fun setEffect(builder: () -> Effect) {
        viewModelScope.launch {
            mutableEffect.send(builder())
        }
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect { event ->
                handleEvent(event)
            }
        }
    }
}
