package com.example.kodelearn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kodelearn.ui.components.TopBar
import com.example.kodelearn.ui.viewmodel.LearningSessionViewModel

/**
 * Pantalla para probar el sistema de aprendizaje dinámico
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestLearningScreen(
    onNavigateBack: () -> Unit,
    viewModel: LearningSessionViewModel = viewModel()
) {
    val currentSession by viewModel.currentSession.collectAsState()
    val sessionHistory by viewModel.sessionHistory.collectAsState()
    val isLearningActive by viewModel.isLearningActive.collectAsState()
    val currentStats by viewModel.currentStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Estado local para controles
    var selectedModule by remember { mutableStateOf(1) }
    var selectedLessons by remember { mutableStateOf(3) }
    var userId by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopBar(
            title = "Prueba de Aprendizaje",
            onBackClick = onNavigateBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = error!!,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Controles de configuración
                item {
                    ConfigurationCard(
                        selectedModule = selectedModule,
                        selectedLessons = selectedLessons,
                        userId = userId,
                        onModuleChange = { selectedModule = it },
                        onLessonsChange = { selectedLessons = it },
                        onUserIdChange = { userId = it }
                    )
                }

                // Controles de sesión
                item {
                    SessionControlsCard(
                        isLearningActive = isLearningActive,
                        onStartSession = {
                            viewModel.startLearningSession(userId, selectedModule, 1)
                        },
                        onSimulateSession = {
                            viewModel.simulateLearningSession(userId, selectedModule, selectedLessons)
                        },
                        onEndSession = {
                            viewModel.endCurrentSession(userId)
                        },
                        onGenerateTestData = {
                            viewModel.generateTestData(userId, 5)
                        }
                    )
                }

                // Sesión actual
                currentSession?.let { session ->
                    item {
                        CurrentSessionCard(
                            session = session,
                            onUpdateProgress = { isCorrect, timeSpent ->
                                viewModel.updateProgress(userId, selectedModule, session.lessonId, isCorrect, timeSpent)
                            },
                            onCompleteLesson = {
                                viewModel.completeLesson(userId, selectedModule, session.lessonId)
                            }
                        )
                    }
                }

                // Estadísticas del usuario
                currentStats?.let { stats ->
                    item {
                        UserStatsCard(
                            stats = stats,
                            onRefresh = { viewModel.loadUserStats(userId) }
                        )
                    }
                }

                // Historial de sesiones
                if (sessionHistory.isNotEmpty()) {
                    item {
                        Text(
                            text = "Historial de Sesiones",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    items(sessionHistory.takeLast(10)) { session -> // Mostrar últimas 10 sesiones
                        SessionHistoryCard(
                            session = session,
                            formatDuration = { viewModel.formatDuration(it) },
                            formatDate = { viewModel.formatDate(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfigurationCard(
    selectedModule: Int,
    selectedLessons: Int,
    userId: Int,
    onModuleChange: (Int) -> Unit,
    onLessonsChange: (Int) -> Unit,
    onUserIdChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Configuración de Prueba",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column {
                    Text("Usuario ID:")
                    TextField(
                        value = userId.toString(),
                        onValueChange = { onUserIdChange(it.toIntOrNull() ?: 1) },
                        modifier = Modifier.width(80.dp)
                    )
                }
                
                Column {
                    Text("Módulo:")
                    TextField(
                        value = selectedModule.toString(),
                        onValueChange = { onModuleChange(it.toIntOrNull() ?: 1) },
                        modifier = Modifier.width(80.dp)
                    )
                }
                
                Column {
                    Text("Lecciones:")
                    TextField(
                        value = selectedLessons.toString(),
                        onValueChange = { onLessonsChange(it.toIntOrNull() ?: 3) },
                        modifier = Modifier.width(80.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SessionControlsCard(
    isLearningActive: Boolean,
    onStartSession: () -> Unit,
    onSimulateSession: () -> Unit,
    onEndSession: () -> Unit,
    onGenerateTestData: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Controles de Sesión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onStartSession,
                    enabled = !isLearningActive
                ) {
                    Text("Iniciar Sesión")
                }
                
                Button(
                    onClick = onSimulateSession,
                    enabled = !isLearningActive
                ) {
                    Text("Simular Sesión")
                }
                
                Button(
                    onClick = onEndSession,
                    enabled = isLearningActive,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Terminar")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onGenerateTestData,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Generar Datos de Prueba (5 sesiones)")
            }
        }
    }
}

@Composable
fun CurrentSessionCard(
    session: com.example.kodelearn.data.learning.LearningSession,
    onUpdateProgress: (Boolean, Long) -> Unit,
    onCompleteLesson: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Sesión Activa",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column {
                    Text("Módulo: ${session.moduleId}")
                    Text("Lección: ${session.lessonId}")
                }
                
                Column {
                    Text("XP: ${session.xpEarned}")
                    Text("Completadas: ${session.lessonsCompleted}")
                }
                
                Column {
                    Text("Precisión: ${(session.accuracy * 100).toInt()}%")
                    Text("Dificultad: ${session.difficulty.name}")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onUpdateProgress(true, 30000) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Correcto")
                }
                
                Button(
                    onClick = { onUpdateProgress(false, 60000) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("Incorrecto")
                }
                
                Button(
                    onClick = onCompleteLesson,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    )
                ) {
                    Text("Completar")
                }
            }
        }
    }
}

@Composable
fun UserStatsCard(
    stats: com.example.kodelearn.data.learning.LearningStats,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estadísticas del Usuario",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = onRefresh) {
                    Text("Actualizar")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column {
                    Text("Sesiones: ${stats.totalSessions}")
                    Text("Tiempo: ${(stats.totalTimeSpent / 1000 / 60).toInt()}m")
                }
                
                Column {
                    Text("XP Total: ${stats.totalXPEarned}")
                    Text("Precisión: ${(stats.averageAccuracy * 100).toInt()}%")
                }
                
                Column {
                    Text("Lecciones: ${stats.lessonsCompleted}")
                    Text("Racha: ${stats.streakDays} días")
                }
            }
        }
    }
}

@Composable
fun SessionHistoryCard(
    session: com.example.kodelearn.data.learning.LearningSession,
    formatDuration: (Long) -> String,
    formatDate: (Long) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Módulo ${session.moduleId} - Lección ${session.lessonId}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatDate(session.startTime),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("XP: ${session.xpEarned}")
                Text("Lecciones: ${session.lessonsCompleted}")
                Text("Precisión: ${(session.accuracy * 100).toInt()}%")
                session.duration?.let { 
                    Text("Duración: ${formatDuration(it)}")
                }
            }
        }
    }
}
