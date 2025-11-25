package com.anos.data.repository

import com.anos.common.AppDispatchers
import com.anos.common.Dispatcher
import com.anos.database.RepoDao
import com.anos.database.entity.mapper.asDomain
import com.anos.database.entity.mapper.asEntity
import com.anos.domain.repository.GitHubRepository
import com.anos.model.RepoInfo
import com.anos.model.result.fold
import com.anos.network.service.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.koin.core.annotation.Single

@Single
internal class GitHubRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val repoDao: RepoDao,
    @param:Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : GitHubRepository {

    /**
     * fetches a list of [RepoInfo] from the api
     * @param page the page number for pagination
     * @param since the last repository ID in the previous page (for API pagination)
     * @return Flow<List<RepoInfo>>
     *
     * Strategy: Check local cache by page first, fetch from API if not cached.
     */
    override suspend fun getPublicRepositories(
        page: Int,
        since: Int?,
        clearCache: Boolean,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ) = flow {
        // clear cache if needed
        if (clearCache) {
            repoDao.clearAll()
        }
        // Step 1: Try to get cached data for this page
        val cachedRepos = repoDao.getAllRepoAt(page_ = page).asDomain()
        //println("getPublicRepositories >>> Cached repos for page $page: ${cachedRepos.size}")

        if (cachedRepos.isEmpty()) {
            // Step 2: No cache found, fetch from API
            remoteDataSource.fetchPublicRepositories(since = since).fold(
                onSuccess = { apiRepos ->
                    // Step 3: Cache the API response with page number
                    val reposWithPage = apiRepos.map { repo ->
                        repo.copy(page = page)
                    }
                    repoDao.insertRepoList(reposWithPage.asEntity())

                    // Step 4: Emit the fresh API data
                    emit(reposWithPage)
                },
                onFailure = { error ->
                    // API failed and no cache available
                    onError(error)
                }
            )
        } else {
            // Step 5: Cache exists, emit cached data
            emit(cachedRepos)
        }
    }.onStart {
        onStart()
    }.onCompletion {
        onComplete()
    }.flowOn(ioDispatcher)

    /**
     * fetches a [RepoInfo] from the api
     * @param owner repository owner name
     * @param repo repository name
     * @return Flow<RepoInfo>
     */
    override suspend fun getRepositoryDetails(
        owner: String,
        repo: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    ) = flow {
        remoteDataSource.getRepositoryDetails(owner, repo).fold(
            onSuccess = { emit(it) },
            onFailure = { error -> onError(error) }
        )
    }.onStart {
        onStart()
    }.onCompletion {
        onComplete()
    }.flowOn(ioDispatcher)

    /**
     * fetches a [ReadmeContent] from the api
     */
    override suspend fun getReadMeContent(
        owner: String,
        repo: String,
        onError: (String?) -> Unit
    ) = flow {
        remoteDataSource.getReadMeContent(owner, repo).fold(
            onSuccess = { emit(it) },
            onFailure = { error -> onError(error) }
        )
    }.flowOn(ioDispatcher)
}
