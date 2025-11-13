package com.anos.database.di

import android.app.Application
import androidx.room.Room
import com.anos.database.GitHubRepoDatabase
import com.anos.database.RepoTypeConverter
import com.anos.database.RepoDao
import com.anos.database.RepoInfoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        application: Application,
        ownerInfoConverter: RepoTypeConverter,
    ): GitHubRepoDatabase {
        return Room
            .databaseBuilder(application, GitHubRepoDatabase::class.java, "gitbrowse.db")
            .fallbackToDestructiveMigration()
            .addTypeConverter(ownerInfoConverter)
            .build()
    }

    @Provides
    @Singleton
    fun provideRepoDao(appDatabase: GitHubRepoDatabase): RepoDao {
        return appDatabase.repoDao()
    }

    @Provides
    @Singleton
    fun provideRepoInfoDao(appDatabase: GitHubRepoDatabase): RepoInfoDao {
        return appDatabase.repoInfoDao()
    }

    @Provides
    @Singleton
    fun provideTypeConverter(json: Json): RepoTypeConverter {
        return RepoTypeConverter(json)
    }
}
