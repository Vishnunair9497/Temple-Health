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
        private const val KEY_FCM_TOKEN = "last_fcm_token"
        private const val KEY_DEVICE_ID = "last_registered_device_id"
        private const val FIRST_TIME = "firstTimeLogin"
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

    fun saveDeviceRegistration(deviceId: String, token: String) {
        sharedPreferences.edit {
            putString(KEY_DEVICE_ID, deviceId)
            putString(KEY_FCM_TOKEN, token)
        }
    }

    fun isAlreadyDeviceRegistered(deviceId: String, token: String): Boolean {
        val savedDeviceId = sharedPreferences.getString(KEY_DEVICE_ID, null)
        val savedToken = sharedPreferences.getString(KEY_FCM_TOKEN, null)
        return savedDeviceId == deviceId && savedToken == token
    }

    fun saveFirstTime(login: Boolean) {
        sharedPreferences.edit { putBoolean(FIRST_TIME, login) }
    }

    fun getFirstTime(): Boolean? {
        return sharedPreferences.getBoolean(FIRST_TIME, true)
    }


    fun clearAll() {
        sharedPreferences.edit { clear().apply() }
    }
}
