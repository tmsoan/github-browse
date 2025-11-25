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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anos.home.R
import com.anos.home.ui.component.HeaderSearchBox
import com.anos.home.ui.component.RepoCard
import com.anos.home.ui.component.SettingsModalBottomSheet
import com.anos.model.OwnerInfo
import com.anos.model.Repo
import com.anos.ui.components.GitBrowseAppBar
import com.anos.ui.components.RetryBox
import com.anos.ui.theme.Dimens
import com.anos.ui.theme.GitBrowseTheme
import com.skydoves.compose.stability.runtime.TraceRecomposition
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(
    onItemClick: (Repo) -> Unit,
    homeViewModel: HomeViewModel = koinViewModel()
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
@TraceRecomposition(threshold = 3)
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
    val isSearching = remember { mutableStateOf(false) }
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
            HomeHeader(
                isSearching = isSearching.value,
                searchQuery = searchQuery,
                onQueryContent = onQueryContent,
                onSearchCloseClick = {
                    isSearching.value = false
                    onQueryContent("")
                },
                onMenuClick = {
                    showSettingsSheet.value = true
                },
                onSearchClick = {
                    isSearching.value = true
                }
            )
        }
    ) { paddingValues ->
        HomeContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            uiState = uiState,
            isSearching = isSearching.value,
            repoList = filteredList,
            onItemClick = onItemClick,
            onFetchNextPage = onFetchNextPage,
            onPullToRefresh = onPullToRefresh,
        )

        if (uiState is HomeUiState.Error && !isSearching.value) {
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
private fun HomeHeader(
    isSearching: Boolean,
    searchQuery: String,
    onQueryContent: (String) -> Unit,
    onSearchCloseClick: () -> Unit,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    if (isSearching) {
        HeaderSearchBox(
            searchQuery = searchQuery,
            onQueryContent = onQueryContent,
            onCloseClick = onSearchCloseClick
        )
    } else {
        GitBrowseAppBar(
            title = stringResource(R.string.home_title),
            leftActions = {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        painter = painterResource(id = com.anos.ui.R.drawable.outline_reorder_24),
                        contentDescription = "Reorder",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            rightActions = {
                IconButton(onClick = onSearchClick) {
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

@TraceRecomposition
@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    repoList: List<Repo>,
    isSearching: Boolean,
    onItemClick: (Repo) -> Unit,
    onFetchNextPage: () -> Unit,
    onPullToRefresh: () -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = modifier,
        state = pullToRefreshState,
        isRefreshing = uiState == HomeUiState.Loading,
        onRefresh = {
            if (!isSearching) {
                onPullToRefresh()
            }
        },
        contentAlignment = Alignment.TopCenter
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(Dimens.spacing8),
        ) {
            itemsIndexed(items = repoList, key = { _, repo -> repo.id }) { index, repo ->
                if (!isSearching && (index + 1) >= repoList.size && uiState != HomeUiState.Loading) {
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
            filteredList = (0..9).map { Repo(id = it, owner = OwnerInfo(login = "login $it")) }.toList(),
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
