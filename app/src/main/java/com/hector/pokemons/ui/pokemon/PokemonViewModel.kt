package com.hector.pokemons.ui.pokemon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hector.pokemons.domain.repository.PokemonRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class PokemonViewModel(
    private val repository: PokemonRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PokemonUiState())
    internal val uiState: StateFlow<PokemonUiState> = _uiState.asStateFlow()

    private var nextPageUrl: String? = null

    init {
        loadInitialPage()
    }

    internal fun loadMore() {
        val url = nextPageUrl ?: return
        val currentState = _uiState.value
        if (currentState.isInitialLoading || currentState.isLoadingMore || !currentState.hasMore) {
            return
        }

        _uiState.update { state ->
            state.copy(isLoadingMore = true, error = null)
        }
        viewModelScope.launch {
            try {
                val page = repository.loadPage(url)
                nextPageUrl = page.nextPageUrl
                _uiState.update { state ->
                    state.copy(
                        items = state.items + page.pokemon,
                        isLoadingMore = false,
                        hasMore = page.nextPageUrl != null,
                    )
                }
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
                _uiState.update {
                    it.copy(isLoadingMore = false, error = throwable.userMessage())
                }
            }
        }
    }

    internal fun retry() {
        if (_uiState.value.items.isEmpty()) {
            loadInitialPage()
        } else {
            loadMore()
        }
    }

    private fun loadInitialPage() {
        if (_uiState.value.isInitialLoading) return

        _uiState.update { state ->
            state.copy(isInitialLoading = true, error = null)
        }
        viewModelScope.launch {
            try {
                val page = repository.loadInitialPage()
                nextPageUrl = page.nextPageUrl
                _uiState.update {
                    PokemonUiState(
                        items = page.pokemon,
                        hasMore = page.nextPageUrl != null,
                    )
                }
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
                _uiState.update { state ->
                    state.copy(isInitialLoading = false, error = throwable.userMessage())
                }
            }
        }
    }

    private fun Throwable.userMessage(): String =
        localizedMessage?.takeIf { msg ->
            msg.isNotBlank()
        } ?: "Something went wrong"
}
