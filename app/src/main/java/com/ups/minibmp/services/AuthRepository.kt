package com.ups.minibmp.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ups.minibmp.utils.SecureDataStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val secureStorage: SecureDataStorage
) {
    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            secureStorage.saveCredentials(email, password, "") // Token vacío inicial
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginWithToken(token: String): Boolean {
        val (email, password, savedToken) = secureStorage.getCredentials()
        return if (!email.isNullOrEmpty() && !password.isNullOrEmpty() && token == savedToken) {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } else {
            false
        }
    }

    suspend fun setupToken(token: String): Boolean {
        val (email, password, _) = secureStorage.getCredentials()
        return if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            secureStorage.saveCredentials(email, password, token)
            true
        } else {
            false
        }
    }

    suspend fun hasToken(): Boolean {
        val (_, _, token) = secureStorage.getCredentials()
        return !token.isNullOrEmpty()
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    fun logout() = auth.signOut()

}