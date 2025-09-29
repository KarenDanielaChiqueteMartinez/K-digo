package com.example.kodelearn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kodelearn.ui.components.TopBar
import com.example.kodelearn.ui.components.StatCard
import com.example.kodelearn.ui.viewmodel.RecommendationViewModel

/**
 * Pantalla que muestra recomendaciones y análisis de usuarios basados en KNN
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecommendationViewModel = viewModel()
) {
    val isInitialized by viewModel.isInitialized.collectAsState()
    val similarUsers by viewModel.similarUsers.collectAsState()
    val userClassification by viewModel.userClassification.collectAsState()
    val performancePrediction by viewModel.performancePrediction.collectAsState()
    val similarityStats by viewModel.similarityStats.collectAsState()
    val learningPatterns by viewModel.learningPatterns.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        if (isInitialized) {
            viewModel.refreshRecommendations()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopBar(
            title = "Recomendaciones",
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
        } else if (isInitialized) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Clasificación del usuario
                item {
                    UserClassificationCard(
                        classification = userClassification,
                        onRefresh = { viewModel.classifyCurrentUser() }
                    )
                }

                // Predicción de rendimiento
                performancePrediction?.let { prediction ->
                    item {
                        PerformancePredictionCard(
                            prediction = prediction,
                            onRefresh = { viewModel.predictUserPerformance() }
                        )
                    }
                }

                // Estadísticas de similitud
                similarityStats?.let { stats ->
                    item {
                        SimilarityStatsCard(
                            stats = stats,
                            onRefresh = { viewModel.loadSimilarityStats() }
                        )
                    }
                }

                // Patrones de aprendizaje
                learningPatterns?.let { patterns ->
                    item {
                        LearningPatternsCard(
                            patterns = patterns,
                            onRefresh = { viewModel.analyzeLearningPatterns() }
                        )
                    }
                }

                // Usuarios similares
                item {
                    Text(
                        text = "Usuarios Similares",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(similarUsers) { user ->
                    SimilarUserCard(
                        user = user,
                        onUserClick = { /* Navegar al perfil del usuario */ }
                    )
                }

                // Botón de validación del modelo
                item {
                    Button(
                        onClick = { viewModel.validateModel() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Validar Modelo")
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Inicializando recomendaciones...")
            }
        }
    }
}

@Composable
fun UserClassificationCard(
    classification: String,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(
                when (classification.lowercase()) {
                    "beginner" -> "#4CAF50"
                    "intermediate" -> "#2196F3"
                    "advanced" -> "#FF9800"
                    "expert" -> "#9C27B0"
                    else -> "#757575"
                }
            ))
        )
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
                    text = "Tu Clasificación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = onRefresh) {
                    Text("🔄", fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = classification.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (classification.lowercase()) {
                    "beginner" -> "Usuario principiante - Comienza con conceptos básicos"
                    "intermediate" -> "Usuario intermedio - Domina conceptos fundamentales"
                    "advanced" -> "Usuario avanzado - Listo para conceptos complejos"
                    "expert" -> "Usuario experto - Puede enseñar a otros"
                    else -> "Clasificación no disponible"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun PerformancePredictionCard(
    prediction: com.example.kodelearn.data.ml.PerformancePrediction,
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
                    text = "Predicción de Rendimiento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onRefresh) {
                    Text("🔄", fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    title = "Precisión",
                    value = "${(prediction.predictedAccuracy * 100).toInt()}%",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatCard(
                    title = "Velocidad",
                    value = "${prediction.predictedSpeed.toInt()} lec/día",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatCard(
                    title = "Consistencia",
                    value = "${(prediction.predictedConsistency * 100).toInt()}%",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Confianza: ${(prediction.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SimilarityStatsCard(
    stats: com.example.kodelearn.data.ml.SimilarityStats,
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
                    text = "Estadísticas de Similitud",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onRefresh) {
                    Text("🔄", fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    title = "General",
                    value = "${(stats.overallSimilarity * 100).toInt()}%",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatCard(
                    title = "Promedio",
                    value = "${(stats.averageSimilarity * 100).toInt()}%",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatCard(
                    title = "Máxima",
                    value = "${(stats.maxSimilarity * 100).toInt()}%",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun LearningPatternsCard(
    patterns: com.example.kodelearn.data.ml.LearningPatternAnalysis,
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
                    text = "Análisis de Patrones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onRefresh) {
                    Text("🔄", fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (patterns.commonPatterns.isNotEmpty()) {
                Text(
                    text = "Patrones Comunes:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                patterns.commonPatterns.forEach { pattern ->
                    Text(
                        text = "• $pattern",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            
            if (patterns.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Recomendaciones:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                patterns.recommendations.forEach { recommendation ->
                    Text(
                        text = "• $recommendation",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun SimilarUserCard(
    user: com.example.kodelearn.data.ml.UserRecommendation,
    onUserClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onUserClick
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
                    text = "Usuario #${user.userId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(user.similarity * 100).toInt()}% similar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (user.sharedCharacteristics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Características compartidas:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                user.sharedCharacteristics.forEach { characteristic ->
                    Text(
                        text = "• $characteristic",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
