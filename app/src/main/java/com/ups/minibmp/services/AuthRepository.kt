package com.ups.minibmp.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ups.minibmp.utils.KeyChainAuthManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val keyChainManager: KeyChainAuthManager
) {
    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            keyChainManager.setTempCredentials(email, password)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginWithToken(token: String): Boolean {
        val credentials = keyChainManager.getCredentialsByToken(token) ?: return false
        return try {
            auth.signInWithEmailAndPassword(credentials.first, credentials.second).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun hasToken(): Boolean {
        return keyChainManager.hasToken()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun logout() {
        auth.signOut()
    }
}