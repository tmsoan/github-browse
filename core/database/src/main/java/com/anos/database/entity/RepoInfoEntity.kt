package com.anos.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anos.model.OwnerInfo

@Entity
data class RepoInfoEntity(
  @PrimaryKey
  val id: Int,
  val name: String,
  val fullName: String,
  val description: String,
  val homepage: String,
  val language: String,
  val forksCount: Int = 0,
  val stargazersCount: Int = 0,
  val subscribersCount: Int = 0,
  val visibility: String,
  val openIssues: Int = 0,
  val topics: List<String>,
  val url: String,
  val updatedAt: String,
  val owner: OwnerInfo,
)
