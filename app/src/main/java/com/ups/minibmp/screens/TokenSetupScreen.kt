package com.ups.minibmp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ups.minibmp.viewModels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenSetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val tokenState = remember { mutableStateOf("") }
    val confirmTokenState = remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Token") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crea tu token de seguridad de 6 dígitos", style = MaterialTheme.typography.titleSmall)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = tokenState.value,
                onValueChange = { if (it.length <= 6) tokenState.value = it },
                label = { Text("Nuevo token") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmTokenState.value,
                onValueChange = { if (it.length <= 6) confirmTokenState.value = it },
                label = { Text("Confirmar token") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (tokenState.value != confirmTokenState.value) {
                        viewModel.setError("Los tokens no coinciden")
                        return@Button
                    }
                    if (tokenState.value.length != 6) {
                        viewModel.setError("El token debe tener 6 dígitos")
                        return@Button
                    }
                    viewModel.setupToken(tokenState.value)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = tokenState.value.length == 6 && confirmTokenState.value.length == 6
            ) {
                if (uiState is AuthViewModel.AuthUiState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Guardar Token")
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.noSetupToken()
                }
            ) {
                if (uiState is AuthViewModel.AuthUiState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Continuar sin Token")
                }
            }

            when (uiState) {
                is AuthViewModel.AuthUiState.Success -> {
                    onSetupComplete()
                }
                is AuthViewModel.AuthUiState.Error -> {
                    Text(
                        text = (uiState as AuthViewModel.AuthUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }
        }
    }
}