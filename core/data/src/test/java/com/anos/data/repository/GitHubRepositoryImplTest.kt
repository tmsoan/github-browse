package com.anos.data.repository

import com.anos.database.RepoDao
import com.anos.database.entity.RepoEntity
import com.anos.domain.repository.GitHubRepository
import com.anos.model.OwnerInfo
import com.anos.model.ReadmeContent
import com.anos.model.Repo
import com.anos.model.RepoInfo
import com.anos.model.result.NetworkResult
import com.anos.network.service.RemoteDataSource
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GitHubRepositoryImplTest {
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var repoDao: RepoDao
    private lateinit var repository: GitHubRepository

    private val testOwner = OwnerInfo(login = "testOwner", avatarUrl = "url", htmlUrl = "html")

    @Before
    fun setUp() {
        remoteDataSource = mockk(relaxed = true)
        repoDao = mockk(relaxed = true)
        repository = GitHubRepositoryImpl(remoteDataSource, repoDao, Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getPublicRepositories emits cached data when cache exists`() = runTest {
        // Given
        val cachedEntities = listOf(
            RepoEntity(id = 1, name = "repo1", fullName = "owner/repo1", description = "desc", owner = testOwner, page = 1)
        )
        coEvery { repoDao.getAllRepoAt(1) } returns cachedEntities

        var started = false
        var completed = false

        // When
        val result = repository.getPublicRepositories(
            page = 1,
            since = null,
            clearCache = false,
            onStart = { started = true },
            onComplete = { completed = true },
            onError = {}
        ).first()

        // Then
        assertTrue(started)
        assertTrue(completed)
        assertEquals(1, result.size)
        assertEquals("repo1", result[0].name)
        coVerify(exactly = 0) { remoteDataSource.fetchPublicRepositories(any()) }
    }

    @Test
    fun `getPublicRepositories fetches from API and caches when no cache exists`() = runTest {
        // Given
        coEvery { repoDao.getAllRepoAt(1) } returns emptyList()
        val apiRepos = listOf(
            Repo(id = 2, name = "apiRepo", fullName = "owner/apiRepo", description = "desc", owner = testOwner, page = 0)
        )
        coEvery { remoteDataSource.fetchPublicRepositories(null) } returns NetworkResult.Success(200, apiRepos)
        coEvery { repoDao.insertRepoList(any()) } just runs

        var error: String? = null
        var started = false
        var completed = false

        // When
        val result = repository.getPublicRepositories(
            page = 1,
            since = null,
            clearCache = false,
            onStart = { started = true },
            onComplete = { completed = true },
            onError = { error = it }
        ).first()

        // Then
        assertTrue(started)
        assertTrue(completed)
        assertEquals(1, result.size)
        assertEquals("apiRepo", result[0].name)
        assertEquals(1, result[0].page) // Page should be set to 1
        assertNull(error)
        coVerify { repoDao.insertRepoList(any()) }
    }

    @Test
    fun `getPublicRepositories calls onError when API fails and no cache exists`() = runTest {
        // Given
        coEvery { repoDao.getAllRepoAt(1) } returns emptyList()
        coEvery { remoteDataSource.fetchPublicRepositories(null) } returns NetworkResult.Exception(Exception("API error"))

        var error: String? = null
        var started = false
        var completed = false

        // When
        val results = repository.getPublicRepositories(
            page = 1,
            since = null,
            clearCache = false,
            onStart = { started = true },
            onComplete = { completed = true },
            onError = { error = it }
        ).toList()

        // Then
        assertTrue(started)
        assertTrue(completed)
        assertTrue(results.isEmpty())
        assertNotNull(error)
    }

    @Test
    fun `getPublicRepositories clears cache when clearCache is true`() = runTest {
        // Given
        coEvery { repoDao.clearAll() } just runs
        coEvery { repoDao.getAllRepoAt(1) } returns emptyList()
        coEvery { remoteDataSource.fetchPublicRepositories(null) } returns NetworkResult.Success(200, emptyList())
        coEvery { repoDao.insertRepoList(any()) } just runs

        // When
        repository.getPublicRepositories(
            page = 1,
            since = null,
            clearCache = true,
            onStart = {},
            onComplete = {},
            onError = {}
        ).toList()

        // Then
        coVerify { repoDao.clearAll() }
    }

    @Test
    fun `getPublicRepositories uses since parameter for pagination`() = runTest {
        // Given
        coEvery { repoDao.getAllRepoAt(2) } returns emptyList()
        val apiRepos = listOf(
            Repo(id = 100, name = "repo100", fullName = "owner/repo100", description = "desc", owner = testOwner, page = 0)
        )
        coEvery { remoteDataSource.fetchPublicRepositories(99) } returns NetworkResult.Success(200, apiRepos)
        coEvery { repoDao.insertRepoList(any()) } just runs

        // When
        val result = repository.getPublicRepositories(
            page = 2,
            since = 99,
            clearCache = false,
            onStart = {},
            onComplete = {},
            onError = {}
        ).first()

        // Then
        assertEquals(1, result.size)
        coVerify { remoteDataSource.fetchPublicRepositories(99) }
    }

    @Test
    fun `getRepositoryDetails emits data on API success`() = runTest {
        // Given
        val repoInfo = RepoInfo(
            id = 3,
            name = "details",
            fullName = "owner/details",
            owner = testOwner
        )
        coEvery { remoteDataSource.getRepositoryDetails("owner", "repo") } returns NetworkResult.Success(200, repoInfo)

        var started = false
        var completed = false

        // When
        val result = repository.getRepositoryDetails(
            owner = "owner",
            repo = "repo",
            onStart = { started = true },
            onComplete = { completed = true },
            onError = {}
        ).first()

        // Then
        assertTrue(started)
        assertTrue(completed)
        assertEquals(repoInfo, result)
    }

    @Test
    fun `getRepositoryDetails calls onError on API failure`() = runTest {
        // Given
        coEvery { remoteDataSource.getRepositoryDetails("owner", "repo") } returns NetworkResult.Exception(Exception("Not found"))

        var error: String? = null
        var started = false
        var completed = false

        // When
        val results = repository.getRepositoryDetails(
            owner = "owner",
            repo = "repo",
            onStart = { started = true },
            onComplete = { completed = true },
            onError = { error = it }
        ).toList()

        // Then
        assertTrue(started)
        assertTrue(completed)
        assertTrue(results.isEmpty())
        assertNotNull(error)
    }

    @Test
    fun `getReadMeContent emits data on API success`() = runTest {
        // Given
        val readmeContent = ReadmeContent(
            name = "README.md",
            content = "README content",
            encoding = "base64"
        )
        coEvery { remoteDataSource.getReadMeContent("owner", "repo") } returns NetworkResult.Success(200, readmeContent)

        // When
        val result = repository.getReadMeContent(
            owner = "owner",
            repo = "repo",
            onError = {}
        ).first()

        // Then
        assertEquals(readmeContent, result)
    }

    @Test
    fun `getReadMeContent calls onError on API failure`() = runTest {
        // Given
        coEvery { remoteDataSource.getReadMeContent("owner", "repo") } returns NetworkResult.Exception(Exception("fail"))

        var error: String? = null

        // When
        val results = repository.getReadMeContent(
            owner = "owner",
            repo = "repo",
            onError = { error = it }
        ).toList()

        // Then
        assertTrue(results.isEmpty())
        assertNotNull(error)
    }
}
