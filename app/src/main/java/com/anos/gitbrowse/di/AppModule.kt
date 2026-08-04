package com.anos.gitbrowse.di

import com.anos.gitbrowse.MainActivityViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module

/**
 * App-level definitions. Every other module (`:core:*`, `:feature:*`) declares its own
 * `@Configuration` module, which Koin aggregates into [com.anos.gitbrowse.GitBrowseApp]'s
 * generated `startKoin`, so nothing has to be included by hand here.
 */
@Module
@Configuration
class AppModule {
    // keep here to avoid ComponentScan scanning too much in other components
    @KoinViewModel
    fun mainActivityViewModel() = MainActivityViewModel()
}
