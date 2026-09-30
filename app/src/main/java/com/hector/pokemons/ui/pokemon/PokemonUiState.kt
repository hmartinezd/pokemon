package com.hector.pokemons.ui.pokemon

import com.hector.pokemons.domain.model.Pokemon

internal data class PokemonUiState(
    val items: List<Pokemon> = emptyList(),
    val isInitialLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = true,
)
