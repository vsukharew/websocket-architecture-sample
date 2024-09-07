package dev.vsukharew.websocketarchitecturesample.fold

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dev.vsukharew.websocketarchitecturesample.domain.Either
import dev.vsukharew.websocketarchitecturesample.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConferenceViewModel(
    private val conferenceRepository: ConferenceRepository,
    private val conferenceWebSocket: ConferenceFakeWebSocket,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(
        savedStateHandle[KEY_UI_STATE] ?: ConferenceUiState()
    )
    val uiState = mutableUiState.asStateFlow()

    init {
        collectConferenceChanges()
        logConferenceChanges()
    }

    fun retry() {
        mutableUiState.update {
            it.copy(
                isErrorOccurred = false,
                isProgressVisible = true
            )
        }
        collectConferenceChanges()
    }

    private fun collectConferenceChanges() {
        viewModelScope.launch {
            when (val changes = conferenceRepository.conferenceChanges(uiState.value.conference)) {
                is Either.Left -> {
                    mutableUiState.update {
                        ConferenceUiState(
                            isProgressVisible = false,
                            conference = null,
                            isErrorOccurred = true
                        )
                    }
                }

                is Either.Right -> {
                    changes.data.collect { conference ->
                        mutableUiState.update {
                            ConferenceUiState(
                                isProgressVisible = false,
                                conference = conference,
                                isErrorOccurred = false,
                            ).also(::saveState)
                        }.also { Log.d("conference", "$conference") }
                    }
                }
            }
        }
    }

    private fun logConferenceChanges() {
        viewModelScope.launch {
            conferenceWebSocket.eventsFlow.collect {
                Log.d("comets", "$it")
            }
        }
    }

    companion object {
        private const val KEY_UI_STATE = "ui_state"

        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val savedStateHandle = extras.createSavedStateHandle()
                return ConferenceViewModel(
                    ServiceLocator.conferenceRepository,
                    ServiceLocator.conferenceWebSocket,
                    savedStateHandle
                ) as T
            }
        }
    }

    private fun saveState(state: ConferenceUiState) {
        savedStateHandle[KEY_UI_STATE] = state
    }
}