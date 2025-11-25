package com.anos.common.di

import com.anos.common.AppDispatchers
import com.anos.common.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/**
 * For now using @Named qualifier manual bridge
 */
@Module
@Configuration
object DispatchersModule {

    @Single // formally Provides in Hilt version
    @Dispatcher(AppDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Single // formally Provides in Hilt version
    @Dispatcher(AppDispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
