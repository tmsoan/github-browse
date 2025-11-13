package com.anos.home.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anos.feature.home.R
import com.anos.home.ui.component.HeaderSearchBox
import com.anos.home.ui.component.RepoCard
import com.anos.home.ui.component.SettingsModalBottomSheet
import com.anos.model.Repo
import com.anos.ui.components.GitBrowseAppBar
import com.anos.ui.components.RetryBox
import com.anos.ui.theme.Dimens
import com.anos.ui.theme.GitBrowseTheme

@Composable
fun HomeRoute(
    onItemClick: (Repo) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val filteredList by homeViewModel.filteredRepoList.collectAsStateWithLifecycle()
    val searchQuery by homeViewModel.queryContent.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        filteredList = filteredList,
        searchQuery = searchQuery,
        onQueryContent = { homeViewModel.updateQueryContent(it) },
        onItemClick = onItemClick,
        onFetchNextPage = homeViewModel::fetchNextRepoList,
        onPullToRefresh = homeViewModel::refreshRepoList,
        onRetryClick = homeViewModel::refreshRepoList,
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    filteredList: List<Repo>,
    searchQuery: String,
    onQueryContent: (String) -> Unit,
    onItemClick: (Repo) -> Unit,
    onFetchNextPage: () -> Unit,
    onPullToRefresh: () -> Unit,
    onRetryClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val showSettingsSheet = remember { mutableStateOf(false) }
    var isSearching by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    if (showSettingsSheet.value) {
        SettingsModalBottomSheet(
            sheetState = sheetState,
            onDismiss = { showSettingsSheet.value = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isSearching) {
                HeaderSearchBox(
                    searchQuery = searchQuery,
                    onQueryContent = onQueryContent,
                    onCloseClick = {
                        isSearching = false
                        onQueryContent("")
                    }
                )
            } else {
                GitBrowseAppBar(
                    title = stringResource(R.string.home_title),
                    leftActions = {
                        IconButton(onClick = {
                            showSettingsSheet.value = true
                        }) {
                            Icon(
                                painter = painterResource(id = com.anos.ui.R.drawable.outline_reorder_24),
                                contentDescription = "Reorder",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                    rightActions = {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(
                                painter = painterResource(id = com.anos.ui.R.drawable.outline_search_24),
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        HomeContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            uiState = uiState,
            repoList = filteredList,
            onItemClick = onItemClick,
            onFetchNextPage = onFetchNextPage,
            onPullToRefresh = onPullToRefresh,
        )

        if (uiState is HomeUiState.Error && !isSearching) {
            RetryBox(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                title = stringResource(R.string.home_error_title),
                message = stringResource(R.string.home_repos_empty_message),
                buttonText = stringResource(R.string.home_retry_btn),
                onRetryClick = onRetryClick
            )
        }
    }

    if (uiState is HomeUiState.Error && uiState.message.isNullOrEmpty().not()) {
        LaunchedEffect(uiState) {
            snackbarHostState.showSnackbar(uiState.message)
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    repoList: List<Repo>,
    onItemClick: (Repo) -> Unit,
    onFetchNextPage: () -> Unit,
    onPullToRefresh: () -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = modifier,
        state = pullToRefreshState,
        isRefreshing = uiState == HomeUiState.Loading,
        onRefresh = onPullToRefresh,
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(Dimens.spacing8),
        ) {
            itemsIndexed(items = repoList, key = { _, repo -> repo.id }) { index, repo ->
                if ((index + 1) >= repoList.size && uiState != HomeUiState.Loading) {
                    onFetchNextPage()
                }
                RepoCard(
                    repoInfo = repo,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    GitBrowseTheme {
        HomeScreen(
            filteredList = listOf(
            ),
            uiState = HomeUiState.Idle,
            onQueryContent = {},
            searchQuery = "",
            onItemClick = {},
            onFetchNextPage = {},
            onPullToRefresh = {},
            onRetryClick = {}
        )
    }
}