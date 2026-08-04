package com.anos.database.di

import android.app.Application
import androidx.room.Room
import com.anos.database.GitHubRepoDatabase
import com.anos.database.RepoTypeConverter
import com.anos.database.RepoDao
import com.anos.database.RepoInfoDao
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Module
internal object DatabaseModule {
    @Single
    fun provideAppDatabase(
        application: Application,
        ownerInfoConverter: RepoTypeConverter,
    ): GitHubRepoDatabase {
        return Room
            .databaseBuilder(application, GitHubRepoDatabase::class.java, "gitbrowse.db")
            .addTypeConverter(ownerInfoConverter)
            .build()
    }
}

@Module(includes = [DatabaseModule::class])
@Configuration
class DaoModule {
    @Single
    fun provideRepoDao(appDatabase: GitHubRepoDatabase): RepoDao {
        return appDatabase.repoDao()
    }

    @Single
    fun provideRepoInfoDao(appDatabase: GitHubRepoDatabase): RepoInfoDao {
        return appDatabase.repoInfoDao()
    }

    // [Json] is provided by :core:network, which this module does not depend on at compile time,
    // hence @Provided to keep KOIN_CONFIG_CHECK happy.
    @Single
    fun provideTypeConverter(@Provided json: Json): RepoTypeConverter {
        return RepoTypeConverter(json)
    }
}
