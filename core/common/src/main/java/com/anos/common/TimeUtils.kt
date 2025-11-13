package com.anos.common

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object TimeFormat {
    const val UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val FULL_FORMAT_1 = "yyyy-MM-dd HH:mm"
}

fun convertUtcToLocal(
    utcTime: String,
    inputFormat: String = TimeFormat.UTC,
    outputFormat: String = TimeFormat.FULL_FORMAT_1
) : String {
    return try {
        val sdfUtc = SimpleDateFormat(inputFormat, Locale.getDefault())
        sdfUtc.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdfUtc.parse(utcTime)
        val sdfLocal = SimpleDateFormat(outputFormat, Locale.getDefault())
        sdfLocal.timeZone = TimeZone.getDefault()
        date?.let { sdfLocal.format(it) } ?: ""
    } catch (e: Exception) {
        ""
    }
}
