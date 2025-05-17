package com.ups.minibmp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ups.minibmp.viewModels.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (needsTokenSetup: Boolean) -> Unit,
    onTokenLoginClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthViewModel.AuthUiState.Success -> {
                val needsSetup = (uiState as AuthViewModel.AuthUiState.Success).needsTokenSetup
                onLoginSuccess(needsSetup)
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido a MiniBMP",
            style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(64.dp))
        Text("Ingresa tus credenciales")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.loginWithCredentials(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            if (uiState is AuthViewModel.AuthUiState.Loading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Ingresar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onTokenLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar con token")
        }

        if (uiState is AuthViewModel.AuthUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (uiState as AuthViewModel.AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}