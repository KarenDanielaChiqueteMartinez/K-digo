package com.example.kodelearn.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kodelearn.data.content.Question
import com.example.kodelearn.data.content.LessonContent
import com.example.kodelearn.data.learning.LessonService
import com.example.kodelearn.data.learning.LearningSessionService
import com.example.kodelearn.data.repository.KodeLearnRepository
import com.example.kodelearn.ui.components.TopBar

/**
 * Pantalla que muestra una lección específica con contenido y preguntas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    moduleId: Int,
    lessonId: Int,
    onNavigateBack: () -> Unit,
    onLessonCompleted: () -> Unit
) {
    // Nota: En una implementación real, estos DAOs se inyectarían via Dagger/Hilt
    // Por ahora creamos servicios mock para evitar errores de compilación
    val repository: KodeLearnRepository? = null
    val learningSessionService: LearningSessionService? = null
    val lessonService: LessonService? = null
    
    val lessonContent = lessonService?.getLessonContent(moduleId, lessonId)
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf(-1) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var xpGained by remember { mutableStateOf(0) }
    var explanation by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var isLessonCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(lessonId) {
        // lessonService.startLesson(1, moduleId, lessonId) // Comentado por dependencias
        startTime = System.currentTimeMillis()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopBar(
            title = lessonContent?.title ?: "Lección",
            onBackClick = onNavigateBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (lessonContent != null) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Información de la lección
                item {
                    LessonInfoCard(
                        lesson = lessonContent,
                        currentQuestion = currentQuestionIndex + 1,
                        totalQuestions = lessonContent.questions.size
                    )
                }

                // Contenido de la lección
                item {
                    LessonContentCard(content = lessonContent.content)
                }

                // Preguntas
                if (lessonContent.questions.isNotEmpty() && currentQuestionIndex < lessonContent.questions.size) {
                    val currentQuestion = lessonContent.questions[currentQuestionIndex]
                    
                    item {
                        QuestionCard(
                            question = currentQuestion,
                            selectedAnswer = selectedAnswer,
                            onAnswerSelected = { selectedAnswer = it },
                            showResult = showResult,
                            isCorrect = isCorrect,
                            explanation = explanation,
                            xpGained = xpGained,
                            onNextQuestion = {
                                if (currentQuestionIndex < lessonContent.questions.size - 1) {
                                    currentQuestionIndex++
                                    selectedAnswer = -1
                                    showResult = false
                                    startTime = System.currentTimeMillis()
                                } else {
                                    // Completar lección
                                    isLessonCompleted = true
                                    // lessonService.completeLesson(1, moduleId, lessonId) // Comentado por dependencias
                                }
                            },
                            onAnswerSubmit = {
                                val timeSpent = System.currentTimeMillis() - startTime
                                // Simular resultado para demo
                                isCorrect = selectedAnswer == currentQuestion.correctAnswer
                                explanation = currentQuestion.explanation
                                xpGained = if (isCorrect) 20 else 5
                                showResult = true
                                
                                // lessonService.processAnswer(
                                //     1, moduleId, lessonId, 
                                //     currentQuestion.id, selectedAnswer, timeSpent
                                // ) // Comentado por dependencias
                            }
                        )
                    }
                }

                // Lección completada
                if (isLessonCompleted) {
                    item {
                        LessonCompletedCard(
                            lesson = lessonContent,
                            onContinue = onLessonCompleted
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Lección no encontrada")
            }
        }
    }
}

@Composable
fun LessonInfoCard(
    lesson: LessonContent,
    currentQuestion: Int,
    totalQuestions: Int
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
                text = lesson.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = lesson.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Pregunta $currentQuestion de $totalQuestions")
                Text("Tiempo estimado: ${lesson.timeEstimate} min")
                Text("XP: ${lesson.xpReward}")
            }
        }
    }
}

@Composable
fun LessonContentCard(content: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Contenido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun QuestionCard(
    question: Question,
    selectedAnswer: Int,
    onAnswerSelected: (Int) -> Unit,
    showResult: Boolean,
    isCorrect: Boolean,
    explanation: String,
    xpGained: Int,
    onNextQuestion: () -> Unit,
    onAnswerSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (showResult) {
                if (isCorrect) Color(0xFF4CAF50).copy(alpha = 0.1f)
                else Color(0xFFF44336).copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pregunta ${question.id}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = question.question,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Opciones de respuesta
            question.options.forEachIndexed { index, option ->
                val isSelected = selectedAnswer == index
                val isCorrectAnswer = showResult && index == question.correctAnswer
                val isWrongAnswer = showResult && isSelected && !isCorrect
                
                val cardColor = when {
                    isCorrectAnswer -> Color(0xFF4CAF50).copy(alpha = 0.3f)
                    isWrongAnswer -> Color(0xFFF44336).copy(alpha = 0.3f)
                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.surface
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { if (!showResult) onAnswerSelected(index) },
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Text(
                        text = option,
                        modifier = Modifier.padding(16.dp),
                        color = when {
                            isCorrectAnswer -> Color(0xFF2E7D32)
                            isWrongAnswer -> Color(0xFFC62828)
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Resultado y explicación
            if (showResult) {
                Column {
                    Text(
                        text = if (isCorrect) "¡Correcto! +$xpGained XP" else "Incorrecto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = explanation,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onNextQuestion,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Siguiente")
                    }
                }
            } else {
                Button(
                    onClick = onAnswerSubmit,
                    enabled = selectedAnswer != -1,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Responder")
                }
            }
        }
    }
}

@Composable
fun LessonCompletedCard(
    lesson: LessonContent,
    onContinue: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎉 ¡Lección Completada!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Has completado: ${lesson.title}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "XP ganado: ${lesson.xpReward}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text("Continuar")
            }
        }
    }
}