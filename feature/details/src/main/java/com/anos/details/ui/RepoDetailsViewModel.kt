package com.anos.details.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anos.domain.usecase.GetReadMeContentUseCase
import com.anos.domain.usecase.GetRepositoryDetailsUseCase
import com.anos.model.OwnerInfo
import com.anos.model.ReadmeContent
import com.anos.model.RepoInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoDetailsViewModel @Inject constructor(
    private val getRepositoryDetailsUseCase: GetRepositoryDetailsUseCase,
    private val getReadMeContentUseCase: GetReadMeContentUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState

    private val _baseRepoInfo: MutableStateFlow<RepoInfo?> = MutableStateFlow(null)

    private val _retryTrigger: MutableStateFlow<Int> = MutableStateFlow(0)

    val repoInfo: StateFlow<RepoInfo?> = combine(
        _baseRepoInfo,
        _retryTrigger
    ) { repo, _ -> repo }
        .flatMapLatest { repo ->
            if (repo == null) {
                MutableStateFlow(null)
            } else {
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
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = _baseRepoInfo.value,
        )

    val readMeContent: StateFlow<ReadmeContent?> = _baseRepoInfo
        .flatMapLatest { repo ->
            getReadMeContentUseCase(
                owner = repo?.owner?.login ?: "",
                repo = repo?.name ?: "",
                onError = { /* No-op */ },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    fun setRepoInfo(repoId: Int, owner: String, name: String) {
        _baseRepoInfo.value = RepoInfo(
            owner = OwnerInfo(
                login = owner
            ),
            name = name,
            id = repoId,
        )
    }

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