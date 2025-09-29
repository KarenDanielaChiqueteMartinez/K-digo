package com.example.kodelearn.data.ml

import com.example.kodelearn.data.database.entities.User
import com.example.kodelearn.data.database.entities.Progress
import com.example.kodelearn.data.repository.KodeLearnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

/**
 * Servicio que integra el algoritmo KNN con el repositorio para proporcionar
 * recomendaciones y análisis de usuarios basados en similitud
 */
class UserRecommendationService(
    private val repository: KodeLearnRepository,
    private val knnAlgorithm: KNNAlgorithm = KNNAlgorithm(k = 5)
) {
    
    /**
     * Inicializa el servicio entrenando el modelo con todos los usuarios
     */
    suspend fun initialize() {
        val allUsers = repository.getAllUsers().first()
        val allProgress = mutableListOf<Progress>()
        
        // Obtener progreso de todos los usuarios
        allUsers.forEach { user ->
            val userProgress = repository.getAllProgressByUser(user.id).first()
            allProgress.addAll(userProgress)
        }
        
        // Crear vectores de características para todos los usuarios
        val featureVectors = allUsers.map { user ->
            val userProgress = allProgress.filter { it.userId == user.id }
            UserFeatureVector.fromUserData(user, userProgress)
        }
        
        // Entrenar el modelo KNN
        knnAlgorithm.train(featureVectors)
    }
    
    /**
     * Obtiene recomendaciones de usuarios similares para un usuario específico
     */
    suspend fun getSimilarUsers(userId: Int, limit: Int = 10): List<UserRecommendation> {
        val user = repository.getCurrentUser().first() ?: return emptyList()
        val userProgress = repository.getAllProgressByUser(userId).first()
        
        val userFeatureVector = UserFeatureVector.fromUserData(user, userProgress)
        return knnAlgorithm.recommendSimilarUsers(userFeatureVector, limit)
    }
    
    /**
     * Clasifica a un usuario en una categoría de aprendizaje
     */
    suspend fun classifyUser(userId: Int): String {
        val user = repository.getCurrentUser().first() ?: return "unknown"
        val userProgress = repository.getAllProgressByUser(userId).first()
        
        val userFeatureVector = UserFeatureVector.fromUserData(user, userProgress)
        return knnAlgorithm.classifyUser(userFeatureVector)
    }
    
    /**
     * Predice el rendimiento futuro de un usuario
     */
    suspend fun predictUserPerformance(userId: Int): PerformancePrediction {
        val user = repository.getCurrentUser().first() ?: return PerformancePrediction(0f, 0f, 0f, 0.0)
        val userProgress = repository.getAllProgressByUser(userId).first()
        
        val userFeatureVector = UserFeatureVector.fromUserData(user, userProgress)
        return knnAlgorithm.predictPerformance(userFeatureVector)
    }
    
    /**
     * Calcula la similitud entre dos usuarios específicos
     */
    suspend fun calculateUserSimilarity(userId1: Int, userId2: Int): Double {
        val user1 = repository.getCurrentUser().first() ?: return 0.0
        val user1Progress = repository.getAllProgressByUser(userId1).first()
        val user2Progress = repository.getAllProgressByUser(userId2).first()
        
        val user1Vector = UserFeatureVector.fromUserData(user1, user1Progress)
        val user2Vector = UserFeatureVector.fromUserData(user1, user2Progress)
        
        val distance = user1Vector.distanceTo(user2Vector)
        return 1.0 / (1.0 + distance) // Convertir distancia a similitud
    }
    
    /**
     * Obtiene estadísticas de similitud para un usuario
     */
    suspend fun getUserSimilarityStats(userId: Int): SimilarityStats {
        val user = repository.getCurrentUser().first() ?: return SimilarityStats(0.0, 0.0, 0.0)
        val userProgress = repository.getAllProgressByUser(userId).first()
        
        val userFeatureVector = UserFeatureVector.fromUserData(user, userProgress)
        val similarityScore = knnAlgorithm.calculateSimilarityScore(userFeatureVector)
        val neighbors = knnAlgorithm.findNearestNeighbors(userFeatureVector)
        
        val avgSimilarity = if (neighbors.isNotEmpty()) {
            neighbors.map { 1.0 / (1.0 + it.distance) }.average()
        } else {
            0.0
        }
        
        val maxSimilarity = if (neighbors.isNotEmpty()) {
            neighbors.map { 1.0 / (1.0 + it.distance) }.maxOrNull() ?: 0.0
        } else {
            0.0
        }
        
        return SimilarityStats(
            overallSimilarity = similarityScore,
            averageSimilarity = avgSimilarity,
            maxSimilarity = maxSimilarity
        )
    }
    
    /**
     * Obtiene usuarios con características similares para un módulo específico
     */
    suspend fun getUsersSimilarForModule(moduleId: Int, limit: Int = 5): List<UserRecommendation> {
        val currentUser = repository.getCurrentUser().first() ?: return emptyList()
        val currentUserProgress = repository.getAllProgressByUser(currentUser.id).first()
        
        // Filtrar progreso solo para el módulo específico
        val moduleProgress = currentUserProgress.filter { it.moduleId == moduleId }
        
        if (moduleProgress.isEmpty()) {
            return emptyList()
        }
        
        val userFeatureVector = UserFeatureVector.fromUserData(currentUser, moduleProgress)
        return knnAlgorithm.recommendSimilarUsers(userFeatureVector, limit)
    }
    
    /**
     * Analiza patrones de aprendizaje de usuarios similares
     */
    suspend fun analyzeLearningPatterns(userId: Int): LearningPatternAnalysis {
        val user = repository.getCurrentUser().first() ?: return LearningPatternAnalysis(emptyList(), emptyList())
        val userProgress = repository.getAllProgressByUser(userId).first()
        
        val userFeatureVector = UserFeatureVector.fromUserData(user, userProgress)
        val neighbors = knnAlgorithm.findNearestNeighbors(userFeatureVector)
        
        val commonPatterns = mutableListOf<String>()
        val recommendations = mutableListOf<String>()
        
        if (neighbors.isNotEmpty()) {
            // Analizar patrones comunes
            val avgAccuracy = neighbors.map { it.user.accuracyRate }.average()
            val avgSpeed = neighbors.map { it.user.learningSpeed }.average()
            val avgFrequency = neighbors.map { it.user.studyFrequency }.average()
            
            if (avgAccuracy > 0.7) {
                commonPatterns.add("Usuarios similares mantienen alta precisión")
            }
            if (avgSpeed > 2.0) {
                commonPatterns.add("Usuarios similares aprenden rápidamente")
            }
            if (avgFrequency > 3.0) {
                commonPatterns.add("Usuarios similares estudian frecuentemente")
            }
            
            // Generar recomendaciones basadas en patrones
            if (userFeatureVector.accuracyRate < avgAccuracy - 0.1) {
                recommendations.add("Considera revisar las lecciones antes de continuar")
            }
            if (userFeatureVector.learningSpeed < avgSpeed - 0.5) {
                recommendations.add("Intenta dedicar más tiempo diario al estudio")
            }
            if (userFeatureVector.studyFrequency < avgFrequency - 1.0) {
                recommendations.add("Establece una rutina de estudio más regular")
            }
        }
        
        return LearningPatternAnalysis(commonPatterns, recommendations)
    }
    
    /**
     * Valida el modelo con datos de prueba
     */
    suspend fun validateModel(): ValidationResult {
        val allUsers = repository.getAllUsers().first()
        val allProgress = mutableListOf<Progress>()
        
        allUsers.forEach { user ->
            val userProgress = repository.getAllProgressByUser(user.id).first()
            allProgress.addAll(userProgress)
        }
        
        val featureVectors = allUsers.map { user ->
            val userProgress = allProgress.filter { it.userId == user.id }
            UserFeatureVector.fromUserData(user, userProgress)
        }
        
        // Usar 80% para entrenamiento y 20% para validación
        val splitIndex = (featureVectors.size * 0.8).toInt()
        val trainingData = featureVectors.take(splitIndex)
        val testData = featureVectors.drop(splitIndex)
        
        val tempKNN = KNNAlgorithm(k = 5)
        tempKNN.train(trainingData)
        
        return tempKNN.validateModel(testData)
    }
}

/**
 * Estadísticas de similitud de un usuario
 */
data class SimilarityStats(
    val overallSimilarity: Double,
    val averageSimilarity: Double,
    val maxSimilarity: Double
)

/**
 * Análisis de patrones de aprendizaje
 */
data class LearningPatternAnalysis(
    val commonPatterns: List<String>,
    val recommendations: List<String>
)
