package com.example.kodelearn.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kodelearn.data.ml.*
import com.example.kodelearn.data.repository.KodeLearnRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar recomendaciones y análisis de usuarios usando KNN
 */
class RecommendationViewModel(
    private val repository: KodeLearnRepository
) : ViewModel() {
    
    private val recommendationService = UserRecommendationService(repository)
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    private val _similarUsers = MutableStateFlow<List<UserRecommendation>>(emptyList())
    val similarUsers: StateFlow<List<UserRecommendation>> = _similarUsers.asStateFlow()
    
    private val _userClassification = MutableStateFlow("unknown")
    val userClassification: StateFlow<String> = _userClassification.asStateFlow()
    
    private val _performancePrediction = MutableStateFlow<PerformancePrediction?>(null)
    val performancePrediction: StateFlow<PerformancePrediction?> = _performancePrediction.asStateFlow()
    
    private val _similarityStats = MutableStateFlow<SimilarityStats?>(null)
    val similarityStats: StateFlow<SimilarityStats?> = _similarityStats.asStateFlow()
    
    private val _learningPatterns = MutableStateFlow<LearningPatternAnalysis?>(null)
    val learningPatterns: StateFlow<LearningPatternAnalysis?> = _learningPatterns.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        initializeService()
    }
    
    /**
     * Inicializa el servicio de recomendaciones
     */
    private fun initializeService() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                recommendationService.initialize()
                _isInitialized.value = true
                loadUserRecommendations()
            } catch (e: Exception) {
                _error.value = "Error al inicializar recomendaciones: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Carga recomendaciones de usuarios similares
     */
    fun loadUserRecommendations(limit: Int = 10) {
        if (!_isInitialized.value) return
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val recommendations = recommendationService.getSimilarUsers(1, limit) // Usar ID 1 como ejemplo
                _similarUsers.value = recommendations
            } catch (e: Exception) {
                _error.value = "Error al cargar usuarios similares: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clasifica al usuario actual
     */
    fun classifyCurrentUser() {
        if (!_isInitialized.value) return
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val classification = recommendationService.classifyUser(1) // Usar ID 1 como ejemplo
                _userClassification.value = classification
            } catch (e: Exception) {
                _error.value = "Error al clasificar usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Predice el rendimiento del usuario actual
     */
    fun predictUserPerformance() {
        if (!_isInitialized.value) return
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val prediction = recommendationService.predictUserPerformance(1) // Usar ID 1 como ejemplo
                _performancePrediction.value = prediction
            } catch (e: Exception) {
                _error.value = "Error al predecir rendimiento: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Obtiene estadísticas de similitud del usuario actual
     */
    fun loadSimilarityStats() {
        if (!_isInitialized.value) return
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val stats = recommendationService.getUserSimilarityStats(1) // Usar ID 1 como ejemplo
                _similarityStats.value = stats
            } catch (e: Exception) {
                _error.value = "Error al cargar estadísticas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Analiza patrones de aprendizaje del usuario actual
     */
    fun analyzeLearningPatterns() {
        if (!_isInitialized.value) return
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val analysis = recommendationService.analyzeLearningPatterns(1) // Usar ID 1 como ejemplo
                _learningPatterns.value = analysis
            } catch (e: Exception) {
                _error.value = "Error al analizar patrones: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Obtiene usuarios similares para un módulo específico
     */
    fun getUsersForModule(moduleId: Int, limit: Int = 5) {
        if (!_isInitialized.value) return
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val recommendations = recommendationService.getUsersSimilarForModule(moduleId, limit)
                _similarUsers.value = recommendations
            } catch (e: Exception) {
                _error.value = "Error al obtener usuarios para módulo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Valida el modelo de recomendaciones
     */
    fun validateModel() {
        if (!_isInitialized.value) return
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val validationResult = recommendationService.validateModel()
                // Aquí podrías mostrar el resultado de validación en la UI
                _error.value = "Modelo validado. Precisión: ${(validationResult.accuracy * 100).toInt()}%"
            } catch (e: Exception) {
                _error.value = "Error al validar modelo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Refresca todos los datos de recomendaciones
     */
    fun refreshRecommendations() {
        loadUserRecommendations()
        classifyCurrentUser()
        predictUserPerformance()
        loadSimilarityStats()
        analyzeLearningPatterns()
    }
    
    /**
     * Limpia el error actual
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Obtiene la descripción de la clasificación del usuario
     */
    fun getClassificationDescription(classification: String): String {
        return when (classification.lowercase()) {
            "beginner" -> "Usuario principiante - Comienza con conceptos básicos"
            "intermediate" -> "Usuario intermedio - Domina conceptos fundamentales"
            "advanced" -> "Usuario avanzado - Listo para conceptos complejos"
            "expert" -> "Usuario experto - Puede enseñar a otros"
            else -> "Clasificación no disponible"
        }
    }
    
    /**
     * Obtiene el color asociado a la clasificación
     */
    fun getClassificationColor(classification: String): String {
        return when (classification.lowercase()) {
            "beginner" -> "#4CAF50" // Verde
            "intermediate" -> "#2196F3" // Azul
            "advanced" -> "#FF9800" // Naranja
            "expert" -> "#9C27B0" // Púrpura
            else -> "#757575" // Gris
        }
    }
    
    /**
     * Formatea el porcentaje de similitud
     */
    fun formatSimilarity(similarity: Double): String {
        return "${(similarity * 100).toInt()}%"
    }
    
    /**
     * Formatea la confianza de predicción
     */
    fun formatConfidence(confidence: Double): String {
        return "${(confidence * 100).toInt()}%"
    }
}
