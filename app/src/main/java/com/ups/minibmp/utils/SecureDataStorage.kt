package com.ups.minibmp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.ups.minibmp.models.SecureData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.secureDataStore: DataStore<SecureData> by dataStore(
    fileName = "secure_data.json",
    serializer = SecureDataSerializer
)

class SecureDataStorage(context: Context) {
    private val dataStore = context.secureDataStore

    suspend fun saveCredentials(email: String, password: String, token: String) {
        dataStore.updateData { current ->
            current.copy(
                email = email,
                password = password,
                token = token
            )
        }
    }

    suspend fun getCredentials(): Triple<String?, String?, String?> {
        val data = dataStore.data.first()
        return Triple(
            data.email.takeIf { it.isNotEmpty() },
            data.password.takeIf { it.isNotEmpty() },
            data.token.takeIf { it.isNotEmpty() }
        )
    }

    suspend fun clearCredentials() {
        dataStore.updateData { SecureData() }
    }

    val hasToken: Flow<Boolean> = dataStore.data.map { it.token.isNotEmpty() }
}