package com.hector.pokemons.domain.model

internal data class PokemonPage(
    val pokemon: List<Pokemon>,
    val nextPageUrl: String?,
)
