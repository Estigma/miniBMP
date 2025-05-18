package com.ups.minibmp.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyChainAuthManager @Inject constructor(context: Context) {
    private val secureStorage = EncryptedSharedPreferences.create(
        context,
        "auth_credentials",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private var tempCredentials: Pair<String, String>? = null

    fun setTempCredentials(email: String, password: String) {
        tempCredentials = Pair(email, password)
    }

    fun saveCredentialsWithToken(token: String): Boolean {
        val credentials = tempCredentials ?: return false
        val editor = secureStorage.edit()
        editor.putString("email", credentials.first)
            .putString("password", credentials.second)
            .putString("token", token)
            .putBoolean("has_token", true)
            .apply()
        tempCredentials = null
        return true
    }

    fun getCredentialsByToken(inputToken: String): Pair<String, String>? {
        val storedToken = secureStorage.getString("token", null)
        return if (storedToken == inputToken) {
            Pair(
                secureStorage.getString("email", "") ?: return null,
                secureStorage.getString("password", "") ?: return null
            )
        } else {
            null
        }
    }

    fun hasToken(): Boolean {
        return secureStorage.getBoolean("has_token", false)
    }
}