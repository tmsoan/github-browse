package com.anos.data.di

import com.anos.data.repository.GitHubRepositoryImpl
import com.anos.domain.repository.GitHubRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindGitHubRepository(impl: GitHubRepositoryImpl): GitHubRepository
}
