package com.ups.minibmp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ups.minibmp.models.Account
import com.ups.minibmp.models.User
import com.ups.minibmp.services.AccountRepository
import com.ups.minibmp.services.AuthRepository
import com.ups.minibmp.services.TransferRepository
import com.ups.minibmp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transferRepository: TransferRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _transferState = MutableStateFlow<Resource<Unit>>(Resource.loading())
    val transferState: StateFlow<Resource<Unit>> = _transferState.asStateFlow()

    private val _formState = MutableStateFlow(TransferFormState())
    val formState = _formState.asStateFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts = _accounts.asStateFlow()

    private val _beneficiary = MutableStateFlow<User?>(null)
    val beneficiary = _beneficiary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        loadUserAccounts()
    }

    private fun loadUserAccounts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _accounts.value = accountRepository.getAccountsByUser(getCurrentUserId())
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar cuentas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getCurrentUserId(): String {
        return authRepository.getCurrentUser()?.email
            ?: throw IllegalStateException("Usuario no autenticado")
    }

    fun updateFromAccount(fromAccountNumber: String) {
        _formState.update { it.copy(fromAccountNumber = fromAccountNumber) }
    }

    fun updateToAccount(accountNumber: String) {
        _formState.update { it.copy(toAccountNumber = accountNumber) }
        validateDestinationAccount(accountNumber)
    }

    private fun validateDestinationAccount(accountNumber: String) {
        if (accountNumber.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _beneficiary.value = accountRepository.validateAccount(accountNumber)
                if (_beneficiary.value == null) {
                    _errorMessage.value = "Cuenta destino no encontrada"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al validar cuenta: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAmount(amount: String) {
        // Validar que sea un número válido
        if (amount.isEmpty() || amount.matches(Regex("^\\d+(\\.\\d{0,2})?$"))) {
            _formState.update { it.copy(amount = amount) }
        }
    }

    fun updateDescription(description: String) {
        _formState.update { it.copy(description = description) }
    }

    fun executeTransfer() {
        val currentForm = _formState.value
        val amount = currentForm.amount.toDoubleOrNull() ?: run {
            _errorMessage.value = "Monto inválido"
            return
        }

        if (_beneficiary.value == null) {
            _errorMessage.value = "Debe validar la cuenta destino primero"
            return
        }

        viewModelScope.launch {
            _transferState.value = Resource.loading("Procesando transferencia...")
            try {
                transferRepository.transferFunds(
                    fromAccountNumber = currentForm.fromAccountNumber,
                    toAccountNumber = currentForm.toAccountNumber,
                    amount = amount,
                    description = currentForm.description
                )
                _transferState.value = Resource.success(Unit)
                resetForm()
            } catch (e: Exception) {
                _transferState.value = Resource.error(
                    message = "Error en la transferencia: ${e.message ?: "Error desconocido"}",
                    throwable = e
                )
            }
        }
    }

    fun resetTransferState() {
        _transferState.value = Resource.loading()
    }

    fun resetForm() {
        _formState.value = TransferFormState()
        _beneficiary.value = null
        _errorMessage.value = null
    }

    data class TransferFormState(
        val fromAccountNumber: String = "",
        val toAccountNumber: String = "",  // Ahora puede ser número de cuenta o ID
        val amount: String = "",
        val description: String = ""
    )
}