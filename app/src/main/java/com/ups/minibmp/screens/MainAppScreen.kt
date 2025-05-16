package com.ups.minibmp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    currentScreen: String,
    onAccountsClick: () -> Unit,
    onTransactionsClick: () -> Unit,
    onTransferClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.AccountBalance, contentDescription = "Cuentas") },
                    label = { Text("Cuentas") },
                    selected = currentScreen == "accounts",
                    onClick = onAccountsClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.History, contentDescription = "Movimientos") },
                    label = { Text("Movimientos") },
                    selected = currentScreen == "transactions",
                    onClick = onTransactionsClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Send, contentDescription = "Transferir") },
                    label = { Text("Transferir") },
                    selected = currentScreen == "transfer",
                    onClick = onTransferClick
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}