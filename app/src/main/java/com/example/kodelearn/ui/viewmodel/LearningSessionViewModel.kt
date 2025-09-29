package com.example.kodelearn.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kodelearn.data.learning.LearningSession
import com.example.kodelearn.data.learning.LearningSessionService
import com.example.kodelearn.data.learning.LearningStats
import com.example.kodelearn.data.repository.KodeLearnRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar las sesiones de aprendizaje dinámicas
 */
class LearningSessionViewModel(
    private val repository: KodeLearnRepository
) : ViewModel() {
    
    private val learningService = LearningSessionService(repository)
    
    val currentSession: StateFlow<LearningSession?> = learningService.currentSession
    val sessionHistory: StateFlow<List<LearningSession>> = learningService.sessionHistory
    
    private val _isLearningActive = MutableStateFlow(false)
    val isLearningActive: StateFlow<Boolean> = _isLearningActive.asStateFlow()
    
    private val _currentStats = MutableStateFlow<LearningStats?>(null)
    val currentStats: StateFlow<LearningStats?> = _currentStats.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    /**
     * Inicia una nueva sesión de aprendizaje
     */
    fun startLearningSession(userId: Int, moduleId: Int, lessonId: Int = 1) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                learningService.startLearningSession(userId, moduleId, lessonId)
                _isLearningActive.value = true
                loadUserStats(userId)
            } catch (e: Exception) {
                _error.value = "Error al iniciar sesión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Simula una sesión de aprendizaje completa
     */
    fun simulateLearningSession(userId: Int, moduleId: Int, numLessons: Int = 3) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                learningService.simulateLearningSession(userId, moduleId, numLessons)
                _isLearningActive.value = false
                loadUserStats(userId)
            } catch (e: Exception) {
                _error.value = "Error en simulación: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Actualiza el progreso durante la sesión
     */
    fun updateProgress(userId: Int, moduleId: Int, lessonId: Int, isCorrect: Boolean, timeSpent: Long) {
        viewModelScope.launch {
            try {
                learningService.updateProgress(userId, moduleId, lessonId, isCorrect, timeSpent)
            } catch (e: Exception) {
                _error.value = "Error al actualizar progreso: ${e.message}"
            }
        }
    }
    
    /**
     * Completa una lección
     */
    fun completeLesson(userId: Int, moduleId: Int, lessonId: Int) {
        viewModelScope.launch {
            try {
                learningService.completeLesson(userId, moduleId, lessonId)
            } catch (e: Exception) {
                _error.value = "Error al completar lección: ${e.message}"
            }
        }
    }
    
    /**
     * Termina la sesión actual
     */
    fun endCurrentSession(userId: Int) {
        viewModelScope.launch {
            try {
                learningService.endCurrentSession()
                _isLearningActive.value = false
                loadUserStats(userId)
            } catch (e: Exception) {
                _error.value = "Error al terminar sesión: ${e.message}"
            }
        }
    }
    
    /**
     * Carga las estadísticas del usuario
     */
    fun loadUserStats(userId: Int) {
        viewModelScope.launch {
            try {
                val stats = learningService.getUserLearningStats(userId)
                _currentStats.value = stats
            } catch (e: Exception) {
                _error.value = "Error al cargar estadísticas: ${e.message}"
            }
        }
    }
    
    /**
     * Simula múltiples sesiones para generar datos de prueba
     */
    fun generateTestData(userId: Int, numSessions: Int = 5) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repeat(numSessions) { sessionIndex ->
                    val moduleId = (sessionIndex % 3) + 1 // Rotar entre módulos 1, 2, 3
                    val numLessons = (2..5).random() // 2-5 lecciones por sesión
                    
                    learningService.simulateLearningSession(userId, moduleId, numLessons)
                    
                    // Pausa entre sesiones
                    kotlinx.coroutines.delay(1000)
                }
                loadUserStats(userId)
            } catch (e: Exception) {
                _error.value = "Error al generar datos de prueba: ${e.message}"
            } finally {
                _isLoading.value = false
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
            minutes > 0 -> "${minutes}m ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }
    
    /**
     * Formatea la fecha en formato legible
     */
    fun formatDate(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return formatter.format(date)
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
     * Limpia el error actual
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Obtiene el resumen de la sesión actual
     */
    fun getCurrentSessionSummary(): String {
        val session = currentSession.value ?: return "No hay sesión activa"
        
        return buildString {
            appendLine("Sesión activa:")
            appendLine("• Módulo: ${session.moduleId}")
            appendLine("• Lección: ${session.lessonId}")
            appendLine("• XP ganado: ${session.xpEarned}")
            appendLine("• Lecciones completadas: ${session.lessonsCompleted}")
            appendLine("• Precisión: ${(session.accuracy * 100).toInt()}%")
        }
    }
    
    /**
     * Obtiene el resumen de estadísticas del usuario
     */
    fun getUserStatsSummary(): String {
        val stats = currentStats.value ?: return "No hay estadísticas disponibles"
        
        return buildString {
            appendLine("Estadísticas de aprendizaje:")
            appendLine("• Total de sesiones: ${stats.totalSessions}")
            appendLine("• Tiempo total: ${formatDuration(stats.totalTimeSpent)}")
            appendLine("• XP total: ${stats.totalXPEarned}")
            appendLine("• Precisión promedio: ${(stats.averageAccuracy * 100).toInt()}%")
            appendLine("• Lecciones completadas: ${stats.lessonsCompleted}")
            appendLine("• Racha actual: ${stats.streakDays} días")
        }
    }
}
