package com.anos.domain.repository

import com.anos.model.ReadmeContent
import com.anos.model.Repo
import com.anos.model.RepoInfo
import kotlinx.coroutines.flow.Flow

interface GitHubRepository {
    suspend fun getPublicRepositories(
        page: Int,
        since: Int?,
        clearCache: Boolean,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ): Flow<List<Repo>>

    suspend fun getRepositoryDetails(
        owner: String,
        repo: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ): Flow<RepoInfo>

    suspend fun getReadMeContent(
        owner: String,
        repo: String,
        onError: (String?) -> Unit,
    ): Flow<ReadmeContent>
}
