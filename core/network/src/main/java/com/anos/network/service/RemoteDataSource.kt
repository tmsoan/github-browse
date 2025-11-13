package com.anos.network.service

import com.anos.model.ReadmeContent
import com.anos.model.Repo
import com.anos.model.RepoInfo
import com.anos.model.result.NetworkResult
import com.anos.network.safeApiCall
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    val gitHubApi: GitHubApi,
) {
    suspend fun fetchPublicRepositories(since: Int?): NetworkResult<List<Repo>> =
        safeApiCall { gitHubApi.getPublicRepositories(since = since) }

    suspend fun getRepositoryDetails(owner: String, repo: String): NetworkResult<RepoInfo> =
        safeApiCall { gitHubApi.getRepositoryDetails(owner, repo) }

    suspend fun getReadMeContent(owner: String, repo: String): NetworkResult<ReadmeContent> =
        safeApiCall { gitHubApi.getReadMeContent(owner, repo) }
}
