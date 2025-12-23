package com.anos.home.ui

import app.cash.turbine.test
import com.anos.domain.usecase.GetRepositoriesUseCase
import com.anos.model.OwnerInfo
import com.anos.model.Repo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var getRepositoriesUseCase: GetRepositoriesUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockRepoList = listOf(
        Repo(
            id = 1,
            name = "repo1",
            fullName = "owner/repo1",
            description = "Test repository 1",
            owner = OwnerInfo(
                login = "owner",
                avatarUrl = "https://example.com/avatar.png"
            ),
            page = 0
        ),
        Repo(
            id = 2,
            name = "repo2",
            fullName = "owner/repo2",
            description = "Test repository 2",
            owner = OwnerInfo(
                login = "owner2",
                avatarUrl = "https://example.com/avatar2.png"
            ),
            page = 0
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getRepositoriesUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() = runTest {
        // Given
        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = any(),
                onComplete = any(),
                onError = any()
            )
        } returns flow { emit(emptyList()) }

        // When
        viewModel = HomeViewModel(getRepositoriesUseCase)

        // Then
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `fetchRepoList should update uiState to Loading then Idle on success`() = runTest {
        // Given
        val onStartSlot = slot<() -> Unit>()
        val onCompleteSlot = slot<() -> Unit>()

        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = capture(onStartSlot),
                onComplete = capture(onCompleteSlot),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            emit(mockRepoList)
            onCompleteSlot.captured.invoke()
        }

        // When
        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        // Then
        assertEquals(HomeUiState.Idle, viewModel.uiState.value)

        // Verify filteredRepoList by collecting it
        viewModel.filteredRepoList.test {
            val items = awaitItem()
            assertEquals(mockRepoList.size, items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchRepoList should update uiState to Error on failure`() = runTest {
        // Given
        val errorMessage = "Network error"
        val onStartSlot = slot<() -> Unit>()
        val onErrorSlot = slot<(String?) -> Unit>()

        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = capture(onStartSlot),
                onComplete = any(),
                onError = capture(onErrorSlot)
            )
        } returns flow {
            onStartSlot.captured.invoke()
            onErrorSlot.captured.invoke(errorMessage)
        }

        // When
        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is HomeUiState.Error)
        assertEquals(errorMessage, (viewModel.uiState.value as HomeUiState.Error).message)
    }

    @Test
    fun `fetchRepoList should increment page index after successful fetch`() = runTest {
        // Given
        val onStartSlot = slot<() -> Unit>()
        val onCompleteSlot = slot<() -> Unit>()

        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = capture(onStartSlot),
                onComplete = capture(onCompleteSlot),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            emit(mockRepoList)
            onCompleteSlot.captured.invoke()
        }

        // When
        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        // Verify first call with page 0 and since = null
        coVerify { getRepositoriesUseCase(page = 0, since = null, clearCache = false, any(), any(), any()) }

        // Fetch next page
        viewModel.fetchNextRepoList()
        advanceUntilIdle()

        // Then - should call with page 1 and since = last repo id (2)
        coVerify { getRepositoriesUseCase(page = 1, since = 2, clearCache = false, any(), any(), any()) }
    }

    @Test
    fun `updateQueryContent should update queryContent state`() = runTest {
        // Given
        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = any(),
                onComplete = any(),
                onError = any()
            )
        } returns flow { emit(emptyList()) }

        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        val query = "test query"

        // When
        viewModel.updateQueryContent(query)
        advanceUntilIdle()

        // Then
        assertEquals(query, viewModel.queryContent.value)
    }

    @Test
    fun `filteredRepoList should filter by fullName when query is not blank`() = runTest {
        // Given
        val onStartSlot = slot<() -> Unit>()
        val onCompleteSlot = slot<() -> Unit>()

        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = capture(onStartSlot),
                onComplete = capture(onCompleteSlot),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            emit(mockRepoList)
            onCompleteSlot.captured.invoke()
        }

        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        // When
        viewModel.updateQueryContent("repo1")
        advanceUntilIdle()

        // Then
        viewModel.filteredRepoList.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("repo1", items[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filteredRepoList should filter by description when query is not blank`() = runTest {
        // Given
        val onStartSlot = slot<() -> Unit>()
        val onCompleteSlot = slot<() -> Unit>()

        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = capture(onStartSlot),
                onComplete = capture(onCompleteSlot),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            emit(mockRepoList)
            onCompleteSlot.captured.invoke()
        }

        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        // When
        viewModel.updateQueryContent("repository 2")
        advanceUntilIdle()

        viewModel.filteredRepoList.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("repo2", items[0].name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filteredRepoList should return all repos when query is blank`() = runTest(testDispatcher) {
        // Given
        val onStartSlot = slot<() -> Unit>()
        val onCompleteSlot = slot<() -> Unit>()

        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = capture(onStartSlot),
                onComplete = capture(onCompleteSlot),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            emit(mockRepoList)
            onCompleteSlot.captured.invoke()
        }

        // When
        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        // Then - collect the StateFlow
        viewModel.filteredRepoList.test {
            val items = awaitItem()
            assertEquals(mockRepoList.size, items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filteredRepoList should be case insensitive`() = runTest {
        // Given
        val onStartSlot = slot<() -> Unit>()
        val onCompleteSlot = slot<() -> Unit>()

        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = capture(onStartSlot),
                onComplete = capture(onCompleteSlot),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            emit(mockRepoList)
            onCompleteSlot.captured.invoke()
        }

        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        // When - search with uppercase
        viewModel.updateQueryContent("REPO1")
        advanceUntilIdle()

        // Then
        viewModel.filteredRepoList.test {
            val filteredItems = awaitItem()
            assertEquals(1, filteredItems.size)
            assertEquals("repo1", filteredItems[0].name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refreshRepoList should clear repoList and fetch again with clearCache true`() = runTest {
        // Given
        val onStartSlot = slot<() -> Unit>()
        val onCompleteSlot = slot<() -> Unit>()

        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = capture(onStartSlot),
                onComplete = capture(onCompleteSlot),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            emit(mockRepoList)
            onCompleteSlot.captured.invoke()
        }

        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        // Verify initial fetch with page = 0, since = null, clearCache = false
        coVerify(exactly = 1) { getRepositoriesUseCase(page = 0, since = null, clearCache = false, any(), any(), any()) }

        // When - refresh the repo list
        viewModel.refreshRepoList()
        advanceUntilIdle()

        // Then - should call fetchRepoList again with page = 0, since = null, clearCache = true
        coVerify(exactly = 1) { getRepositoriesUseCase(page = 0, since = null, clearCache = true, any(), any(), any()) }

        // Verify that the list was cleared and refetched
        viewModel.filteredRepoList.test {
            val items = awaitItem()
            // After refresh, list is cleared then refetched, so we have mockRepoList
            assertEquals(mockRepoList.size, items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchNextRepoList should not fetch if already loading`() = runTest {
        // Given
        val onStartSlot = slot<() -> Unit>()

        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = capture(onStartSlot),
                onComplete = any(),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            // Don't complete - stay in loading state
            emit(mockRepoList)
            // No onComplete call
        }

        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        // Verify state is still loading (onComplete was not called)
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)

        // When - try to fetch next while loading
        viewModel.fetchNextRepoList()
        viewModel.fetchNextRepoList()
        viewModel.fetchNextRepoList()
        advanceUntilIdle()

        // Then - should not call the use case again (still 1 call from init)
        coVerify(exactly = 1) { getRepositoriesUseCase(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `fetchRepoList should accumulate repos from multiple pages`() = runTest {
        // Given
        val onStartSlot = slot<() -> Unit>()
        val onCompleteSlot = slot<() -> Unit>()

        val page1Repos = listOf(
            Repo(id = 1, name = "repo1", fullName = "owner/repo1", owner = mockRepoList[0].owner, page = 0)
        )
        val page2Repos = listOf(
            Repo(id = 2, name = "repo2", fullName = "owner/repo2", owner = mockRepoList[1].owner, page = 1)
        )

        coEvery {
            getRepositoriesUseCase(
                page = 0,
                since = null,
                clearCache = false,
                onStart = capture(onStartSlot),
                onComplete = capture(onCompleteSlot),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            emit(page1Repos)
            onCompleteSlot.captured.invoke()
        }

        coEvery {
            getRepositoriesUseCase(
                page = 1,
                since = 1,
                clearCache = false,
                onStart = any(),
                onComplete = any(),
                onError = any()
            )
        } returns flow {
            emit(page2Repos)
        }

        // When
        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        viewModel.fetchNextRepoList()
        advanceUntilIdle()

        // Then - repos should be accumulated
        viewModel.filteredRepoList.test {
            val items = awaitItem()
            assertEquals(2, items.size) // Should have repos from both pages
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filteredRepoList should return empty list when no matches found`() = runTest {
        // Given
        val onStartSlot = slot<() -> Unit>()
        val onCompleteSlot = slot<() -> Unit>()

        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = capture(onStartSlot),
                onComplete = capture(onCompleteSlot),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            emit(mockRepoList)
            onCompleteSlot.captured.invoke()
        }

        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        // When - search for non-existent repo
        viewModel.updateQueryContent("nonexistent")
        advanceUntilIdle()

        // Then
        viewModel.filteredRepoList.test {
            val filteredItems = awaitItem()
            assertEquals(0, filteredItems.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refreshRepoList should not refresh if already loading`() = runTest {
        // Given
        val onStartSlot = slot<() -> Unit>()

        coEvery {
            getRepositoriesUseCase(
                page = any(),
                since = any(),
                clearCache = any(),
                onStart = capture(onStartSlot),
                onComplete = any(),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            // Don't complete - stay in loading state
            emit(mockRepoList)
        }

        viewModel = HomeViewModel(getRepositoriesUseCase)
        advanceUntilIdle()

        // Verify state is loading
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)

        // When - try to refresh while loading
        viewModel.refreshRepoList()
        advanceUntilIdle()

        // Then - should not call the use case again
        coVerify(exactly = 1) { getRepositoriesUseCase(any(), any(), any(), any(), any(), any()) }
    }
}
