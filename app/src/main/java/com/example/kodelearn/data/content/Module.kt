package com.example.kodelearn.data.content

/**
 * Clase que representa un módulo de aprendizaje con su contenido
 */
data class Module(
    val id: Int,
    val title: String,
    val description: String,
    val difficulty: String,
    val estimatedTime: String,
    val isUnlocked: Boolean,
    val requiredModuleId: Int?,
    val lessons: List<LessonContent>,
    val totalLessons: Int,
    val totalXP: Int,
    val isCompleted: Boolean
)
