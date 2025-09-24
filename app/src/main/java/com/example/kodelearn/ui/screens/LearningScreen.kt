package com.example.kodelearn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kodelearn.data.repository.KodeLearnRepository
import com.example.kodelearn.data.repository.ModuleWithProgress
import com.example.kodelearn.ui.components.LearningTopBar
import com.example.kodelearn.ui.components.ModuleItem
import com.example.kodelearn.ui.components.ModulePath
import com.example.kodelearn.ui.components.ModulePopup
import com.example.kodelearn.ui.theme.KodeLearnTheme
import com.example.kodelearn.ui.viewmodel.LearningViewModel

@Composable
fun LearningScreen(
    repository: KodeLearnRepository,
    onNavigateToLesson: (ModuleWithProgress) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: LearningViewModel = viewModel(factory = LearningViewModel.factory(repository))
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedModule by remember { mutableStateOf<ModuleWithProgress?>(null) }
    var showPopup by remember { mutableStateOf(false) }
    
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar with Hearts, Coins, and Streak
            uiState.user?.let { user ->
                LearningTopBar(
                    hearts = user.hearts,
                    coins = user.coins,
                    streak = user.dailyStreak
                )
            }
            
            // Course Content with Scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Course Header
                CourseHeader(
                    courseName = uiState.currentCourse?.name ?: "",
                    courseDescription = uiState.currentCourse?.description ?: "",
                    modifier = Modifier.padding(16.dp)
                )
                
                // Progress Overview
                uiState.currentCourse?.let { course ->
                    val completedModules = uiState.modulesWithProgress.count { it.progress?.isCompleted == true }
                    val totalModules = uiState.modulesWithProgress.size
                    val overallProgress = if (totalModules > 0) {
                        (completedModules.toFloat() / totalModules) * 100f
                    } else 0f
                    
                    ProgressOverview(
                        progress = overallProgress,
                        completedModules = completedModules,
                        totalModules = totalModules,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Learning Path Title
                Text(
                    text = "Tu camino de aprendizaje",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                // Module Path with Curved Design
                ModulePath(
                    modules = uiState.modulesWithProgress,
                    onModuleClick = { moduleWithProgress ->
                        selectedModule = moduleWithProgress
                        showPopup = true
                    }
                )
                
                Spacer(modifier = Modifier.height(100.dp)) // Space for bottom navigation
            }
        }
        
        // Module Popup
        ModulePopup(
            moduleWithProgress = selectedModule,
            isVisible = showPopup,
            onDismiss = {
                showPopup = false
                selectedModule = null
            },
            onStartModule = { moduleWithProgress ->
                onNavigateToLesson(moduleWithProgress)
            }
        )
    }
}

@Composable
private fun CourseHeader(
    courseName: String,
    courseDescription: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = courseName,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (courseDescription.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = courseDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ProgressOverview(
    progress: Float,
    completedModules: Int,
    totalModules: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso del curso",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "${progress.toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress Bar
            LinearProgressIndicator(
                progress = progress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$completedModules de $totalModules módulos completados",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview(showBackground = true, name = "Learning Screen")
@Composable
fun LearningScreenPreview() {
    KodeLearnTheme {
        LearningScreenContent()
    }
}

@Composable
private fun LearningScreenContent() {
    val sampleModules = getSampleModulesForPreview()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar with Hearts, Coins, and Streak
            LearningTopBar(
                hearts = 5,
                coins = 220,
                streak = 3
            )
            
            // Course Content with Scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Course Header
                CourseHeader(
                    courseName = "Programación Básica",
                    courseDescription = "Aprende los fundamentos de la programación desde cero. Domina conceptos esenciales como variables, estructuras de control, funciones y lógica de programación.",
                    modifier = Modifier.padding(16.dp)
                )
                
                // Progress Overview
                ProgressOverview(
                    progress = 8.3f,
                    completedModules = 1,
                    totalModules = 12,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Learning Path Title
                Text(
                    text = "Tu camino de aprendizaje",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                // Module Path with Curved Design
                ModulePath(
                    modules = sampleModules,
                    onModuleClick = { }
                )
                
                Spacer(modifier = Modifier.height(100.dp)) // Space for bottom navigation
            }
        }
    }
}

private fun getSampleModulesForPreview(): List<com.example.kodelearn.data.repository.ModuleWithProgress> {
    return listOf(
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 1,
                courseId = 1,
                name = "Introducción a la Sintaxis Básica",
                description = "Aprende los fundamentos de la sintaxis de programación, reglas básicas y estructura de código.",
                totalLessons = 3,
                order = 1,
                isLocked = false,
                xpReward = 40
            ),
            progress = com.example.kodelearn.data.database.entities.Progress(
                id = 1,
                userId = 1,
                moduleId = 1,
                lessonsCompleted = 1,
                progressPercentage = 33f,
                isCompleted = false
            )
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 2,
                courseId = 1,
                name = "Declaración de Variables y Tipos de Datos",
                description = "Domina cómo declarar variables y trabajar con diferentes tipos de datos en programación.",
                totalLessons = 4,
                order = 2,
                isLocked = true,
                xpReward = 50
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 3,
                courseId = 1,
                name = "Operadores",
                description = "Aprende a usar operadores aritméticos, lógicos y de comparación en tu código.",
                totalLessons = 3,
                order = 3,
                isLocked = true,
                xpReward = 45
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 4,
                courseId = 1,
                name = "Estructuras de Control",
                description = "Comprende las estructuras de control que dirigen el flujo de ejecución de tu programa.",
                totalLessons = 5,
                order = 4,
                isLocked = true,
                xpReward = 60
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 5,
                courseId = 1,
                name = "Condicionales (if/else)",
                description = "Domina las estructuras condicionales para tomar decisiones en tu código.",
                totalLessons = 4,
                order = 5,
                isLocked = true,
                xpReward = 50
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 6,
                courseId = 1,
                name = "Ciclos (for/while)",
                description = "Aprende a crear bucles y repeticiones para automatizar tareas repetitivas.",
                totalLessons = 4,
                order = 6,
                isLocked = true,
                xpReward = 50
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 7,
                courseId = 1,
                name = "Funciones y Procedimientos",
                description = "Crea funciones reutilizables para organizar y modularizar tu código.",
                totalLessons = 5,
                order = 7,
                isLocked = true,
                xpReward = 60
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 8,
                courseId = 1,
                name = "Modularidad y Reutilización",
                description = "Aprende principios de diseño para crear código modular y reutilizable.",
                totalLessons = 4,
                order = 8,
                isLocked = true,
                xpReward = 50
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 9,
                courseId = 1,
                name = "Estructuras de Datos",
                description = "Comprende las estructuras de datos fundamentales y cuándo usar cada una.",
                totalLessons = 5,
                order = 9,
                isLocked = true,
                xpReward = 60
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 10,
                courseId = 1,
                name = "Arreglos y Listas",
                description = "Domina el trabajo con colecciones de datos: arreglos, listas y sus operaciones.",
                totalLessons = 4,
                order = 10,
                isLocked = true,
                xpReward = 50
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 11,
                courseId = 1,
                name = "Entrada y Salida de Datos",
                description = "Aprende a manejar la entrada de datos del usuario y mostrar información.",
                totalLessons = 3,
                order = 11,
                isLocked = true,
                xpReward = 45
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 12,
                courseId = 1,
                name = "Lógica y Resolución de Problemas",
                description = "Desarrolla habilidades de pensamiento lógico y resolución de problemas de programación.",
                totalLessons = 6,
                order = 12,
                isLocked = true,
                xpReward = 70
            ),
            progress = null
        )
    )
}
