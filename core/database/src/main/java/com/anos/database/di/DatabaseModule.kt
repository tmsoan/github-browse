package com.anos.database.di

import android.app.Application
import androidx.room.Room
import com.anos.database.GitHubRepoDatabase
import com.anos.database.RepoTypeConverter
import com.anos.database.RepoDao
import com.anos.database.RepoInfoDao
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Module
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
    @Single // formally Provides in Hilt version
    fun provideRepoDao(appDatabase: GitHubRepoDatabase): RepoDao {
        return appDatabase.repoDao()
    }

    @Single // formally Provides in Hilt version
    fun provideRepoInfoDao(appDatabase: GitHubRepoDatabase): RepoInfoDao {
        return appDatabase.repoInfoDao()
    }

    @Single // formally Provides in Hilt version
    fun provideTypeConverter(@InjectedParam json: Json): RepoTypeConverter {
        return RepoTypeConverter(json)
    }
}
