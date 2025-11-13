package com.anos.domain.usecase

import com.anos.domain.repository.GitHubRepository
import javax.inject.Inject

class GetReadMeContentUseCase @Inject constructor(
    private val gitHubRepository: GitHubRepository,
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