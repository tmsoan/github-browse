package com.anos.details.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.anos.domain.usecase.GetReadMeContentUseCase
import com.anos.domain.usecase.GetRepositoryDetailsUseCase
import com.anos.model.OwnerInfo
import com.anos.model.ReadmeContent
import com.anos.model.RepoInfo
import com.anos.navigation.ScreenRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class RepoDetailsViewModel(
    private val getRepositoryDetailsUseCase: GetRepositoryDetailsUseCase,
    private val getReadMeContentUseCase: GetReadMeContentUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState

    private val _baseRepoInfo: MutableStateFlow<RepoInfo> = MutableStateFlow(
        savedStateHandle.toRoute<ScreenRoute.DetailsScreen>().run {
            RepoInfo(
                id = repoId,
                owner = OwnerInfo(
                    login = owner,
                ),
                name = name,
            )
        }
    )

    private val _retryTrigger: MutableStateFlow<Int> = MutableStateFlow(0)

    val repoInfo: StateFlow<RepoInfo?> = combine(
        _baseRepoInfo,
        _retryTrigger
    ) { repo, _ -> repo }
        .flatMapLatest { repo ->
            getRepositoryDetailsUseCase(
                owner = repo.owner.login,
                repo = repo.name ?: "",
                onStart = { _uiState.value = DetailsUiState.Loading },
                onComplete = { _uiState.value = DetailsUiState.Idle },
                onError = {
                    viewModelScope.launch {
                        _uiState.value = DetailsUiState.Error(it)
                    }
                },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _baseRepoInfo.value,
        )

    val readMeContent: StateFlow<ReadmeContent?> = _baseRepoInfo
        .flatMapLatest { repo ->
            getReadMeContentUseCase(
                owner = repo.owner.login,
                repo = repo.name ?: "",
                onError = { /* No-op */ },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    fun refreshData() {
        _retryTrigger.value++
    }
}

@Stable
sealed interface DetailsUiState {
    data object Idle : DetailsUiState
    data object Loading : DetailsUiState
    data class Error(val message: String?) : DetailsUiState
}