package com.anos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Repo(
    val page: Int = 0,

    @SerialName(value = "id")
    val id: Int,

    @SerialName(value = "name")
    val name: String? = null,

    @SerialName(value = "full_name")
    val fullName: String? = null,

    @SerialName(value = "description")
    val description: String? = null,

    @SerialName(value = "owner")
    val owner: OwnerInfo,
) : Parcelable {

    fun contains(query: String): Boolean {
        val lowerCaseQuery = query.lowercase()
        return (fullName?.lowercase()?.contains(lowerCaseQuery) == true) ||
               (description?.lowercase()?.contains(lowerCaseQuery) == true)
    }
}