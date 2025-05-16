package com.ups.minibmp.models

import com.google.firebase.Timestamp
import java.util.Date

data class Transaction(
    val id: String = "", // Se autogenerará al crear el documento
    val cuenta: String = "",
    val monto: Double = 0.0,
    val tipo: String = "", // "débito" o "crédito"
    val saldo: Double = 0.0,
    val fecha: Date = Date(),
    val descripcion: String = "",
    val cuentaRelacionada: String = ""
) {
    // Conversión desde Firestore
    companion object {
        fun fromFirestore(
            id: String,
            data: Map<String, Any>
        ): Transaction {
            val timestamp = data["fecha"] as? Timestamp
            return Transaction(
                id = id,
                cuenta = data["cuenta"] as? String ?: "",
                monto = data["monto"] as? Double ?: 0.0,
                tipo = data["tipo"] as? String ?: "",
                saldo = data["saldo"] as? Double ?: 0.0,
                fecha = timestamp?.toDate() ?: Date(),
                descripcion = data["descripcion"] as? String ?: "",
                cuentaRelacionada = data["cuentaRelacionada"] as? String ?: ""
            )
        }
    }
}