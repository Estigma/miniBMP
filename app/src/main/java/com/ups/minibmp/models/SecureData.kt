package com.ups.minibmp.models

import kotlinx.serialization.Serializable

@Serializable
data class SecureData(
    val email: String = "",
    val password: String = "",
    val token: String = ""
)