package com.hector.pokemons.ui.pokemon

import com.hector.pokemons.MainDispatcherRule
import com.hector.pokemons.domain.model.Pokemon
import com.hector.pokemons.domain.model.PokemonPage
import com.hector.pokemons.domain.repository.PokemonRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial load populates the first page`() = runTest {
        val repository = FakePokemonRepository(initialPage = page("bulbasaur", next = PAGE_TWO_URL))

        val viewModel = PokemonViewModel(repository)
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals("bulbasaur", uiState.items.first().name)
        assertTrue(uiState.hasMore)
        assertFalse(uiState.isInitialLoading)
    }

    @Test
    fun `pagination appends rather than replacing existing pokemon`() = runTest {
        val repository = FakePokemonRepository(
            initialPage = page("bulbasaur", next = PAGE_TWO_URL),
            nextPageResult = Result.success(page("ivysaur", next = PAGE_THREE_URL)),
        )
        val viewModel = PokemonViewModel(repository)
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        assertEquals(
            listOf("bulbasaur", "ivysaur"),
            viewModel.uiState.value.items.map { pokemon -> pokemon.name },
        )
    }

    @Test
    fun `pagination failure preserves previously loaded pokemon`() = runTest {
        val repository = FakePokemonRepository(
            initialPage = page("bulbasaur", next = PAGE_TWO_URL)
        )
        val viewModel = PokemonViewModel(repository)
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(listOf("bulbasaur"), uiState.items.map(Pokemon::name))
        assertFalse(uiState.isLoadingMore)
        assertNotNull(uiState.error)
        assertTrue(uiState.hasMore)
    }

    private fun page(name: String, next: String?) = PokemonPage(
        pokemon = listOf(Pokemon(name = name, url = "https://pokeapi.co/pokemon/$name")),
        nextPageUrl = next,
    )

    private class FakePokemonRepository(
        private val initialPage: PokemonPage,
        private val nextPageResult: Result<PokemonPage> = Result.failure(
            exception = AssertionError("No pagination result configured")
        ),
    ) : PokemonRepository {

        override suspend fun loadInitialPage(): PokemonPage {
            return initialPage
        }

        override suspend fun loadPage(url: String): PokemonPage {
            return nextPageResult.getOrThrow()
        }
    }

    private companion object {
        const val PAGE_TWO_URL = "https://pokeapi.co/api/v2/pokemon?offset=10&limit=10"
        const val PAGE_THREE_URL = "https://pokeapi.co/api/v2/pokemon?offset=20&limit=10"
    }
}
