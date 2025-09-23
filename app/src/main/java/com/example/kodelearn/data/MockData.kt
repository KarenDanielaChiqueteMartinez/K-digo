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
            name = "Programación",
            description = "Aprende los fundamentos de programación desde cero hasta convertirte en un desarrollador competente.",
            iconUrl = "",
            totalModules = 11,
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
        // Programming Modules - Structured Learning Path
        Module(
            id = 1,
            courseId = 1,
            name = "Introducción a la Sintaxis Básica",
            description = "Aprende los fundamentos de la sintaxis de programación: indentación, comentarios y estructura básica.",
            totalLessons = 4,
            order = 1,
            isLocked = false,
            xpReward = 40
        ),
        Module(
            id = 2,
            courseId = 1,
            name = "Declaración de Variables y Tipos de Datos",
            description = "Domina cómo declarar variables y trabajar con diferentes tipos de datos en programación.",
            totalLessons = 5,
            order = 2,
            isLocked = true,
            xpReward = 50
        ),
        Module(
            id = 3,
            courseId = 1,
            name = "Operadores",
            description = "Aprende a usar operadores aritméticos, de comparación y lógicos en programación.",
            totalLessons = 4,
            order = 3,
            isLocked = true,
            xpReward = 40
        ),
        Module(
            id = 4,
            courseId = 1,
            name = "Estructuras de Control",
            description = "Comprende el flujo de control básico en programación.",
            totalLessons = 3,
            order = 4,
            isLocked = true,
            xpReward = 30
        ),
        Module(
            id = 5,
            courseId = 1,
            name = "Condicionales (if/else)",
            description = "Aprende a tomar decisiones en tu código usando condicionales if, elif y else.",
            totalLessons = 5,
            order = 5,
            isLocked = true,
            xpReward = 50
        ),
        Module(
            id = 6,
            courseId = 1,
            name = "Ciclos (for/while)",
            description = "Domina los bucles for y while para repetir código de manera eficiente.",
            totalLessons = 6,
            order = 6,
            isLocked = true,
            xpReward = 60
        ),
        Module(
            id = 7,
            courseId = 1,
            name = "Funciones y Procedimientos",
            description = "Aprende a crear y usar funciones para organizar y reutilizar tu código.",
            totalLessons = 7,
            order = 7,
            isLocked = true,
            xpReward = 70
        ),
        Module(
            id = 8,
            courseId = 1,
            name = "Modularidad y Reutilización",
            description = "Organiza tu código en módulos y aprende las mejores prácticas de reutilización.",
            totalLessons = 4,
            order = 8,
            isLocked = true,
            xpReward = 40
        ),
        Module(
            id = 9,
            courseId = 1,
            name = "Estructuras de Datos",
            description = "Explora las estructuras de datos fundamentales: listas, tuplas, diccionarios y conjuntos.",
            totalLessons = 8,
            order = 9,
            isLocked = true,
            xpReward = 80
        ),
        Module(
            id = 10,
            courseId = 1,
            name = "Arreglos y Listas",
            description = "Profundiza en el manejo de listas y arreglos para almacenar y manipular datos.",
            totalLessons = 6,
            order = 10,
            isLocked = true,
            xpReward = 60
        ),
        Module(
            id = 11,
            courseId = 1,
            name = "Entrada y Salida de Datos",
            description = "Aprende a recibir datos del usuario y mostrar información en pantalla.",
            totalLessons = 5,
            order = 11,
            isLocked = true,
            xpReward = 50
        ),
        
        // JavaScript Modules (locked course)
        Module(
            id = 12,
            courseId = 2,
            name = "JavaScript Básico",
            description = "Fundamentos de JavaScript para desarrollo web.",
            totalLessons = 5,
            order = 1,
            isLocked = true,
            xpReward = 50
        ),
        
        // Kotlin Modules (locked course)
        Module(
            id = 13,
            courseId = 3,
            name = "Kotlin Básico",
            description = "Sintaxis básica de Kotlin para desarrollo Android.",
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
