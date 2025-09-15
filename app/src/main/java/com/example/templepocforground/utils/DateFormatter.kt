package com.example.templepocforground.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

fun getCurrentFormattedTime(): String {
    val date = Date()
    val formatter = SimpleDateFormat("MMM dd, hh:mm:ss a", Locale.ENGLISH)
    return formatter.format(date)
}

fun formatIsoToReadable(isoString: String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val zonedDateTime = ZonedDateTime.parse(isoString)
            .withZoneSameInstant(java.time.ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm:ss a", Locale.ENGLISH)
        zonedDateTime.format(formatter)
    } else {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(isoString)

        val outputFormat = SimpleDateFormat("MMM dd, hh:mm:ss a", Locale.ENGLISH)
        outputFormat.timeZone = TimeZone.getDefault()
        outputFormat.format(date!!)
    }
}

fun parseDate(isoString: String): Long {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.parse(isoString).toEpochMilli()
        } else {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .parse(isoString)?.time ?: 0L
        }
    } catch (e: Exception) {
        0L
    }

}

