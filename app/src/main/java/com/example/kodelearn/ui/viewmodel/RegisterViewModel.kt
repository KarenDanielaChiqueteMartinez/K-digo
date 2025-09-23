package com.example.kodelearn.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kodelearn.data.database.entities.User
import com.example.kodelearn.data.repository.KodeLearnRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)

class RegisterViewModel(
    private val repository: KodeLearnRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            errorMessage = "",
            successMessage = ""
        )
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = "",
            successMessage = ""
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = "",
            successMessage = ""
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            errorMessage = "",
            successMessage = ""
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
    }

    fun registerUser() {
        val currentState = _uiState.value
        
        // Validation
        if (!isValidInput(currentState)) {
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")

        viewModelScope.launch {
            try {
                // Create new user
                val newUser = User(
                    id = 1, // For now, we'll use ID 1
                    name = currentState.name.trim(),
                    biography = "",
                    dailyStreak = 0,
                    totalXP = 0,
                    league = "Wooden",
                    avatarUrl = "",
                    hearts = 5,
                    coins = 0
                )

                // Insert user into database
                repository.insertUser(newUser)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "¡Cuenta creada exitosamente! Bienvenido a KodeLearn.",
                    errorMessage = ""
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al crear la cuenta: ${e.message}",
                    successMessage = ""
                )
            }
        }
    }

    private fun isValidInput(state: RegisterUiState): Boolean {
        val name = state.name.trim()
        val email = state.email.trim()
        val password = state.password
        val confirmPassword = state.confirmPassword

        when {
            name.isEmpty() -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "El nombre es requerido"
                )
                return false
            }
            name.length < 2 -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "El nombre debe tener al menos 2 caracteres"
                )
                return false
            }
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
            password.length < 6 -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "La contraseña debe tener al menos 6 caracteres"
                )
                return false
            }
            confirmPassword.isEmpty() -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Confirma tu contraseña"
                )
                return false
            }
            password != confirmPassword -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Las contraseñas no coinciden"
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
            errorMessage = "",
            successMessage = ""
        )
    }

    companion object {
        fun factory(repository: KodeLearnRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RegisterViewModel(repository) as T
                }
            }
        }
    }
}
