package com.hector.pokemons.ui.pokemon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hector.pokemons.domain.model.Pokemon
import com.hector.pokemons.ui.components.InfiniteScrollList

@Composable
internal fun PokemonScreen(
    viewModel: PokemonViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PokemonScreenContent(
        state = state,
        onLoadMore = viewModel::loadMore,
        onRetry = viewModel::retry
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PokemonScreenContent(
    state: PokemonUiState,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Pokémon") }) },
    ) { contentPadding ->
        when {
            state.isInitialLoading -> LoadingContent(contentPadding)
            state.items.isEmpty() && state.error != null -> ErrorContent(
                message = state.error,
                onRetry = onRetry,
                contentPadding = contentPadding,
            )

            else -> InfiniteScrollList(
                items = state.items,
                isLoading = state.isLoadingMore,
                hasMore = state.hasMore,
                onLoadMore = onLoadMore,
                key = { pokemon -> pokemon.url },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = contentPadding),
                footerContent = state.error?.let { message ->
                    {
                        PaginationError(
                            message = message,
                            onRetry = onRetry,
                        )
                    }
                },
                itemContent = { pokemon -> PokemonRow(pokemon) },
            )
        }
    }
}

@Composable
private fun LoadingContent(contentPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun PokemonRow(pokemon: Pokemon) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = pokemon.name.replaceFirstChar { character ->
                character.titlecase()
            },
            modifier = Modifier.padding(all = 20.dp),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = contentPadding)
            .padding(all = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Unable to load Pokémon", style = MaterialTheme.typography.titleMedium)
        Text(text = message, modifier = Modifier.padding(top = 8.dp))
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text("Retry")
        }
    }
}

@Composable
private fun PaginationError(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
            Text("Retry")
        }
    }
}
