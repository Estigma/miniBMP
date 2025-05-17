package com.ups.minibmp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ups.minibmp.services.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun loginWithCredentials(email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val success = authRepo.login(email, password)
            _uiState.value = if (success) {
                val hasToken = authRepo.hasToken()
                AuthUiState.Success(needsTokenSetup = !hasToken)
            } else {
                AuthUiState.Error("Credenciales incorrectas")
            }
        }
    }

    fun loginWithToken(token: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val success = authRepo.loginWithToken(token)
            _uiState.value = if (success) {
                AuthUiState.Success(needsTokenSetup = false)
            } else {
                AuthUiState.Error("Token inválido")
            }
        }
    }

    fun setupToken(token: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val success = authRepo.setupToken(token)
            _uiState.value = if (success) {
                AuthUiState.Success(needsTokenSetup = false)
            } else {
                AuthUiState.Error("Error al guardar token")
            }
        }
    }

    fun noSetupToken() {
        _uiState.value = AuthUiState.Success(needsTokenSetup = false)
    }

    fun setError(message: String) {
        _uiState.value = AuthUiState.Error(message)
    }

    sealed class AuthUiState {
        object Idle : AuthUiState()
        object Loading : AuthUiState()
        data class Success(val needsTokenSetup: Boolean) : AuthUiState()
        data class Error(val message: String) : AuthUiState()
    }
}