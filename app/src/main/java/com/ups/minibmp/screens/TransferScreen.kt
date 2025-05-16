package com.ups.minibmp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ups.minibmp.utils.Resource
import com.ups.minibmp.viewModels.TransferViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    viewModel: TransferViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val transferState by viewModel.transferState.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val beneficiary by viewModel.beneficiary.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var fromAccountExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Transferencia") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selector cuenta origen
            ExposedDropdownMenuBox(
                expanded = fromAccountExpanded,
                onExpandedChange = { fromAccountExpanded = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    value = accounts.find { it.numeroCuenta == formState.fromAccountNumber }?.getMaskedAccountNumber()
                        ?: "Seleccione cuenta origen",
                    onValueChange = {},
                    label = { Text("Cuenta Origen") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(fromAccountExpanded) }
                )

                ExposedDropdownMenu(
                    expanded = fromAccountExpanded,
                    onDismissRequest = { fromAccountExpanded = false }
                ) {
                    accounts.forEach { account ->
                        DropdownMenuItem(
                            text = { Text("${account.tipoCuenta}: ${account.getMaskedAccountNumber()}") },
                            onClick = {
                                viewModel.updateFromAccount(account.numeroCuenta)
                                fromAccountExpanded = false
                            }
                        )
                    }
                }
            }

            // Campo cuenta destino
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = formState.toAccountNumber,
                onValueChange = viewModel::updateToAccount,
                label = { Text("Número de cuenta destino") },
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            )

            // Info beneficiario
            beneficiary?.let {
                Text(
                    text = "Beneficiario: ${it.nombre} ${it.apellido}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Campo monto
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = formState.amount,
                onValueChange = viewModel::updateAmount,
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            // Campo descripción
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = formState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Descripción") },
                maxLines = 3
            )

            // Mensajes de error
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Botón transferir
            Button(
                onClick = viewModel::executeTransfer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = formState.fromAccountNumber.isNotEmpty() &&
                        formState.toAccountNumber.isNotEmpty() &&
                        formState.amount.isNotEmpty() &&
                        beneficiary != null
            ) {
                if (transferState is Resource.Loading) {
                    Text("Transferir")//CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Transferir")
                }
            }

            // Resultado transferencia
            when (transferState) {
                is Resource.Success -> {
                    AlertDialog(
                        onDismissRequest = { /* No permitir cerrar haciendo clic fuera */ },
                        title = { Text("Transferencia exitosa") },
                        text = { Text("La transferencia se realizó correctamente") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    // 1. Resetear el estado de transferencia
                                    viewModel.resetTransferState()
                                    // 2. Resetear el formulario
                                    viewModel.resetForm()
                                    // 3. Opcional: Navegar a pantalla inicial si es necesario
                                    //navController.navigate("inicio") { popUpTo(0) }
                                }
                            ) {
                                Text("Aceptar")
                            }
                        }
                    )
                }
                is Resource.Error -> {
                    Text(
                        text = (transferState as Resource.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }
        }
    }
}