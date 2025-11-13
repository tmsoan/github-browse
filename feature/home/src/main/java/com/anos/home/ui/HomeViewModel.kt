package com.anos.home.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anos.domain.usecase.GetRepositoriesUseCase
import com.anos.model.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
) : ViewModel() {
    private var _fetchingReposJob: Job? = null

    private var _pageIndex: Int = 0
    private var _lastRepoId: Int? = null
    private var _lastFetchTime: Long = 0L
    private val _minFetchInterval: Long = MIN_FETCH_INTERVAL_MS // 1 second debounce

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _queryContent = MutableStateFlow("")
    val queryContent: StateFlow<String> = _queryContent

    private val _repoList = MutableStateFlow<List<Repo>>(emptyList())

    val filteredRepoList: StateFlow<List<Repo>> = combine(
        _repoList,
        _queryContent
    ) { repos, query ->
        if (query.isBlank()) repos
        else repos.filter {
            it.fullName?.contains(query, ignoreCase = true) == true ||
                    it.description?.contains(query, ignoreCase = true) == true
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    init {
        // Initial fetch of repositories
        fetchRepoList()
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
        if (_uiState.value != HomeUiState.Loading) {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastFetch = currentTime - _lastFetchTime

            // Debounce: Check if enough time has passed since last fetch
            if (timeSinceLastFetch < _minFetchInterval) {
                // Too soon, ignore this call
                return
            }
            // Update timestamp
            _lastFetchTime = currentTime

            // Update the page index for the next fetch
            _pageIndex++
            // Update the last repository ID for pagination
            _lastRepoId = _repoList.value.lastOrNull()?.id
            fetchRepoList()
        }
    }

    companion object {
        private const val MIN_FETCH_INTERVAL_MS = 1000L
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
