package com.hector.pokemons.data.remote

import com.hector.pokemons.data.remote.dto.PokemonPageData
import retrofit2.http.GET
import retrofit2.http.Url

internal interface PokemonApi {
    @GET
    suspend fun getPokemonPage(@Url url: String): PokemonPageData
}
