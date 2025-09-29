package com.example.kodelearn.data.learning

import com.example.kodelearn.data.database.entities.Progress
import com.example.kodelearn.data.repository.KodeLearnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

/**
 * Servicio que maneja las sesiones de aprendizaje en tiempo real
 * Genera datos dinámicos para XP, tiempo de estudio y progreso
 */
class LearningSessionService(
    private val repository: KodeLearnRepository
) {
    private val _currentSession = MutableStateFlow<LearningSession?>(null)
    val currentSession: StateFlow<LearningSession?> = _currentSession.asStateFlow()
    
    private val _sessionHistory = MutableStateFlow<List<LearningSession>>(emptyList())
    val sessionHistory: StateFlow<List<LearningSession>> = _sessionHistory.asStateFlow()
    
    private var sessionStartTime: Long = 0
    private var isSessionActive: Boolean = false
    
    /**
     * Inicia una nueva sesión de aprendizaje
     */
    suspend fun startLearningSession(userId: Int, moduleId: Int, lessonId: Int) {
        if (isSessionActive) {
            endCurrentSession()
        }
        
        sessionStartTime = System.currentTimeMillis()
        isSessionActive = true
        
        val session = LearningSession(
            id = generateSessionId(),
            userId = userId,
            moduleId = moduleId,
            lessonId = lessonId,
            startTime = sessionStartTime,
            endTime = null,
            xpEarned = 0,
            lessonsCompleted = 0,
            accuracy = 0f,
            difficulty = LessonDifficulty.MEDIUM
        )
        
        _currentSession.value = session
    }
    
    /**
     * Actualiza el progreso durante la sesión
     */
    suspend fun updateProgress(
        userId: Int,
        moduleId: Int,
        lessonId: Int,
        isCorrect: Boolean,
        timeSpent: Long
    ) {
        val session = _currentSession.value ?: return
        
        // Calcular XP basado en respuesta correcta y tiempo
        val baseXP = if (isCorrect) 10 else 5
        val timeBonus = calculateTimeBonus(timeSpent, session.difficulty)
        val xpGained = baseXP + timeBonus
        
        // Actualizar sesión actual
        val updatedSession = session.copy(
            xpEarned = session.xpEarned + xpGained,
            accuracy = calculateAccuracy(session, isCorrect)
        )
        _currentSession.value = updatedSession
        
        // Actualizar base de datos
        updateUserXP(userId, xpGained)
        updateProgressInDatabase(userId, moduleId, lessonId, isCorrect)
    }
    
    /**
     * Completa una lección
     */
    suspend fun completeLesson(
        userId: Int,
        moduleId: Int,
        lessonId: Int
    ) {
        val session = _currentSession.value ?: return
        
        val updatedSession = session.copy(
            lessonsCompleted = session.lessonsCompleted + 1
        )
        _currentSession.value = updatedSession
        
        // Actualizar progreso en la base de datos
        updateModuleProgress(userId, moduleId)
    }
    
    /**
     * Termina la sesión actual
     */
    suspend fun endCurrentSession() {
        val session = _currentSession.value ?: return
        
        val endTime = System.currentTimeMillis()
        val duration = endTime - sessionStartTime
        
        val finalSession = session.copy(
            endTime = endTime,
            duration = duration
        )
        
        // Agregar a historial
        val currentHistory = _sessionHistory.value.toMutableList()
        currentHistory.add(finalSession)
        _sessionHistory.value = currentHistory
        
        // Actualizar estadísticas del usuario
        updateUserStatistics(finalSession)
        
        _currentSession.value = null
        isSessionActive = false
    }
    
    /**
     * Obtiene estadísticas de aprendizaje del usuario
     */
    suspend fun getUserLearningStats(userId: Int): LearningStats {
        val sessions = _sessionHistory.value.filter { it.userId == userId }
        val user = repository.getCurrentUser().first() ?: return LearningStats()
        
        return LearningStats(
            totalSessions = sessions.size,
            totalTimeSpent = sessions.sumOf { it.duration ?: 0 },
            totalXPEarned = sessions.sumOf { it.xpEarned },
            averageAccuracy = sessions.map { it.accuracy }.average().toFloat(),
            lessonsCompleted = sessions.sumOf { it.lessonsCompleted },
            averageSessionDuration = sessions.mapNotNull { it.duration }.average().toLong(),
            streakDays = user.dailyStreak,
            lastStudyDate = sessions.maxByOrNull { it.startTime }?.startTime
        )
    }
    
    /**
     * Simula una sesión de aprendizaje completa
     */
    suspend fun simulateLearningSession(
        userId: Int,
        moduleId: Int,
        numLessons: Int = 3
    ) {
        startLearningSession(userId, moduleId, 1)
        
        repeat(numLessons) { lessonIndex ->
            val lessonId = lessonIndex + 1
            
            // Simular tiempo de estudio por lección (30-120 segundos)
            val studyTime = (30 + (Math.random() * 90)).toLong() * 1000
            delay(studyTime / 10) // Acelerar para demo
            
            // Simular respuestas (80% de acierto)
            val isCorrect = Math.random() > 0.2
            updateProgress(userId, moduleId, lessonId, isCorrect, studyTime)
            
            // Completar lección
            completeLesson(userId, moduleId, lessonId)
            
            // Pausa entre lecciones
            delay(500)
        }
        
        endCurrentSession()
    }
    
    private fun calculateTimeBonus(timeSpent: Long, difficulty: LessonDifficulty): Int {
        val baseTime = when (difficulty) {
            LessonDifficulty.EASY -> 60000L      // 1 minuto
            LessonDifficulty.MEDIUM -> 90000L    // 1.5 minutos
            LessonDifficulty.HARD -> 120000L     // 2 minutos
        }
        
        return when {
            timeSpent < baseTime * 0.5 -> 5      // Muy rápido
            timeSpent < baseTime * 1.0 -> 3      // Rápido
            timeSpent < baseTime * 1.5 -> 1      // Normal
            else -> 0                             // Lento
        }
    }
    
    private fun calculateAccuracy(session: LearningSession, isCorrect: Boolean): Float {
        // Implementación simplificada - en la realidad necesitarías trackear todas las respuestas
        return if (isCorrect) 0.8f else 0.6f
    }
    
    private suspend fun updateUserXP(userId: Int, xpGained: Int) {
        val currentUser = repository.getCurrentUser().first()
        currentUser?.let { user ->
            repository.updateXP(user.totalXP + xpGained)
        }
    }
    
    private suspend fun updateProgressInDatabase(
        userId: Int,
        moduleId: Int,
        lessonId: Int,
        isCorrect: Boolean
    ) {
        // Aquí actualizarías el progreso específico de la lección
        // Por simplicidad, actualizamos el progreso del módulo
    }
    
    private suspend fun updateModuleProgress(userId: Int, moduleId: Int) {
        val currentProgress = repository.getProgressByModule(userId, moduleId).first()
        val lessonsCompleted = (currentProgress?.lessonsCompleted ?: 0) + 1
        
        // Calcular porcentaje (asumiendo 5 lecciones por módulo)
        val totalLessons = 5
        val percentage = (lessonsCompleted.toFloat() / totalLessons * 100).coerceAtMost(100f)
        val isCompleted = lessonsCompleted >= totalLessons
        
        repository.updateModuleProgress(
            userId = userId,
            moduleId = moduleId,
            lessonsCompleted = lessonsCompleted,
            percentage = percentage,
            isCompleted = isCompleted
        )
    }
    
    private suspend fun updateUserStatistics(session: LearningSession) {
        val user = repository.getCurrentUser().first() ?: return
        
        // Actualizar racha diaria
        val today = System.currentTimeMillis()
        val lastStudyDate = session.startTime
        
        // Lógica simple para racha (en la realidad sería más compleja)
        val currentStreak = if (today - lastStudyDate < 86400000) { // 24 horas
            user.dailyStreak + 1
        } else {
            1
        }
        
        repository.updateDailyStreak(currentStreak)
    }
    
    private fun generateSessionId(): String {
        return "session_${System.currentTimeMillis()}_${(Math.random() * 1000).toInt()}"
    }
}

/**
 * Representa una sesión de aprendizaje
 */
data class LearningSession(
    val id: String,
    val userId: Int,
    val moduleId: Int,
    val lessonId: Int,
    val startTime: Long,
    val endTime: Long?,
    val duration: Long? = null,
    val xpEarned: Int,
    val lessonsCompleted: Int,
    val accuracy: Float,
    val difficulty: LessonDifficulty
)

/**
 * Dificultad de las lecciones
 */
enum class LessonDifficulty {
    EASY, MEDIUM, HARD
}

/**
 * Estadísticas de aprendizaje del usuario
 */
data class LearningStats(
    val totalSessions: Int = 0,
    val totalTimeSpent: Long = 0,
    val totalXPEarned: Int = 0,
    val averageAccuracy: Float = 0f,
    val lessonsCompleted: Int = 0,
    val averageSessionDuration: Long = 0,
    val streakDays: Int = 0,
    val lastStudyDate: Long? = null
)
