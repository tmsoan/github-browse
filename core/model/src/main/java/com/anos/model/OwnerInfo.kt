package com.anos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class OwnerInfo(
    @SerialName(value = "login")
    val login: String,

    @SerialName(value = "avatar_url")
    val avatarUrl: String? = null,

    @SerialName(value = "html_url")
    val htmlUrl: String? = null,
) : Parcelable

