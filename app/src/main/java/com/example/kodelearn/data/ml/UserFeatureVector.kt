package com.example.kodelearn.data.ml

import com.example.kodelearn.data.database.entities.User
import com.example.kodelearn.data.database.entities.Progress
import kotlin.math.sqrt

/**
 * Vector de características que representa a un usuario para el algoritmo KNN
 * Incluye métricas de rendimiento y comportamiento de aprendizaje
 */
data class UserFeatureVector(
    val userId: Int,
    val accuracyRate: Float,        // Tasa de aciertos (0.0 - 1.0)
    val learningSpeed: Float,       // Velocidad de aprendizaje (lecciones/día)
    val studyFrequency: Float,      // Frecuencia de estudio (sesiones/semana)
    val consistency: Float,         // Consistencia en el estudio (0.0 - 1.0)
    val difficultyPreference: Float, // Preferencia por dificultad (0.0 - 1.0)
    val timePerLesson: Float,       // Tiempo promedio por lección (minutos)
    val completionRate: Float,      // Tasa de finalización de módulos (0.0 - 1.0)
    val streakLength: Float,        // Longitud de racha actual (normalizada)
    val xpEarned: Float,            // XP ganado (normalizado)
    val category: String = "unknown" // Categoría del usuario (ej: "beginner", "intermediate", "advanced")
) {
    
    /**
     * Calcula la distancia euclidiana entre este vector y otro
     */
    fun distanceTo(other: UserFeatureVector): Double {
        val accuracyDiff = (accuracyRate - other.accuracyRate).toDouble()
        val speedDiff = (learningSpeed - other.learningSpeed).toDouble()
        val frequencyDiff = (studyFrequency - other.studyFrequency).toDouble()
        val consistencyDiff = (consistency - other.consistency).toDouble()
        val difficultyDiff = (difficultyPreference - other.difficultyPreference).toDouble()
        val timeDiff = (timePerLesson - other.timePerLesson).toDouble()
        val completionDiff = (completionRate - other.completionRate).toDouble()
        val streakDiff = (streakLength - other.streakLength).toDouble()
        val xpDiff = (xpEarned - other.xpEarned).toDouble()
        
        return sqrt(
            accuracyDiff * accuracyDiff +
            speedDiff * speedDiff +
            frequencyDiff * frequencyDiff +
            consistencyDiff * consistencyDiff +
            difficultyDiff * difficultyDiff +
            timeDiff * timeDiff +
            completionDiff * completionDiff +
            streakDiff * streakDiff +
            xpDiff * xpDiff
        )
    }
    
    /**
     * Calcula la distancia euclidiana ponderada entre este vector y otro
     * Permite dar más peso a ciertas características
     */
    fun weightedDistanceTo(other: UserFeatureVector, weights: FeatureWeights = FeatureWeights()): Double {
        val accuracyDiff = (accuracyRate - other.accuracyRate).toDouble()
        val speedDiff = (learningSpeed - other.learningSpeed).toDouble()
        val frequencyDiff = (studyFrequency - other.studyFrequency).toDouble()
        val consistencyDiff = (consistency - other.consistency).toDouble()
        val difficultyDiff = (difficultyPreference - other.difficultyPreference).toDouble()
        val timeDiff = (timePerLesson - other.timePerLesson).toDouble()
        val completionDiff = (completionRate - other.completionRate).toDouble()
        val streakDiff = (streakLength - other.streakLength).toDouble()
        val xpDiff = (xpEarned - other.xpEarned).toDouble()
        
        return sqrt(
            weights.accuracy * accuracyDiff * accuracyDiff +
            weights.learningSpeed * speedDiff * speedDiff +
            weights.studyFrequency * frequencyDiff * frequencyDiff +
            weights.consistency * consistencyDiff * consistencyDiff +
            weights.difficultyPreference * difficultyDiff * difficultyDiff +
            weights.timePerLesson * timeDiff * timeDiff +
            weights.completionRate * completionDiff * completionDiff +
            weights.streakLength * streakDiff * streakDiff +
            weights.xpEarned * xpDiff * xpDiff
        )
    }
    
    companion object {
        /**
         * Crea un UserFeatureVector a partir de datos de usuario y progreso
         */
        fun fromUserData(
            user: User,
            progressList: List<Progress>,
            totalLessons: Int = 100, // Total de lecciones disponibles
            maxStreak: Int = 30,     // Racha máxima esperada
            maxXP: Int = 10000       // XP máximo esperado
        ): UserFeatureVector {
            // Calcular tasa de aciertos basada en XP ganado vs tiempo
            val accuracyRate = calculateAccuracyRate(user, progressList)
            
            // Calcular velocidad de aprendizaje (lecciones completadas por día)
            val learningSpeed = calculateLearningSpeed(progressList)
            
            // Calcular frecuencia de estudio (sesiones por semana)
            val studyFrequency = calculateStudyFrequency(progressList)
            
            // Calcular consistencia (regularidad en el estudio)
            val consistency = calculateConsistency(progressList)
            
            // Calcular preferencia por dificultad (basada en progreso en módulos difíciles)
            val difficultyPreference = calculateDifficultyPreference(progressList)
            
            // Calcular tiempo promedio por lección (estimado)
            val timePerLesson = calculateTimePerLesson(progressList)
            
            // Calcular tasa de finalización
            val completionRate = calculateCompletionRate(progressList, totalLessons)
            
            // Normalizar racha
            val streakLength = (user.dailyStreak.toFloat() / maxStreak).coerceAtMost(1.0f)
            
            // Normalizar XP
            val xpEarned = (user.totalXP.toFloat() / maxXP).coerceAtMost(1.0f)
            
            return UserFeatureVector(
                userId = user.id,
                accuracyRate = accuracyRate,
                learningSpeed = learningSpeed,
                studyFrequency = studyFrequency,
                consistency = consistency,
                difficultyPreference = difficultyPreference,
                timePerLesson = timePerLesson,
                completionRate = completionRate,
                streakLength = streakLength,
                xpEarned = xpEarned
            )
        }
        
        private fun calculateAccuracyRate(user: User, progressList: List<Progress>): Float {
            // Basado en XP ganado vs tiempo invertido
            val totalProgress = progressList.sumOf { it.progressPercentage }.toFloat()
            val timeInvested = progressList.size.toFloat()
            return if (timeInvested > 0) (user.totalXP.toFloat() / (totalProgress * 100)).coerceIn(0f, 1f) else 0f
        }
        
        private fun calculateLearningSpeed(progressList: List<Progress>): Float {
            // Lecciones completadas por día (estimado)
            val totalLessons = progressList.sumOf { it.lessonsCompleted }
            val daysSinceStart = 30f // Asumir 30 días de uso
            return (totalLessons.toFloat() / daysSinceStart).coerceAtMost(10f) // Máximo 10 lecciones por día
        }
        
        private fun calculateStudyFrequency(progressList: List<Progress>): Float {
            // Sesiones por semana basadas en accesos
            val uniqueDays = progressList.map { it.lastAccessedAt }.distinct().size
            val weeks = 4f // Asumir 4 semanas
            return (uniqueDays.toFloat() / weeks).coerceAtMost(7f) // Máximo 7 sesiones por semana
        }
        
        private fun calculateConsistency(progressList: List<Progress>): Float {
            // Regularidad en el estudio basada en la distribución de accesos
            if (progressList.isEmpty()) return 0f
            
            val accessTimes = progressList.map { it.lastAccessedAt }.sorted()
            val intervals = mutableListOf<Long>()
            
            for (i in 1 until accessTimes.size) {
                intervals.add(accessTimes[i] - accessTimes[i-1])
            }
            
            if (intervals.isEmpty()) return 0f
            
            val avgInterval = intervals.average()
            val variance = intervals.map { (it - avgInterval) * (it - avgInterval) }.average()
            val consistency = 1f - (variance / (avgInterval * avgInterval)).toFloat()
            
            return consistency.coerceIn(0f, 1f)
        }
        
        private fun calculateDifficultyPreference(progressList: List<Progress>): Float {
            // Basado en la distribución de progreso en diferentes módulos
            val progressValues = progressList.map { it.progressPercentage }
            val avgProgress = progressValues.average().toFloat()
            val variance = progressValues.map { (it - avgProgress) * (it - avgProgress) }.average().toFloat()
            
            // Mayor varianza indica preferencia por dificultad variada
            return (variance / 10000f).coerceIn(0f, 1f) // Normalizar
        }
        
        private fun calculateTimePerLesson(progressList: List<Progress>): Float {
            // Tiempo estimado por lección (minutos)
            val totalLessons = progressList.sumOf { it.lessonsCompleted }
            val estimatedTimePerLesson = 15f // 15 minutos por lección por defecto
            
            return if (totalLessons > 0) {
                estimatedTimePerLesson
            } else {
                0f
            }
        }
        
        private fun calculateCompletionRate(progressList: List<Progress>, totalLessons: Int): Float {
            val completedLessons = progressList.sumOf { it.lessonsCompleted }
            return if (totalLessons > 0) {
                (completedLessons.toFloat() / totalLessons).coerceAtMost(1f)
            } else {
                0f
            }
        }
    }
}

/**
 * Pesos para el cálculo de distancia ponderada en KNN
 */
data class FeatureWeights(
    val accuracy: Double = 1.0,
    val learningSpeed: Double = 1.0,
    val studyFrequency: Double = 1.0,
    val consistency: Double = 1.0,
    val difficultyPreference: Double = 1.0,
    val timePerLesson: Double = 1.0,
    val completionRate: Double = 1.0,
    val streakLength: Double = 1.0,
    val xpEarned: Double = 1.0
)
