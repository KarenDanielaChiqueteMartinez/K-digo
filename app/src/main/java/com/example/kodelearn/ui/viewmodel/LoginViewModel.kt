package com.example.kodelearn.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kodelearn.data.repository.KodeLearnRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isLoggedIn: Boolean = false
)

class LoginViewModel(
    private val repository: KodeLearnRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = ""
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = ""
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun loginUser() {
        val currentState = _uiState.value
        
        // Simple validation
        if (currentState.email.trim().isEmpty() || currentState.password.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email y contraseña son requeridos"
            )
            return
        }

        // Direct login - no coroutines, no delays, no database
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isLoggedIn = true,
            errorMessage = ""
        )
    }

    private fun isValidInput(state: LoginUiState): Boolean {
        val email = state.email.trim()
        val password = state.password

        when {
            email.isEmpty() -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "El correo electrónico es requerido"
                )
                return false
            }
            !isValidEmail(email) -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Ingresa un correo electrónico válido"
                )
                return false
            }
            password.isEmpty() -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "La contraseña es requerida"
                )
                return false
            }
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = ""
        )
    }

    fun clearLoginState() {
        _uiState.value = _uiState.value.copy(
            isLoggedIn = false
        )
    }

    companion object {
        fun factory(repository: KodeLearnRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LoginViewModel(repository) as T
                }
            }
        }
    }
}
