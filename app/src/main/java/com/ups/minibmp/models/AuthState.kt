package com.ups.minibmp.models

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val email: String?, val uid: String?) : AuthState()
    data class Error(val message: String) : AuthState()
}