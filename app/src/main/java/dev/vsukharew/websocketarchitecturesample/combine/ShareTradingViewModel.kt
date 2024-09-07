package dev.vsukharew.websocketarchitecturesample.combine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dev.vsukharew.websocketarchitecturesample.di.ServiceLocator
import dev.vsukharew.websocketarchitecturesample.domain.Either
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShareTradingViewModel(
    private val shareTradingRepository: ShareTradingRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(
        savedStateHandle[KEY_UI_STATE] ?: ShareTradingUiState()
    )
    val uiState = mutableUiState.asStateFlow()

    init {
        collectShareTradingChanges()
    }

    fun retry() {
        mutableUiState.update {
            it.copy(
                isErrorOccurred = false,
                isProgressVisible = true
            )
        }
        collectShareTradingChanges()
    }

    private fun collectShareTradingChanges() {
        viewModelScope.launch {
            when (val changes = shareTradingRepository.shareTradingChanges(uiState.value.shareTrading)) {
                is Either.Left -> {
                    mutableUiState.update {
                        ShareTradingUiState(
                            isErrorOccurred = true,
                            isProgressVisible = false,
                            shareTrading = null,
                        )
                    }
                }
                is Either.Right -> {
                    changes.data.collect { shareTrading ->
                        mutableUiState.update {
                            ShareTradingUiState(
                                isErrorOccurred = false,
                                isProgressVisible = false,
                                shareTrading = shareTrading
                            ).also(::saveState)
                        }
                    }
                }
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
                return ShareTradingViewModel(
                    ServiceLocator.shareTradingRepository,
                    savedStateHandle
                ) as T
            }
        }
    }

    private fun saveState(state: ShareTradingUiState) {
        savedStateHandle[KEY_UI_STATE] = state
    }
}