package com.anos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ReadmeContent(
    @SerialName(value = "name")
    val name: String? = null,

    @SerialName(value = "path")
    val path: String? = null,

    @SerialName(value = "html_url")
    val htmlUrl: String? = null,

    @SerialName(value = "download_url")
    val downloadUrl: String? = null,

    @SerialName(value = "content")
    val content: String? = null,

    @SerialName(value = "encoding")
    val encoding: String? = null,
) : Parcelable
