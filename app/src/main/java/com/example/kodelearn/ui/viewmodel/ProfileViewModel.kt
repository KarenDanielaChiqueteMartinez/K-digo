package com.example.kodelearn.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kodelearn.data.database.entities.User
import com.example.kodelearn.data.repository.KodeLearnRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val friendsCount: Int = 0
)

class ProfileViewModel(
    private val repository: KodeLearnRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // Crear usuario de prueba si no existe
            val currentUser = repository.getCurrentUser().first()
            if (currentUser == null) {
                repository.createTestUser("Usuario Demo", 1)
            }
            
            repository.getCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(
                    user = user,
                    isLoading = false
                )
            }
        }
    }

    fun updateBiography(biography: String) {
        viewModelScope.launch {
            _uiState.value.user?.let { user ->
                val updatedUser = user.copy(biography = biography)
                repository.updateUser(updatedUser)
            }
        }
    }

    fun shareProgress() {
        // Implementation for sharing progress
        // Could integrate with Android's share intent
    }

    companion object {
        fun factory(repository: KodeLearnRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProfileViewModel(repository) as T
                }
            }
        }
    }
}
