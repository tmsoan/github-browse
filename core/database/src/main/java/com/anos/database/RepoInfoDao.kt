package com.anos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anos.database.entity.RepoInfoEntity

@Dao
interface RepoInfoDao {

    @Query("SELECT * FROM RepoInfoEntity WHERE id = :id_")
    suspend fun getRepoInfoBy(id_: Int): List<RepoInfoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepoInfo(repo: RepoInfoEntity)
}
