package com.example.kodelearn.data.ml

import kotlin.math.sqrt

/**
 * Implementación del algoritmo K-Nearest Neighbors (KNN) para clasificación y recomendación
 * de usuarios basada en similitud de características de aprendizaje
 */
class KNNAlgorithm(
    private val k: Int = 5, // Número de vecinos más cercanos a considerar
    private val useWeightedDistance: Boolean = false,
    private val featureWeights: FeatureWeights = FeatureWeights()
) {
    
    private var trainingData: List<UserFeatureVector> = emptyList()
    
    /**
     * Entrena el modelo con datos de usuarios
     */
    fun train(trainingData: List<UserFeatureVector>) {
        this.trainingData = trainingData
    }
    
    /**
     * Encuentra los K vecinos más cercanos a un usuario dado
     */
    fun findNearestNeighbors(targetUser: UserFeatureVector): List<NeighborResult> {
        if (trainingData.isEmpty()) {
            return emptyList()
        }
        
        val distances = trainingData
            .filter { it.userId != targetUser.userId } // Excluir al usuario objetivo
            .map { user ->
                val distance = if (useWeightedDistance) {
                    targetUser.weightedDistanceTo(user, featureWeights)
                } else {
                    targetUser.distanceTo(user)
                }
                NeighborResult(user, distance)
            }
            .sortedBy { it.distance }
            .take(k)
        
        return distances
    }
    
    /**
     * Clasifica a un usuario en una categoría basada en sus vecinos más cercanos
     */
    fun classifyUser(targetUser: UserFeatureVector): String {
        val neighbors = findNearestNeighbors(targetUser)
        
        if (neighbors.isEmpty()) {
            return "unknown"
        }
        
        // Contar las categorías de los vecinos más cercanos
        val categoryCounts = neighbors.groupBy { it.user.category }
            .mapValues { it.value.size }
        
        // Devolver la categoría más común
        return categoryCounts.maxByOrNull { it.value }?.key ?: "unknown"
    }
    
    /**
     * Calcula la similitud promedio con los vecinos más cercanos
     */
    fun calculateSimilarityScore(targetUser: UserFeatureVector): Double {
        val neighbors = findNearestNeighbors(targetUser)
        
        if (neighbors.isEmpty()) {
            return 0.0
        }
        
        // Convertir distancias a similitudes (distancia menor = mayor similitud)
        val similarities = neighbors.map { neighbor ->
            1.0 / (1.0 + neighbor.distance) // Normalizar entre 0 y 1
        }
        
        return similarities.average()
    }
    
    /**
     * Recomienda usuarios similares para colaboración o comparación
     */
    fun recommendSimilarUsers(targetUser: UserFeatureVector, limit: Int = 10): List<UserRecommendation> {
        val neighbors = findNearestNeighbors(targetUser)
        
        return neighbors.take(limit).map { neighbor ->
            val similarity = 1.0 / (1.0 + neighbor.distance)
            UserRecommendation(
                userId = neighbor.user.userId,
                similarity = similarity,
                sharedCharacteristics = findSharedCharacteristics(targetUser, neighbor.user)
            )
        }
    }
    
    /**
     * Predice el rendimiento futuro de un usuario basado en usuarios similares
     */
    fun predictPerformance(targetUser: UserFeatureVector): PerformancePrediction {
        val neighbors = findNearestNeighbors(targetUser)
        
        if (neighbors.isEmpty()) {
            return PerformancePrediction(
                predictedAccuracy = 0.0f,
                predictedSpeed = 0.0f,
                predictedConsistency = 0.0f,
                confidence = 0.0
            )
        }
        
        // Calcular promedios ponderados por similitud
        var totalWeight = 0.0
        var weightedAccuracy = 0.0
        var weightedSpeed = 0.0
        var weightedConsistency = 0.0
        
        neighbors.forEach { neighbor ->
            val weight = 1.0 / (1.0 + neighbor.distance)
            totalWeight += weight
            
            weightedAccuracy += neighbor.user.accuracyRate * weight
            weightedSpeed += neighbor.user.learningSpeed * weight
            weightedConsistency += neighbor.user.consistency * weight
        }
        
        val predictedAccuracy = (weightedAccuracy / totalWeight).toFloat()
        val predictedSpeed = (weightedSpeed / totalWeight).toFloat()
        val predictedConsistency = (weightedConsistency / totalWeight).toFloat()
        
        // Calcular confianza basada en la similitud promedio
        val confidence = calculateSimilarityScore(targetUser)
        
        return PerformancePrediction(
            predictedAccuracy = predictedAccuracy,
            predictedSpeed = predictedSpeed,
            predictedConsistency = predictedConsistency,
            confidence = confidence
        )
    }
    
    /**
     * Encuentra características compartidas entre dos usuarios
     */
    private fun findSharedCharacteristics(user1: UserFeatureVector, user2: UserFeatureVector): List<String> {
        val characteristics = mutableListOf<String>()
        
        // Comparar características con un umbral de similitud
        val threshold = 0.1f
        
        if (kotlin.math.abs(user1.accuracyRate - user2.accuracyRate) < threshold) {
            characteristics.add("Tasa de aciertos similar")
        }
        if (kotlin.math.abs(user1.learningSpeed - user2.learningSpeed) < threshold) {
            characteristics.add("Velocidad de aprendizaje similar")
        }
        if (kotlin.math.abs(user1.studyFrequency - user2.studyFrequency) < threshold) {
            characteristics.add("Frecuencia de estudio similar")
        }
        if (kotlin.math.abs(user1.consistency - user2.consistency) < threshold) {
            characteristics.add("Consistencia similar")
        }
        if (kotlin.math.abs(user1.difficultyPreference - user2.difficultyPreference) < threshold) {
            characteristics.add("Preferencia de dificultad similar")
        }
        
        return characteristics
    }
    
    /**
     * Valida el modelo usando validación cruzada
     */
    fun validateModel(testData: List<UserFeatureVector>): ValidationResult {
        var correctPredictions = 0
        var totalPredictions = 0
        
        testData.forEach { testUser ->
            val predictedCategory = classifyUser(testUser)
            if (predictedCategory == testUser.category) {
                correctPredictions++
            }
            totalPredictions++
        }
        
        val accuracy = if (totalPredictions > 0) {
            correctPredictions.toDouble() / totalPredictions
        } else {
            0.0
        }
        
        return ValidationResult(
            accuracy = accuracy,
            correctPredictions = correctPredictions,
            totalPredictions = totalPredictions
        )
    }
    
    /**
     * Optimiza los pesos de características basado en el rendimiento
     */
    fun optimizeWeights(validationData: List<UserFeatureVector>): FeatureWeights {
        val currentWeights = featureWeights
        var bestWeights = currentWeights
        var bestAccuracy = 0.0
        
        // Probar diferentes combinaciones de pesos
        val weightOptions = listOf(0.5, 1.0, 1.5, 2.0)
        
        for (accuracyWeight in weightOptions) {
            for (speedWeight in weightOptions) {
                for (frequencyWeight in weightOptions) {
                    val testWeights = FeatureWeights(
                        accuracy = accuracyWeight,
                        learningSpeed = speedWeight,
                        studyFrequency = frequencyWeight,
                        consistency = currentWeights.consistency,
                        difficultyPreference = currentWeights.difficultyPreference,
                        timePerLesson = currentWeights.timePerLesson,
                        completionRate = currentWeights.completionRate,
                        streakLength = currentWeights.streakLength,
                        xpEarned = currentWeights.xpEarned
                    )
                    
                    val tempKNN = KNNAlgorithm(k, true, testWeights)
                    tempKNN.train(trainingData)
                    val validationResult = tempKNN.validateModel(validationData)
                    
                    if (validationResult.accuracy > bestAccuracy) {
                        bestAccuracy = validationResult.accuracy
                        bestWeights = testWeights
                    }
                }
            }
        }
        
        return bestWeights
    }
}

/**
 * Resultado de un vecino más cercano
 */
data class NeighborResult(
    val user: UserFeatureVector,
    val distance: Double
)

/**
 * Recomendación de usuario similar
 */
data class UserRecommendation(
    val userId: Int,
    val similarity: Double,
    val sharedCharacteristics: List<String>
)

/**
 * Predicción de rendimiento futuro
 */
data class PerformancePrediction(
    val predictedAccuracy: Float,
    val predictedSpeed: Float,
    val predictedConsistency: Float,
    val confidence: Double
)

/**
 * Resultado de validación del modelo
 */
data class ValidationResult(
    val accuracy: Double,
    val correctPredictions: Int,
    val totalPredictions: Int
)
