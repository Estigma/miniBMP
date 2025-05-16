package com.ups.minibmp.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.ups.minibmp.models.Account
import com.ups.minibmp.models.Transaction
import com.ups.minibmp.models.TransferResult
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransferRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun transferFunds(
        fromAccountNumber: String,
        toAccountNumber: String,
        amount: Double,
        description: String
    ): TransferResult {
        if (amount <= 0) throw IllegalArgumentException("El monto debe ser positivo")

        // 1. Obtener información de la cuenta destino (fuera de la transacción)
        val toAccountQuery = firestore.collection("Cuentas")
            .whereEqualTo("numeroCuenta", toAccountNumber)
            .limit(1)
            .get()
            .await()

        val toAccountDoc = toAccountQuery.documents.firstOrNull()
            ?: throw Exception("Cuenta destino no encontrada")
        val toAccountId = toAccountDoc.id
        val toAccount = toAccountDoc.toObject<Account>()
            ?: throw Exception("Cuenta destino inválida")

        // 2. Ejecutar transacción atómica para actualizar saldos
        return firestore.runTransaction { transaction ->
            // 2.1 Obtener cuenta origen
            val fromAccountRef = firestore.collection("Cuentas").document(fromAccountNumber)
            val fromAccount = transaction.get(fromAccountRef).toObject<Account>()
                ?: throw Exception("Cuenta origen no encontrada")

            // 2.2 Validar saldo
            if (fromAccount.saldo < amount) throw Exception("Saldo insuficiente")

            // 2.3 Calcular nuevos saldos
            val newFromBalance = fromAccount.saldo - amount
            val newToBalance = toAccount.saldo + amount

            // 2.4 Actualizar saldos
            transaction.update(fromAccountRef, "saldo", newFromBalance)
            transaction.update(toAccountDoc.reference, "saldo", newToBalance)


            // 2.5 Registrar transacciones DENTRO de la transacción
            val now = Timestamp.now()
            val batch = firestore.batch()

            val debitRef = firestore.collection("Transacciones").document()
            val debitTransaction = hashMapOf(
                "cuenta" to fromAccountNumber,
                "monto" to amount,
                "tipo" to "débito",
                "saldo" to newFromBalance,
                "fecha" to now,
                "descripcion" to description,
                "cuentaRelacionada" to toAccountId
            )
            batch.set(debitRef, debitTransaction)

            val creditRef = firestore.collection("Transacciones").document()
            val creditTransaction = hashMapOf(
                "cuenta" to toAccountId,
                "monto" to amount,
                "tipo" to "crédito",
                "saldo" to newToBalance,
                "fecha" to now,
                "descripcion" to description,
                "cuentaRelacionada" to fromAccountNumber
            )
            batch.set(creditRef, creditTransaction)

            batch.commit()

            TransferResult(
                success = true,
                newFromBalance = newFromBalance,
                newToBalance = newToBalance,
                message = "Transferencia exitosa"
            )
        }.await()
    }
}