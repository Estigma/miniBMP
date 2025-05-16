package com.ups.minibmp.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ups.minibmp.models.Account
import com.ups.minibmp.models.User
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class AccountRepository @Inject constructor() {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun getAccountsByUser(email: String): List<Account> {
        return try {
            val snapshot = db.collection("Cuentas")
                .whereEqualTo("correo", email)
                .get()
                .await()

            snapshot.documents.map { doc ->
                Account(
                    id = doc.id,
                    correo = doc.getString("correo") ?: "",
                    numeroCuenta = doc.getString("numeroCuenta") ?: "",
                    tipoCuenta = doc.getString("tipoCuenta") ?: "",
                    saldo = doc.getDouble("saldo") ?: 0.0,
                    fechaCreacion = doc.getDate("fechaCreacion") ?: Date(),
                    estado = doc.getString("estado") ?: "Activa"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun validateAccount(accountNumber: String): User? {
        return try {
            val snapshot = db.collection("Cuentas")
                .whereEqualTo("numeroCuenta", accountNumber)
                .get()
                .await()

            if (snapshot.documents.isNotEmpty()) {
                val account = snapshot.documents[0]
                val userEmail = account.getString("correo") ?: return null

                val userSnapshot = db.collection("Usuarios")
                    .whereEqualTo("correo", userEmail)
                    .get()
                    .await()

                if (userSnapshot.documents.isNotEmpty()) {
                    val userDoc = userSnapshot.documents[0]
                    User(
                        nombre = userDoc.getString("nombre") ?: "",
                        apellido = userDoc.getString("apellido") ?: "",
                        correo = userEmail
                    )
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}