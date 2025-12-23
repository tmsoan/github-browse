package com.anos.home.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anos.domain.usecase.GetRepositoriesUseCase
import com.anos.model.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
) : ViewModel() {
    private var _fetchingReposJob: Job? = null

    private var _pageIndex: Int = 0
    private var _lastRepoId: Int? = null

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _queryContent = MutableStateFlow("")
    val queryContent: StateFlow<String> = _queryContent.asStateFlow()

    private val _repoList = MutableStateFlow<List<Repo>>(emptyList())

    val filteredRepoList: StateFlow<List<Repo>> = combine(
        _repoList,
        _queryContent
    ) { repos, query ->
        if (query.isBlank()) repos
        else repos.filter { it.contains(query) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    // Channel for debouncing fetchNextRepoList calls
    private val _fetchNextChannel = Channel<Unit>(Channel.CONFLATED)

    init {
        // Initial fetch of repositories
        fetchRepoList()

        // Setup debounced flow for fetchNextRepoList
        viewModelScope.launch {
            _fetchNextChannel.receiveAsFlow()
                .debounce(1000L)
                .collectLatest {
                    if (_uiState.value != HomeUiState.Loading) {
                        // Update the page index for the next fetch
                        _pageIndex++
                        // Update the last repository ID for pagination
                        _lastRepoId = _repoList.value.lastOrNull()?.id
                        fetchRepoList()
                    }
                }
        }
    }

    /**
     * Fetches a list of public repositories from GitHub API with pagination.
     * Cancels the previous fetching job if it's still running.
     */
    private fun fetchRepoList(clearCache: Boolean = false) {
        _fetchingReposJob?.cancel()
        _fetchingReposJob = viewModelScope.launch {
            getRepositoriesUseCase(
                page = _pageIndex,
                since = _lastRepoId,
                clearCache = clearCache,
                onStart = { _uiState.update { HomeUiState.Loading } },
                onComplete = {
                    _uiState.update { if (it !is HomeUiState.Error) HomeUiState.Idle else it }
                },
                onError = { error -> _uiState.update { HomeUiState.Error(error) } }
            ).collectLatest { repos ->
                // Append new repositories to the existing list
                // Repository returns individual pages (100 items each), so we accumulate here
                _repoList.value += repos
            }
        }
    }

    /**
     * Updates the search query content.
     */
    fun updateQueryContent(query: String) {
        _queryContent.value = query
    }

    /**
     * Refreshes the repository list by clearing the current list and fetching again.
     */
    fun refreshRepoList() {
        if (_uiState.value != HomeUiState.Loading) {
            _repoList.value = emptyList()
            _pageIndex = 0
            _lastRepoId = null
            fetchRepoList(clearCache = true)
        }
    }

    fun fetchNextRepoList() {
        // Send event to the debounced channel
        // Channel.CONFLATED ensures only the latest event is kept if multiple calls happen
        _fetchNextChannel.trySend(Unit)
    }
}

@Stable
sealed interface HomeUiState {
    data object Idle : HomeUiState
    data object Loading : HomeUiState
    data class Error(val message: String?) : HomeUiState
}

enum class SortOption {
    STARS, LAST_UPDATED, FORKS
}
