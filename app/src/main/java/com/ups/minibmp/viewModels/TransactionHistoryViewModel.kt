package com.ups.minibmp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ups.minibmp.models.Account
import com.ups.minibmp.models.Transaction
import com.ups.minibmp.services.AccountRepository
import com.ups.minibmp.services.TransactionRepository
import com.ups.minibmp.services.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _selectedAccount = MutableStateFlow<String?>(null)
    val selectedAccount: StateFlow<String?> = _selectedAccount.asStateFlow()

    private val _startDate = MutableStateFlow<Date>(Date().apply {
        time -= 30L * 24 * 60 * 60 * 1000 // Últimos 30 días por defecto
    })
    val startDate: StateFlow<Date> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<Date>(Date())
    val endDate: StateFlow<Date> = _endDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUserAccounts()
        loadTransactions()
    }

    private fun loadUserAccounts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = authRepository.getCurrentUser()?.email
                    ?: throw Exception("Usuario no autenticado")
                _accounts.value = accountRepository.getAccountsByUser(userId)
                _selectedAccount.value = _accounts.value.firstOrNull()?.id
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val accountId = _selectedAccount.value ?: return@launch
                _transactions.value = transactionRepository.getTransactions(
                    accountNumber = accountId,
                    startDate = _startDate.value,
                    endDate = _endDate.value,
                    limit = 10
                ).sortedByDescending { it.fecha }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSelectedAccount(accountId: String) {
        _selectedAccount.value = accountId
        loadTransactions()
    }

    fun updateStartDate(date: Date) {
        _startDate.value = date
        loadTransactions()
    }

    fun updateEndDate(date: Date) {
        _endDate.value = date
        loadTransactions()
    }
}