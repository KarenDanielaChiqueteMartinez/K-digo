package com.example.kodelearn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kodelearn.data.repository.KodeLearnRepository
import com.example.kodelearn.ui.components.LearningTopBar
import com.example.kodelearn.ui.components.ModuleItem
import com.example.kodelearn.ui.viewmodel.LearningViewModel

@Composable
fun LearningScreen(
    repository: KodeLearnRepository,
    modifier: Modifier = Modifier,
    viewModel: LearningViewModel = viewModel(factory = LearningViewModel.factory(repository))
) {
    val uiState by viewModel.uiState.collectAsState()
    
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar with Hearts, Coins, and Streak
        uiState.user?.let { user ->
            LearningTopBar(
                hearts = user.hearts,
                coins = user.coins,
                streak = user.dailyStreak
            )
        }
        
        // Course Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Course Header
                CourseHeader(
                    courseName = uiState.currentCourse?.name ?: "",
                    courseDescription = uiState.currentCourse?.description ?: ""
                )
            }
            
            item {
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
                        totalModules = totalModules
                    )
                }
            }
            
            item {
                Text(
                    text = "Módulos del curso",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Modules List
            items(uiState.modulesWithProgress) { moduleWithProgress ->
                ModuleItem(
                    module = moduleWithProgress.module,
                    progress = moduleWithProgress.progress,
                    onClick = {
                        viewModel.startModule(moduleWithProgress.module.id)
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for bottom navigation
            }
        }
    }
}

@Composable
private fun CourseHeader(
    courseName: String,
    courseDescription: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
    totalModules: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
