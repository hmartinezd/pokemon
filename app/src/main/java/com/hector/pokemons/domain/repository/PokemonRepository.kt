package com.hector.pokemons.domain.repository

import com.hector.pokemons.domain.model.PokemonPage

internal interface PokemonRepository {
    suspend fun loadInitialPage(): PokemonPage

    suspend fun loadPage(url: String): PokemonPage
}
