package com.anos.domain.usecase

import com.anos.domain.repository.GitHubRepository
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

@Factory
class GetRepositoriesUseCase(
    @Provided private val gitHubRepository: GitHubRepository,
) {
    suspend operator fun invoke(
        page: Int,
        since: Int?,
        clearCache: Boolean,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ) =
        gitHubRepository.getPublicRepositories(
            page = page,
            since = since,
            clearCache = clearCache,
            onStart = onStart,
            onComplete = onComplete,
            onError = onError
        )
}
