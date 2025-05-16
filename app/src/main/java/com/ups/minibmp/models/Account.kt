package com.ups.minibmp.models

import java.util.Date

data class Account(
    val id: String = "",
    val correo: String = "",
    val numeroCuenta: String = "",
    val tipoCuenta: String = "",
    val saldo: Double = 0.0,
    val fechaCreacion: Date = Date(),
    val estado: String = "Activa"
){
    fun getMaskedAccountNumber(): String {
        return "****-****-${numeroCuenta.takeLast(4)}"
    }
}