package com.example.kodelearn.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kodelearn.data.learning.LessonService
import com.example.kodelearn.data.learning.LearningSessionService
import com.example.kodelearn.data.learning.ModuleProgressInfo
import com.example.kodelearn.data.learning.ModuleUnlockService
import com.example.kodelearn.data.learning.UserStats
import com.example.kodelearn.data.content.Module
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
    private val moduleUnlockService = ModuleUnlockService(repository)
    
    private val _userStats = MutableStateFlow<UserStats?>(null)
    val userStats: StateFlow<UserStats?> = _userStats.asStateFlow()
    
    private val _moduleProgress = MutableStateFlow<ModuleProgressInfo?>(null)
    val moduleProgress: StateFlow<ModuleProgressInfo?> = _moduleProgress.asStateFlow()
    
    private val _allModules = MutableStateFlow<List<Module>>(emptyList())
    val allModules: StateFlow<List<Module>> = _allModules.asStateFlow()
    
    private val _newlyUnlockedModule = MutableStateFlow<Module?>(null)
    val newlyUnlockedModule: StateFlow<Module?> = _newlyUnlockedModule.asStateFlow()
    
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
                
                // Crear usuario de prueba si no existe
                val currentUser = repository.getCurrentUser().first()
                if (currentUser == null) {
                    repository.createTestUser("Usuario Demo", 1)
                }
                
                // Simular finalización del módulo 1 para testing
                lessonService.simulateModuleCompletion(1, 1)
                
                // Cargar estadísticas del usuario
                val stats = lessonService.getUserStats(1) // Usar userId = 1 por defecto
                _userStats.value = stats
                
                // Cargar progreso del módulo principal
                val progress = lessonService.getModuleProgress(1, 1) // Módulo 1
                _moduleProgress.value = progress
                
                // Cargar todos los módulos con estado de desbloqueo
                val modules = moduleUnlockService.getAllModulesWithUnlockStatus(1)
                _allModules.value = modules
                
                // Verificar si hay módulos recién desbloqueados
                checkForNewlyUnlockedModules()
                
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
     * Verifica si hay módulos recién desbloqueados
     */
    private fun checkForNewlyUnlockedModules() {
        viewModelScope.launch {
            try {
                val modules = moduleUnlockService.getAllModulesWithUnlockStatus(1)
                val newlyUnlocked = modules.filter { it.isUnlocked }
                
                // Si hay módulos desbloqueados, mostrar el primero como recién desbloqueado
                if (newlyUnlocked.isNotEmpty()) {
                    _newlyUnlockedModule.value = newlyUnlocked.first()
                }
            } catch (e: Exception) {
                _error.value = "Error al verificar módulos desbloqueados: ${e.message}"
            }
        }
    }
    
    /**
     * Cierra la animación de desbloqueo
     */
    fun dismissUnlockAnimation() {
        _newlyUnlockedModule.value = null
    }
    
    /**
     * Verifica si un módulo está desbloqueado
     */
    fun isModuleUnlocked(moduleId: Int): Boolean {
        return _allModules.value.find { it.id == moduleId }?.isUnlocked ?: false
    }
    
    /**
     * Obtiene el progreso de desbloqueo de un módulo
     */
    fun getUnlockProgress(moduleId: Int): String {
        val module = _allModules.value.find { it.id == moduleId }
        return if (module?.isUnlocked == true) {
            "Módulo desbloqueado"
        } else {
            "Completa el módulo anterior para desbloquear"
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
