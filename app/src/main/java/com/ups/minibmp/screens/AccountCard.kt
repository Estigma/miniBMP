package com.ups.minibmp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ups.minibmp.models.Account  // Asegúrate de que esta ruta sea correcta
@Composable
fun AccountCard(account: Account) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "****${account.numeroCuenta.takeLast(4)}",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tipo: ${account.tipoCuenta}")
            Text("Saldo: $${account.saldo.format(2)}")
            Text(
                "Estado: ${account.estado}",
                color = when (account.estado) {
                    "Activa" -> Color.Green
                    "Bloqueada" -> Color.Red
                    else -> Color.Gray
                }
            )
        }
    }
}

fun Double.format(decimals: Int): String {
    return "%.${decimals}f".format(this)
}