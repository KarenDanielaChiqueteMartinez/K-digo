package com.example.kodelearn.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kodelearn.data.learning.LessonService
import com.example.kodelearn.data.learning.LearningSessionService
import com.example.kodelearn.data.learning.ModuleProgressInfo
import com.example.kodelearn.data.learning.UserStats
import com.example.kodelearn.data.repository.KodeLearnRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel para el HomeScreen que maneja datos dinámicos
 */
class HomeViewModel(
    private val repository: KodeLearnRepository
) : ViewModel() {
    
    private val lessonService = LessonService(repository, LearningSessionService(repository))
    
    private val _userStats = MutableStateFlow<UserStats?>(null)
    val userStats: StateFlow<UserStats?> = _userStats.asStateFlow()
    
    private val _moduleProgress = MutableStateFlow<ModuleProgressInfo?>(null)
    val moduleProgress: StateFlow<ModuleProgressInfo?> = _moduleProgress.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadUserData()
    }
    
    /**
     * Carga los datos del usuario y progreso
     */
    fun loadUserData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Cargar estadísticas del usuario
                val stats = lessonService.getUserStats(1) // Usar userId = 1 por defecto
                _userStats.value = stats
                
                // Cargar progreso del módulo principal
                val progress = lessonService.getModuleProgress(1, 1) // Módulo 1
                _moduleProgress.value = progress
                
            } catch (e: Exception) {
                _error.value = "Error al cargar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Refresca los datos del usuario
     */
    fun refreshData() {
        loadUserData()
    }
    
    /**
     * Inicia una lección
     */
    fun startLesson(moduleId: Int, lessonId: Int) {
        viewModelScope.launch {
            try {
                lessonService.startLesson(1, moduleId, lessonId)
                loadUserData() // Refrescar datos después de iniciar
            } catch (e: Exception) {
                _error.value = "Error al iniciar lección: ${e.message}"
            }
        }
    }
    
    /**
     * Formatea el tiempo en formato legible
     */
    fun formatDuration(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes % 60}m"
            minutes > 0 -> "${minutes}m"
            else -> "${seconds}s"
        }
    }
    
    /**
     * Calcula el nivel del usuario basado en XP
     */
    fun calculateUserLevel(xp: Int): Int {
        return (xp / 100) + 1 // 100 XP por nivel
    }
    
    /**
     * Calcula el progreso hacia el siguiente nivel
     */
    fun calculateLevelProgress(xp: Int): Float {
        val currentLevelXP = xp % 100
        return currentLevelXP / 100f
    }
    
    /**
     * Obtiene el nombre de la liga basado en XP
     */
    fun getLeagueName(xp: Int): String {
        return when {
            xp >= 1000 -> "Diamante"
            xp >= 800 -> "Oro"
            xp >= 600 -> "Plata"
            xp >= 400 -> "Bronce"
            xp >= 200 -> "Hierro"
            else -> "Principiante"
        }
    }
    
    /**
     * Obtiene el color de la liga
     */
    fun getLeagueColor(xp: Int): String {
        return when {
            xp >= 1000 -> "#B8860B" // Dorado
            xp >= 800 -> "#FFD700" // Oro
            xp >= 600 -> "#C0C0C0" // Plata
            xp >= 400 -> "#CD7F32" // Bronce
            xp >= 200 -> "#A19D94" // Hierro
            else -> "#808080" // Gris
        }
    }
    
    /**
     * Limpia el error actual
     */
    fun clearError() {
        _error.value = null
    }
    
    companion object {
        fun factory(repository: KodeLearnRepository) = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        }
    }
}
