package com.example.kodelearn.data

import com.example.kodelearn.data.database.entities.*

object MockData {
    
    fun getDefaultUser() = User(
        id = 1,
        name = "Estudiante",
        biography = "",
        dailyStreak = 0,
        totalXP = 130,
        league = "Wooden",
        avatarUrl = "",
        hearts = 5,
        coins = 220
    )
    
    fun getDefaultCourses() = listOf(
        Course(
            id = 1,
            name = "Python",
            description = "Aprende los fundamentos de Python desde cero hasta convertirte en un desarrollador competente.",
            iconUrl = "",
            totalModules = 3,
            isLocked = false
        ),
        Course(
            id = 2,
            name = "JavaScript",
            description = "Domina JavaScript para desarrollo web moderno.",
            iconUrl = "",
            totalModules = 4,
            isLocked = true
        ),
        Course(
            id = 3,
            name = "Kotlin",
            description = "Desarrollo Android nativo con Kotlin.",
            iconUrl = "",
            totalModules = 5,
            isLocked = true
        )
    )
    
    fun getDefaultModules() = listOf(
        // Python Modules
        Module(
            id = 1,
            courseId = 1,
            name = "Python Básico",
            description = "Fundamentos de Python: variables, tipos de datos y operadores básicos.",
            totalLessons = 5,
            order = 1,
            isLocked = false,
            xpReward = 50
        ),
        Module(
            id = 2,
            courseId = 1,
            name = "Estructuras de Control",
            description = "If/else, bucles for y while, manejo de flujo de control.",
            totalLessons = 4,
            order = 2,
            isLocked = true,
            xpReward = 40
        ),
        Module(
            id = 3,
            courseId = 1,
            name = "Funciones y Métodos",
            description = "Definición de funciones, parámetros, return y scope.",
            totalLessons = 6,
            order = 3,
            isLocked = true,
            xpReward = 60
        ),
        
        // JavaScript Modules (locked course)
        Module(
            id = 4,
            courseId = 2,
            name = "JavaScript Básico",
            description = "Variables, tipos de datos y operadores en JavaScript.",
            totalLessons = 5,
            order = 1,
            isLocked = true,
            xpReward = 50
        ),
        Module(
            id = 5,
            courseId = 2,
            name = "DOM Manipulation",
            description = "Interacción con elementos HTML desde JavaScript.",
            totalLessons = 7,
            order = 2,
            isLocked = true,
            xpReward = 70
        ),
        
        // Kotlin Modules (locked course)
        Module(
            id = 6,
            courseId = 3,
            name = "Kotlin Básico",
            description = "Sintaxis básica de Kotlin para Android.",
            totalLessons = 6,
            order = 1,
            isLocked = true,
            xpReward = 60
        )
    )
    
    fun getDefaultProgress() = listOf(
        Progress(
            id = 1,
            userId = 1,
            moduleId = 1,
            lessonsCompleted = 2,
            progressPercentage = 40f,
            isCompleted = false,
            lastAccessedAt = System.currentTimeMillis()
        )
    )
}
