package com.anos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class RepoInfo(
    @SerialName(value = "id")
    val id: Int,

    @SerialName(value = "name")
    val name: String? = null,

    @SerialName(value = "full_name")
    val fullName: String? = null,

    @SerialName(value = "description")
    val description: String? = null,

    @SerialName(value = "homepage")
    val homepage: String? = null,

    @SerialName(value = "language")
    val language: String? = null,

    @SerialName(value = "forks_count")
    val forksCount: Int = 0,

    @SerialName(value = "stargazers_count")
    val stargazersCount: Int = 0,

    @SerialName(value = "subscribers_count")
    val subscribersCount: Int = 0,

    @SerialName(value = "visibility")
    val visibility: String? = null, // "public"

    @SerialName(value = "open_issues")
    val openIssues: Int = 0,

    @SerialName(value = "topics")
    val topics: List<String>? = null,

    @SerialName(value = "url")
    val url: String? = null,

    @SerialName(value = "updated_at")
    val updatedAt: String? = null, // "2025-05-30T07:56:32Z"

    @SerialName(value = "owner")
    val owner: OwnerInfo,
) : Parcelable