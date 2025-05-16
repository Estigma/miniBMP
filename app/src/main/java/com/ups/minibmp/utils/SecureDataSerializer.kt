package com.ups.minibmp.utils

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.ups.minibmp.models.SecureData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object SecureDataSerializer : Serializer<SecureData> {
    override val defaultValue: SecureData = SecureData()
    private val json = Json { encodeDefaults = true }

    override suspend fun readFrom(input: InputStream): SecureData {
        return try {
            json.decodeFromString(
                SecureData.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            throw CorruptionException("Error leyendo datos seguros", e)
        }
    }

    override suspend fun writeTo(t: SecureData, output: OutputStream) {
        output.write(
            json.encodeToString(SecureData.serializer(), t)
                .encodeToByteArray()
        )
    }
}