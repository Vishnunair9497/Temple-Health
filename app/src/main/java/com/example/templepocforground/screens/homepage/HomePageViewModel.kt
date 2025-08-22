package com.example.templepocforground.screens.homepage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.templepocforground.models.NegotiateModel
import com.example.templepocforground.repository.AuthRepository
import com.example.templepocforground.utils.Resource
import com.example.templepocforground.utils.SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val repository: AuthRepository, private val prefs: SharedPrefsManager

) : ViewModel() {

    var socketState by mutableStateOf<Resource<NegotiateModel>>(Resource.Idle)
        private set

    fun getSocketUrl(uid: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            socketState = Resource.Loading
            socketState = try {
                val response = repository.getSocketUrl(uid)
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.url?.let { prefs.saveSocketUrl(it) }
                    onSuccess()
                    Resource.Success(body!!)
                } else {
                    Resource.Error("fetch failed")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun stopAlerts(notificationId: String?, userId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = notificationId?.let { repository.stopAlertsApi(it, userId) }
                response?.let {
                    if (it.isSuccessful) {
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun getSavedSocketUrl(): String? = prefs.getSocketUrl()

    fun saveUsername(username: String) = prefs.saveUsername(username)
    fun getSavedUsername(): String? = prefs.getUsername()

    fun saveUserId(username: String) = prefs.saveUserId(username)
    fun getSavedUserId(): String? = prefs.getUserId()

    fun setStop(bool: Boolean) {
        prefs.setStopped(bool)
    }

}