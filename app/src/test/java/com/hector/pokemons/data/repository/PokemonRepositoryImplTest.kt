package com.hector.pokemons.data.repository

import com.hector.pokemons.data.remote.PokemonApi
import com.hector.pokemons.data.remote.dto.PokemonData
import com.hector.pokemons.data.remote.dto.PokemonPageData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonRepositoryImplTest {
    @Test
    fun `loading the same URL twice returns the cached page`() = runTest {
        val api = FakePokemonApi()
        val repository = PokemonRepositoryImpl(api)

        val first = repository.loadPage(PAGE_URL)
        val second = repository.loadPage(PAGE_URL)

        assertEquals(first, second)
        assertEquals("bulbasaur", second.pokemon.single().name)
    }

    private class FakePokemonApi : PokemonApi {
        override suspend fun getPokemonPage(url: String): PokemonPageData {
            return response
        }

        private val response = PokemonPageData(
            count = 1,
            next = null,
            previous = null,
            results = listOf(
                PokemonData(
                    name = "bulbasaur",
                    url = "https://pokeapi.co/api/v2/pokemon/1/",
                ),
            ),
        )
    }

    private companion object {
        const val PAGE_URL = "https://pokeapi.co/api/v2/pokemon?offset=10&limit=10"
    }
}
