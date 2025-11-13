package com.anos.common.di

import com.anos.common.AppDispatchers
import com.anos.common.Dispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Custom qualifier annotation to distinguish application-level coroutine scope
 * from other CoroutineScope instances during dependency injection.
 *
 * This prevents ambiguity when multiple scopes are available (e.g., viewModelScope,
 * lifecycleScope, applicationScope).
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

/**
 * Dagger Hilt module that provides application-level coroutine scopes.
 *
 * This module is installed in SingletonComponent, meaning the provided
 * coroutine scope will live for the entire application lifecycle and
 * survive UI component destruction/recreation.
 *
 * Use cases:
 * - Long-running background operations (data sync, caching)
 * - Repository-level operations that shouldn't be cancelled when UI changes
 * - Global event buses or managers
 * - Operations that need to persist across screen navigation
 */
@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineScopesModule {

    /**
     * Provides a singleton application-scoped CoroutineScope.
     *
     * @param dispatcher The coroutine dispatcher (injected as Default dispatcher)
     * @return CoroutineScope configured for application-level operations
     *
     * Key characteristics:
     * - SupervisorJob(): Child coroutine failures don't cancel the entire scope
     * - Default dispatcher: Optimized for CPU-intensive background work
     * - Singleton: One instance shared across the entire application
     * - Survives UI lifecycle: Won't be cancelled when Activities/Fragments are destroyed
     *
     * Example usage:
     * ```kotlin
     * class DataRepository @Inject constructor(
     *     @ApplicationScope private val appScope: CoroutineScope
     * ) {
     *     fun syncDataInBackground() {
     *         appScope.launch {
     *             // Long-running sync operation
     *         }
     *     }
     * }
     * ```
     */
    @Provides
    @Singleton
    @ApplicationScope
    fun providesCoroutineScope(
        @Dispatcher(AppDispatchers.Default) dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}
