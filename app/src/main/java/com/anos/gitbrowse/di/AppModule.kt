package com.anos.gitbrowse.di

import com.anos.gitbrowse.MainActivityViewModel
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module

@Module(includes = [HomeModule::class, DomainModule::class])
@Configuration
class AppModule {
    // keep here to avoid ComponentScan scanning too much in other components
    @KoinViewModel
    fun mainActivityViewModel() = MainActivityViewModel()
}

@Module
@ComponentScan("com.anos.home.ui")
class HomeModule

@Module
@ComponentScan("com.anos.domain")
class DomainModule
