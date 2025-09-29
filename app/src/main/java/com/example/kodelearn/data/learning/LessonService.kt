package com.example.kodelearn.data.learning

import com.example.kodelearn.data.content.LessonContent
import com.example.kodelearn.data.content.ModuleContent
import com.example.kodelearn.data.repository.KodeLearnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Servicio que maneja las lecciones y actualiza los datos dinámicamente
 */
class LessonService(
    private val repository: KodeLearnRepository,
    private val learningSessionService: LearningSessionService
) {
    
    /**
     * Obtiene el contenido de una lección específica
     */
    fun getLessonContent(moduleId: Int, lessonId: Int): LessonContent? {
        return when (moduleId) {
            1 -> ModuleContent.introductionModule.lessons.find { it.id == lessonId }
            else -> null
        }
    }
    
    /**
     * Obtiene todas las lecciones de un módulo
     */
    fun getModuleLessons(moduleId: Int): List<LessonContent> {
        return when (moduleId) {
            1 -> ModuleContent.introductionModule.lessons
            else -> emptyList()
        }
    }
    
    /**
     * Inicia una lección y actualiza el progreso
     */
    suspend fun startLesson(userId: Int, moduleId: Int, lessonId: Int) {
        val lessonContent = getLessonContent(moduleId, lessonId)
        if (lessonContent != null) {
            learningSessionService.startLearningSession(userId, moduleId, lessonId)
        }
    }
    
    /**
     * Procesa la respuesta del usuario y actualiza el progreso
     */
    suspend fun processAnswer(
        userId: Int,
        moduleId: Int,
        lessonId: Int,
        questionId: Int,
        selectedAnswer: Int,
        timeSpent: Long
    ): AnswerResult {
        val lessonContent = getLessonContent(moduleId, lessonId)
        val question = lessonContent?.questions?.find { it.id == questionId }
        
        if (question == null) {
            return AnswerResult(false, "Pregunta no encontrada", 0)
        }
        
        val isCorrect = selectedAnswer == question.correctAnswer
        val xpGained = if (isCorrect) {
            calculateXPForCorrectAnswer(lessonContent, timeSpent)
        } else {
            calculateXPForIncorrectAnswer(lessonContent, timeSpent)
        }
        
        // Actualizar progreso en la sesión
        learningSessionService.updateProgress(userId, moduleId, lessonId, isCorrect, timeSpent)
        
        // Actualizar XP del usuario
        updateUserXP(userId, xpGained)
        
        // Marcar lección como completada si es correcta
        if (isCorrect) {
            markLessonAsCompleted(userId, moduleId, lessonId)
        }
        
        return AnswerResult(
            isCorrect = isCorrect,
            explanation = question.explanation,
            xpGained = xpGained
        )
    }
    
    /**
     * Completa una lección y actualiza el progreso del módulo
     */
    suspend fun completeLesson(userId: Int, moduleId: Int, lessonId: Int) {
        val lessonContent = getLessonContent(moduleId, lessonId)
        if (lessonContent != null) {
            // Completar en la sesión de aprendizaje
            learningSessionService.completeLesson(userId, moduleId, lessonId)
            
            // Actualizar progreso del módulo
            updateModuleProgress(userId, moduleId)
            
            // Actualizar estadísticas del usuario
            updateUserStatistics(userId, lessonContent)
        }
    }
    
    /**
     * Termina la lección actual
     */
    suspend fun endLesson(userId: Int) {
        learningSessionService.endCurrentSession()
    }
    
    /**
     * Obtiene el progreso del usuario en un módulo específico
     */
    suspend fun getModuleProgress(userId: Int, moduleId: Int): ModuleProgressInfo {
        val progress = repository.getProgressByModule(userId, moduleId).first()
        val moduleLessons = getModuleLessons(moduleId)
        val totalLessons = moduleLessons.size
        val completedLessons = progress?.lessonsCompleted ?: 0 // Usar progreso real
        val percentage = if (totalLessons > 0) {
            (completedLessons.toFloat() / totalLessons * 100).coerceAtMost(100f)
        } else {
            0f
        }
        
        return ModuleProgressInfo(
            moduleId = moduleId,
            totalLessons = totalLessons,
            completedLessons = completedLessons,
            progressPercentage = percentage,
            isCompleted = percentage >= 100f,
            currentLesson = completedLessons + 1
        )
    }
    
    /**
     * Obtiene las estadísticas generales del usuario
     */
    suspend fun getUserStats(userId: Int): UserStats {
        val user = repository.getCurrentUser().first() ?: return UserStats()
        val learningStats = learningSessionService.getUserLearningStats(userId)
        
        return UserStats(
            userId = userId,
            name = user.name,
            totalXP = user.totalXP,
            coins = user.coins,
            hearts = user.hearts,
            dailyStreak = user.dailyStreak,
            league = user.league,
            totalSessions = learningStats.totalSessions,
            totalTimeSpent = learningStats.totalTimeSpent,
            lessonsCompleted = learningStats.lessonsCompleted,
            averageAccuracy = learningStats.averageAccuracy
        )
    }
    
    /**
     * Calcula XP para respuesta correcta
     */
    private fun calculateXPForCorrectAnswer(lessonContent: LessonContent, timeSpent: Long): Int {
        val baseXP = lessonContent.xpReward
        val timeBonus = calculateTimeBonus(lessonContent.timeEstimate, timeSpent)
        return baseXP + timeBonus
    }
    
    /**
     * Calcula XP para respuesta incorrecta
     */
    private fun calculateXPForIncorrectAnswer(lessonContent: LessonContent, timeSpent: Long): Int {
        // Dar XP reducido por intentar
        return (lessonContent.xpReward * 0.3).toInt()
    }
    
    /**
     * Calcula bonus de tiempo
     */
    private fun calculateTimeBonus(estimatedMinutes: Int, timeSpent: Long): Int {
        val estimatedMillis = estimatedMinutes * 60 * 1000L
        val ratio = timeSpent.toDouble() / estimatedMillis
        
        return when {
            ratio < 0.5 -> 5  // Muy rápido
            ratio < 1.0 -> 3  // Rápido
            ratio < 1.5 -> 1  // Normal
            else -> 0         // Lento
        }
    }
    
    /**
     * Actualiza XP del usuario
     */
    private suspend fun updateUserXP(userId: Int, xpGained: Int) {
        val user = repository.getCurrentUser().first()
        user?.let {
            repository.updateXP(it.totalXP + xpGained)
            
            // Actualizar monedas (1 moneda por cada 10 XP)
            val coinsGained = xpGained / 10
            if (coinsGained > 0) {
                repository.updateCoins(it.coins + coinsGained)
            }
        }
    }
    
    /**
     * Actualiza progreso del módulo
     */
    private suspend fun updateModuleProgress(userId: Int, moduleId: Int) {
        val progress = repository.getProgressByModule(userId, moduleId).first()
        val moduleLessons = getModuleLessons(moduleId)
        val totalLessons = moduleLessons.size
        val completedLessons = (progress?.lessonsCompleted ?: 0) + 1
        val percentage = if (totalLessons > 0) {
            (completedLessons.toFloat() / totalLessons * 100).coerceAtMost(100f)
        } else {
            0f
        }
        val isCompleted = completedLessons >= totalLessons
        
        repository.updateModuleProgress(
            userId = userId,
            moduleId = moduleId,
            lessonsCompleted = completedLessons,
            percentage = percentage,
            isCompleted = isCompleted
        )
    }
    
    /**
     * Actualiza estadísticas del usuario
     */
    private suspend fun updateUserStatistics(userId: Int, lessonContent: LessonContent) {
        val user = repository.getCurrentUser().first() ?: return
        
        // Actualizar racha diaria si es necesario
        val today = System.currentTimeMillis()
        val lastStudyDate = lessonContent.id.toLong() // Simplificado para demo
        
        // Lógica simple para racha
        val currentStreak = if (today - lastStudyDate < 86400000) { // 24 horas
            user.dailyStreak + 1
        } else {
            1
        }
        
        repository.updateDailyStreak(currentStreak)
    }
    
    /**
     * Marca una lección como completada y actualiza el progreso
     */
    suspend fun markLessonAsCompleted(userId: Int, moduleId: Int, lessonId: Int) {
        // Obtener progreso actual
        val currentProgress = repository.getProgressByModule(userId, moduleId).first()
        val completedLessons = (currentProgress?.lessonsCompleted ?: 0) + 1
        val totalLessons = getModuleLessons(moduleId).size
        
        // Crear o actualizar progreso
        val progress = com.example.kodelearn.data.database.entities.Progress(
            id = currentProgress?.id ?: 0,
            userId = userId,
            moduleId = moduleId,
            lessonsCompleted = completedLessons,
            progressPercentage = (completedLessons.toFloat() / totalLessons * 100).coerceAtMost(100f),
            isCompleted = completedLessons >= totalLessons,
            lastAccessedAt = System.currentTimeMillis()
        )
        
        repository.insertProgress(progress)
    }

    /**
     * Simula la finalización completa de un módulo para testing
     */
    suspend fun simulateModuleCompletion(userId: Int, moduleId: Int) {
        val moduleLessons = getModuleLessons(moduleId)
        val totalLessons = moduleLessons.size
        
        // Crear progreso completo
        val progress = com.example.kodelearn.data.database.entities.Progress(
            id = 0,
            userId = userId,
            moduleId = moduleId,
            lessonsCompleted = totalLessons,
            progressPercentage = 100f,
            isCompleted = true,
            lastAccessedAt = System.currentTimeMillis()
        )
        
        repository.insertProgress(progress)
    }
}

/**
 * Resultado de una respuesta
 */
data class AnswerResult(
    val isCorrect: Boolean,
    val explanation: String,
    val xpGained: Int
)

/**
 * Información de progreso de un módulo
 */
data class ModuleProgressInfo(
    val moduleId: Int,
    val totalLessons: Int,
    val completedLessons: Int,
    val progressPercentage: Float,
    val isCompleted: Boolean,
    val currentLesson: Int
)

/**
 * Estadísticas del usuario
 */
data class UserStats(
    val userId: Int = 0,
    val name: String = "",
    val totalXP: Int = 0,
    val coins: Int = 0,
    val hearts: Int = 5,
    val dailyStreak: Int = 0,
    val league: String = "Bronze",
    val totalSessions: Int = 0,
    val totalTimeSpent: Long = 0,
    val lessonsCompleted: Int = 0,
    val averageAccuracy: Float = 0f
)
