package com.hector.pokemons

import android.app.Application
import com.hector.pokemons.data.remote.PokemonApi
import com.hector.pokemons.data.repository.PokemonRepositoryImpl
import com.hector.pokemons.domain.repository.PokemonRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PokemonApplication : Application() {
    internal val pokemonApi: PokemonApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonApi::class.java)
    }

    private companion object {
        const val BASE_URL = "https://pokeapi.co/api/v2/"
    }
}
