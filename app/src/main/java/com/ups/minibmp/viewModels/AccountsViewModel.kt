package com.ups.minibmp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ups.minibmp.models.Account
import com.ups.minibmp.services.AccountRepository
import com.ups.minibmp.services.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    // Estado privado mutable
    private val _uiState = MutableStateFlow(AccountsUiState())

    // Estado público inmutable
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    init {
        loadAccounts()
    }

    fun loadAccounts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val accounts = accountRepository.getAccountsByUser(getCurrentUserId())
                _uiState.update {
                    it.copy(
                        accounts = accounts,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Error al cargar cuentas",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refreshAccounts() {
        loadAccounts()
    }

    private fun getCurrentUserId(): String {
        return authRepository.getCurrentUser()?.email
            ?: throw IllegalStateException("Usuario no autenticado")
    }

    // Data class para el estado de la UI
    data class AccountsUiState(
        val accounts: List<Account> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )
}