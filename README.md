# Pokemons

## Overview

This small Android application implements the Pokémon infinite-scroll assignment with Kotlin, Jetpack Compose, and MVVM. It loads Pokémon names from PokeAPI in pages of ten and keeps already loaded content visible while later pages are requested or retried.

## Architecture

```text
Retrofit API
    ↓
Repository (DTO mapping + memory cache)
    ↓
PokemonViewModel / StateFlow
    ↓
Compose UI
```

The UI observes a single immutable `PokemonUiState`. Retrofit DTOs are mapped to domain models in the repository and are not exposed to Compose. Dependencies are constructed manually in an application-scoped container, which also gives the repository cache process lifetime.

`InfiniteScrollList<T>` is a generic Compose component independent of Pokémon and networking. It renders a `LazyColumn`, observes `LazyListState` with `snapshotFlow`, and asks its caller to load more when the last visible row enters a three-item threshold.

## Pagination

The known endpoint is used only for the initial request. Every later request uses the exact `next` URL returned by PokeAPI through Retrofit's `@Url`; offsets are never calculated locally. The ViewModel sets its loading guard before starting a pagination coroutine, preventing rapid scroll/recomposition signals from launching duplicate requests.

## Caching

The repository caches mapped pages by request identity in a private in-memory map. The initial request has a stable key and subsequent pages use their full URL as the key. The cache lasts for the repository/application process lifetime. Room could be introduced if offline access or durability across process restarts became a product requirement, but it is intentionally omitted here.

## Libraries

The dependency set is intentionally small:

- AndroidX lifecycle and Compose APIs for `ViewModel`, `StateFlow`, and lifecycle-aware state collection
- Retrofit with its Gson converter for HTTP and JSON mapping
- `kotlinx-coroutines-test` and JUnit 4 for deterministic local tests

No dependency-injection, image-loading, navigation, database, mocking, or paging libraries are used.

## Testing

Local unit tests use fakes and never contact PokeAPI. They cover initial loading, appending pages, exact `next` URL use, end-of-list behavior, duplicate-request prevention, preserving content after pagination failure, retrying the failed URL, and repository cache reuse.

## Tradeoffs

- Paging 3 was intentionally not used because the exercise asks for a reusable custom infinite-scrolling Compose component and direct handling of the API's `next` field.
- Room was intentionally not used because persistence across app restarts and offline storage are outside the assignment requirements.
- A dependency-injection framework was intentionally omitted because the graph is tiny and manual construction keeps the sample easy to follow.
