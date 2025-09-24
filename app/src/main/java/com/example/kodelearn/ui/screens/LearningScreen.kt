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
                    courseName = "JavaScript",
                    courseDescription = "Aprende los fundamentos de JavaScript desde cero hasta convertirte en un desarrollador competente.",
                    modifier = Modifier.padding(16.dp)
                )
                
                // Progress Overview
                ProgressOverview(
                    progress = 25f,
                    completedModules = 1,
                    totalModules = 4,
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
                name = "Conceptos básicos de JavaScript",
                description = "Aprende los fundamentos: variables, tipos de datos, operadores y estructuras básicas.",
                totalLessons = 4,
                order = 1,
                isLocked = false,
                xpReward = 50
            ),
            progress = com.example.kodelearn.data.database.entities.Progress(
                id = 1,
                userId = 1,
                moduleId = 1,
                lessonsCompleted = 1,
                progressPercentage = 25f,
                isCompleted = false
            )
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 2,
                courseId = 1,
                name = "Estructuras de Control",
                description = "Domina las estructuras condicionales y bucles para controlar el flujo de tu código.",
                totalLessons = 5,
                order = 2,
                isLocked = true,
                xpReward = 60
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 3,
                courseId = 1,
                name = "Funciones y Métodos",
                description = "Crea funciones reutilizables y aprende sobre el scope y closures en JavaScript.",
                totalLessons = 6,
                order = 3,
                isLocked = true,
                xpReward = 70
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 4,
                courseId = 1,
                name = "Objetos y Arrays",
                description = "Trabaja con estructuras de datos complejas y manipula objetos y arrays.",
                totalLessons = 7,
                order = 4,
                isLocked = true,
                xpReward = 80
            ),
            progress = null
        ),
        com.example.kodelearn.data.repository.ModuleWithProgress(
            module = com.example.kodelearn.data.database.entities.Module(
                id = 5,
                courseId = 1,
                name = "DOM y Eventos",
                description = "Interactúa con el DOM y maneja eventos para crear aplicaciones dinámicas.",
                totalLessons = 8,
                order = 5,
                isLocked = true,
                xpReward = 90
            ),
            progress = null
        )
    )
}
