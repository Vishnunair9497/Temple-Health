package com.example.templepocforground.screens.login

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.templepocforground.models.AuthResponse
import com.example.templepocforground.repository.AuthRepository
import com.example.templepocforground.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    var loginState by mutableStateOf<Resource<AuthResponse>>(Resource.Idle)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginState = Resource.Loading
            loginState = try {
                val response = repository.login(email, password)
                if (response.isSuccessful) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Login failed")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun showBiometricPrompt(
        activity: FragmentActivity, onSuccess: () -> Unit, onFail: () -> Unit, onError: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(
            activity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(activity, "Authentication Error", Toast.LENGTH_SHORT).show()
                    onError()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(activity, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    onFail()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("Biometric Login")
            .setSubtitle("Use your fingerprint or face to login").setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}