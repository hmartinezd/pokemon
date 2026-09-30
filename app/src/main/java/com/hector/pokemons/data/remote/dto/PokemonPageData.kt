package com.hector.pokemons.data.remote.dto

internal data class PokemonPageData(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonData>,
)
