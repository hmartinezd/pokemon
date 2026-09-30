package com.hector.pokemons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hector.pokemons.data.repository.PokemonRepositoryImpl
import com.hector.pokemons.domain.repository.PokemonRepository
import com.hector.pokemons.ui.pokemon.PokemonScreen
import com.hector.pokemons.ui.pokemon.PokemonViewModel

class MainActivity : ComponentActivity() {
    private val repository: PokemonRepository by lazy {
        PokemonRepositoryImpl((application as PokemonApplication).pokemonApi)
    }

    private val viewModel: PokemonViewModel by viewModels {
        viewModelFactory {
            initializer {
                PokemonViewModel(repository)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PokemonScreen(viewModel = viewModel)
            }
        }
    }
}
