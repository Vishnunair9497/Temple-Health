package com.example.templepocforground.utils

import android.os.Build
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDateTime(isoString: String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val zonedDateTime = ZonedDateTime.parse(isoString)
            .withZoneSameInstant(java.time.ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm:ss a", Locale.ENGLISH)
        zonedDateTime.format(formatter)
    } else {
        try {
            val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = parser.parse(isoString)

            val formatter = java.text.SimpleDateFormat("MMM dd, hh:mm:ss a", Locale.ENGLISH)
            formatter.timeZone = java.util.TimeZone.getDefault()
            formatter.format(date!!)
        } catch (e: Exception) {
            isoString
        }
    }
}

