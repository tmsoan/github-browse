package com.anos.domain.usecase

import com.anos.domain.repository.GitHubRepository
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

@Factory
class GetReadMeContentUseCase(
    // Add @Provided here to tell KSP: "Don't worry, this will exist at runtime"
    @Provided private val gitHubRepository: GitHubRepository,
) {
    suspend operator fun invoke(
        owner: String,
        repo: String,
        onError: (String?) -> Unit,
    ) =
        gitHubRepository.getReadMeContent(
            owner = owner,
            repo = repo,
            onError = onError
        )
}