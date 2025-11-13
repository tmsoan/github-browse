package com.anos.details.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.anos.domain.usecase.GetReadMeContentUseCase
import com.anos.domain.usecase.GetRepositoryDetailsUseCase
import com.anos.model.OwnerInfo
import com.anos.model.ReadmeContent
import com.anos.model.RepoInfo
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class RepoDetailsViewModelTest {
    private lateinit var viewModel: RepoDetailsViewModel
    private lateinit var getRepositoryDetailsUseCase: GetRepositoryDetailsUseCase
    private lateinit var getReadMeContentUseCase: GetReadMeContentUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()

    private val mockRepoInfo = RepoInfo(
        id = 1,
        name = "test-repo",
        fullName = "owner/test-repo",
        description = "Test repository description",
        owner = OwnerInfo(
            login = "owner",
            avatarUrl = "https://example.com/avatar.png"
        ),
        stargazersCount = 100,
        forksCount = 10,
        updatedAt = "2024-01-01T00:00:00Z",
        homepage = "https://example.com",
        topics = listOf("kotlin", "android", "compose")
    )

    private val mockReadmeContent = ReadmeContent(
        name = "README.md",
        path = "README.md",
        downloadUrl = "https://example.com/readme",
        content = "VGVzdCBSRUFETUU="  // Base64 encoded "Test README"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        getRepositoryDetailsUseCase = mockk(relaxed = true)
        getReadMeContentUseCase = mockk(relaxed = true)

        val initialRepoInfo = RepoInfo(
            id = 1,
            name = "test-repo",
            owner = OwnerInfo(
                login = "owner",
            ),
        )
        savedStateHandle = SavedStateHandle(
            mapOf(
                "repoId" to initialRepoInfo.id,
                "owner" to initialRepoInfo.owner.login,
                "name" to initialRepoInfo.name
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() {
        // When
        viewModel = RepoDetailsViewModel(
            getRepositoryDetailsUseCase,
            getReadMeContentUseCase,
            savedStateHandle
        )

        // Then
        assertEquals(DetailsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `initial repoInfo should be null on some values`() = runTest {
        // Given
        coEvery {
            getRepositoryDetailsUseCase(any(), any(), any(), any(), any())
        } returns flow { emit(mockRepoInfo) }

        coEvery {
            getReadMeContentUseCase(any(), any(), any())
        } returns flow { emit(mockReadmeContent) }

        // When
        viewModel = RepoDetailsViewModel(
            getRepositoryDetailsUseCase,
            getReadMeContentUseCase,
            savedStateHandle
        )

        // Then
        assertTrue(viewModel.repoInfo.value?.forksCount == 0)
        assertTrue(viewModel.repoInfo.value?.stargazersCount == 0)
        assertNull(viewModel.repoInfo.value?.updatedAt)
    }

    @Test
    fun `initial repoInfo should trigger repository details fetch`() = runTest {
        // Given
        val onStartSlot = slot<() -> Unit>()
        val onCompleteSlot = slot<() -> Unit>()

        coEvery {
            getRepositoryDetailsUseCase(
                owner = "owner",
                repo = "test-repo",
                onStart = capture(onStartSlot),
                onComplete = capture(onCompleteSlot),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            emit(mockRepoInfo)
            onCompleteSlot.captured.invoke()
        }

        coEvery {
            getReadMeContentUseCase(any(), any(), any())
        } returns flow { emit(mockReadmeContent) }

        viewModel = RepoDetailsViewModel(
            getRepositoryDetailsUseCase,
            getReadMeContentUseCase,
            savedStateHandle
        )

        // Then
        assertEquals(DetailsUiState.Loading, viewModel.uiState.value)

        viewModel.repoInfo.test {
            val initialValue = awaitItem()
            assertNull(initialValue?.updatedAt)
            val items = awaitItem()
            assertEquals(DetailsUiState.Idle, viewModel.uiState.value)
            assertEquals(mockRepoInfo.id, items?.id)
            assertTrue(items?.updatedAt != null)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial repoInfo should update uiState to Loading then Idle on success`() = runTest {
        // Given
        val onStartSlot = slot<() -> Unit>()
        val onCompleteSlot = slot<() -> Unit>()

        coEvery {
            getRepositoryDetailsUseCase(
                owner = any(),
                repo = any(),
                onStart = capture(onStartSlot),
                onComplete = capture(onCompleteSlot),
                onError = any()
            )
        } returns flow {
            onStartSlot.captured.invoke()
            emit(mockRepoInfo)
            onCompleteSlot.captured.invoke()
        }

        coEvery {
            getReadMeContentUseCase(any(), any(), any())
        } returns flow { emit(mockReadmeContent) }

        viewModel = RepoDetailsViewModel(
            getRepositoryDetailsUseCase,
            getReadMeContentUseCase,
            savedStateHandle
        )

        // Then
        assertEquals(DetailsUiState.Loading, viewModel.uiState.value)

        viewModel.repoInfo.test {
            val initialValue = awaitItem()
            val items = awaitItem()
            assertEquals(DetailsUiState.Idle, viewModel.uiState.value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial repoInfo should update uiState to Error on failure`() = runTest {
        // Given
        val errorMessage = "Network error"
        val onStartSlot = slot<() -> Unit>()
        val onErrorSlot = slot<(String?) -> Unit>()

        coEvery {
            getRepositoryDetailsUseCase(
                owner = any(),
                repo = any(),
                onStart = capture(onStartSlot),
                onComplete = any(),
                onError = capture(onErrorSlot)
            )
        } returns flow {
            onStartSlot.captured.invoke()
            onErrorSlot.captured.invoke(errorMessage)
            // emit to simulate failure
            emit(mockRepoInfo)
        }

        viewModel = RepoDetailsViewModel(
            getRepositoryDetailsUseCase,
            getReadMeContentUseCase,
            savedStateHandle
        )

        viewModel.repoInfo.test {
            val initialValue = awaitItem()
            val item = awaitItem()
            // Then
            assertTrue(viewModel.uiState.value is DetailsUiState.Error)
            assertEquals(errorMessage, (viewModel.uiState.value as DetailsUiState.Error).message)
        }
    }

    @Test
    fun `readMeContent should emit readme after setRepoInfo`() = runTest {
        // Given
        coEvery {
            getRepositoryDetailsUseCase(any(), any(), any(), any(), any())
        } returns flow { emit(mockRepoInfo) }

        coEvery {
            getReadMeContentUseCase(
                owner = "owner",
                repo = "test-repo",
                onError = any()
            )
        } returns flow { emit(mockReadmeContent) }

        viewModel = RepoDetailsViewModel(
            getRepositoryDetailsUseCase,
            getReadMeContentUseCase,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then
        viewModel.readMeContent.test {
            val initialValue = awaitItem()
            assertNull(initialValue)

            val readme = awaitItem()
            assertEquals(mockReadmeContent.name, readme?.name)
            assertEquals(mockReadmeContent.content, readme?.content)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `readMeContent should handle error gracefully`() = runTest {
        // Given
        val onErrorSlot = slot<(String?) -> Unit>()

        coEvery {
            getRepositoryDetailsUseCase(any(), any(), any(), any(), any())
        } returns flow { emit(mockRepoInfo) }

        coEvery {
            getReadMeContentUseCase(
                owner = any(),
                repo = any(),
                onError = capture(onErrorSlot)
            )
        } returns flow {
            onErrorSlot.captured.invoke("README not found")
        }

        viewModel = RepoDetailsViewModel(
            getRepositoryDetailsUseCase,
            getReadMeContentUseCase,
            savedStateHandle
        )
        advanceUntilIdle()

        // Then - should not crash, uiState should remain Loading or Idle (not Error for README)
        assertTrue(
            viewModel.uiState.value is DetailsUiState.Loading ||
            viewModel.uiState.value is DetailsUiState.Idle
        )
    }
}
