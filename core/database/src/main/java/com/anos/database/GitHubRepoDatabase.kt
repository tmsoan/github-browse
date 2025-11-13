package com.anos.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anos.database.entity.RepoEntity
import com.anos.database.entity.RepoInfoEntity

@Database(
    entities = [RepoEntity::class, RepoInfoEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(value = [RepoTypeConverter::class])
abstract class GitHubRepoDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun repoInfoDao(): RepoInfoDao
}
