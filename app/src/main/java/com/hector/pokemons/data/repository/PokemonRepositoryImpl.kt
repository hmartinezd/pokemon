package com.hector.pokemons.data.repository

import com.hector.pokemons.data.remote.PokemonApi
import com.hector.pokemons.data.remote.dto.PokemonPageData
import com.hector.pokemons.domain.model.Pokemon
import com.hector.pokemons.domain.model.PokemonPage
import com.hector.pokemons.domain.repository.PokemonRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class PokemonRepositoryImpl(
    private val api: PokemonApi,
) : PokemonRepository {
    private val cache = mutableMapOf<String, PokemonPage>()
    private val cacheMutex = Mutex()

    override suspend fun loadInitialPage(): PokemonPage =
        loadPage(INITIAL_PAGE_URL)

    override suspend fun loadPage(url: String): PokemonPage =
        loadCached(url) { api.getPokemonPage(url) }

    private suspend fun loadCached(
        key: String,
        fetch: suspend () -> PokemonPageData,
    ): PokemonPage = cacheMutex.withLock {
        cache[key] ?: fetch().toDomain().also { cache[key] = it }
    }

    private fun PokemonPageData.toDomain() = PokemonPage(
        pokemon = results.map { dto ->
            Pokemon(name = dto.name, url = dto.url)
        },
        nextPageUrl = next,
    )

    private companion object {
        const val INITIAL_PAGE_URL = "pokemon?limit=10&offset=0"
    }
}
