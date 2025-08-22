package com.example.templepocforground.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val KEY_SOCKET_URL = "socket_url"
        private const val KEY_USERNAME = "username"
        private const val KEY_USERID = "userid"
        private const val IS_STOPPED = "is_stoped"
    }

    fun saveSocketUrl(url: String) {
        sharedPreferences.edit { putString(KEY_SOCKET_URL, url) }
    }

    fun getSocketUrl(): String? {
        return sharedPreferences.getString(KEY_SOCKET_URL, null)
    }

    fun saveUsername(username: String) {
        sharedPreferences.edit { putString(KEY_USERNAME, username) }
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    fun saveUserId(username: String) {
        sharedPreferences.edit { putString(KEY_USERID, username) }
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USERID, null)
    }

    fun setStopped(value: Boolean) {
        sharedPreferences.edit { putBoolean("IS_STOPPED", value) }
    }

    fun isStopped(): Boolean {
        return sharedPreferences.getBoolean("IS_STOPPED", false)
    }

    fun clearAll() {
        sharedPreferences.edit { clear() }
    }
}
