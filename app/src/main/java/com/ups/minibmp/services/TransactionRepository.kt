package com.ups.minibmp.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import com.ups.minibmp.models.Transaction

class TransactionRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun getTransactions(
        accountNumber: String,
        startDate: Date? = null,
        endDate: Date? = null,
        limit: Int = 10
    ): List<Transaction> {
        return try {
            var query = firestore.collection("Transacciones")
                .whereEqualTo("cuenta", accountNumber)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .limit(limit.toLong())

            if (startDate != null && endDate != null) {
                query = query.whereGreaterThanOrEqualTo("fecha", startDate)
                    .whereLessThanOrEqualTo("fecha", endDate)
            }

            val snapshot = query.get().await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            e.message
            emptyList()
        }
    }
}