package com.anos.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anos.model.OwnerInfo

@Entity
data class RepoEntity(
  val page: Int,
  @PrimaryKey
  val id: Int,
  val name: String,
  val fullName: String,
  val description: String,
  val owner: OwnerInfo,
)
