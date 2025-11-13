package com.anos.common

import android.util.Log
import kotlin.io.encoding.Base64

fun decodeBase64(content: String): String? {
    return try {
        val clean = content.replace("\n", "")
        String(Base64.decode(clean))
    } catch (e: IllegalArgumentException) {
        Log.e("Utils", "Failed to decode Base64", e)
        null
    }
}
