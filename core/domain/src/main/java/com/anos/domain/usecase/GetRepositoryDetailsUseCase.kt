package com.anos.domain.usecase

import com.anos.domain.repository.GitHubRepository
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

@Factory
class GetRepositoryDetailsUseCase(
    @Provided private val gitHubRepository: GitHubRepository,
) {
    suspend operator fun invoke(
        owner: String,
        repo: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ) =
        gitHubRepository.getRepositoryDetails(
            owner = owner,
            repo = repo,
            onStart = onStart,
            onComplete = onComplete,
            onError = onError
        )
}
