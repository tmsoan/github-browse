package com.anos.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anos.database.entity.RepoEntity

@Dao
interface RepoDao {

    @Query("SELECT * FROM RepoEntity WHERE id = :id_")
    suspend fun getRepoBy(id_: Int): List<RepoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepoList(repos: List<RepoEntity>)

    @Query("SELECT * FROM RepoEntity WHERE page = :page_")
    suspend fun getAllRepoAt(page_: Int): List<RepoEntity>

    @Query("DELETE FROM RepoEntity")
    suspend fun clearAll()
}