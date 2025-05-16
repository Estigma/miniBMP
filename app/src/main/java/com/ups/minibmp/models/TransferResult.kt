package com.ups.minibmp.models

data class TransferResult(
    val success: Boolean,
    val newFromBalance: Double,
    val newToBalance: Double,
    val message: String? = null
)