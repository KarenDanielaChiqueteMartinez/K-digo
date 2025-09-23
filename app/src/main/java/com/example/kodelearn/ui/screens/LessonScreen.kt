package com.example.kodelearn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kodelearn.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    moduleId: Int = 1,
    moduleTitle: String = "Introducción a la Sintaxis Básica",
    lessonNumber: Int = 1,
    totalLessons: Int = 4,
    onNavigateBack: () -> Unit = {},
    onLessonComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentExercise by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }
    
    val exercises = remember {
        getExercisesForModule(moduleId)
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = "$moduleTitle - Lección $lessonNumber",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
        
        // Progress Bar
        LinearProgressIndicator(
            progress = (currentExercise + 1).toFloat() / exercises.size,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = PrimaryGreen,
            trackColor = MaterialTheme.colorScheme.outline
        )
        
        // Exercise Counter
        Text(
            text = "${currentExercise + 1} de ${exercises.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // Main Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentExercise < exercises.size) {
                item {
                    ExerciseCard(
                        exercise = exercises[currentExercise],
                        onAnswerSelected = { isCorrect ->
                            if (isCorrect) {
                                score++
                            }
                            // Move to next exercise after a delay
                            if (currentExercise < exercises.size - 1) {
                                // Show explanation first
                            } else {
                                isCompleted = true
                            }
                        }
                    )
                }
            } else {
                item {
                    LessonCompleteCard(
                        score = score,
                        totalExercises = exercises.size,
                        onContinue = onLessonComplete
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseData,
    onAnswerSelected: (Boolean) -> Unit
) {
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showExplanation by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Question
            Text(
                text = exercise.question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            when (exercise.type) {
                ExerciseType.MULTIPLE_CHOICE -> {
                    exercise.options?.forEachIndexed { index, option ->
                        OptionButton(
                            text = option,
                            isSelected = selectedAnswer == index,
                            isCorrect = showExplanation && index == exercise.correctAnswer,
                            isWrong = showExplanation && selectedAnswer == index && index != exercise.correctAnswer,
                            onClick = {
                                if (!showExplanation) {
                                    selectedAnswer = index
                                    showExplanation = true
                                    onAnswerSelected(index == exercise.correctAnswer)
                                }
                            }
                        )
                    }
                }
                ExerciseType.CODE_COMPLETION -> {
                    CodeCompletionExercise(
                        code = exercise.code ?: "",
                        onAnswerSubmitted = { answer ->
                            showExplanation = true
                            onAnswerSelected(answer.equals(exercise.correctAnswer.toString(), ignoreCase = true))
                        }
                    )
                }
                ExerciseType.TRUE_FALSE -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OptionButton(
                            text = "Verdadero",
                            isSelected = selectedAnswer == 0,
                            isCorrect = showExplanation && exercise.correctAnswer == true,
                            isWrong = showExplanation && selectedAnswer == 0 && exercise.correctAnswer == false,
                            onClick = {
                                if (!showExplanation) {
                                    selectedAnswer = 0
                                    showExplanation = true
                                    onAnswerSelected(exercise.correctAnswer == true)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        OptionButton(
                            text = "Falso",
                            isSelected = selectedAnswer == 1,
                            isCorrect = showExplanation && exercise.correctAnswer == false,
                            isWrong = showExplanation && selectedAnswer == 1 && exercise.correctAnswer == true,
                            onClick = {
                                if (!showExplanation) {
                                    selectedAnswer = 1
                                    showExplanation = true
                                    onAnswerSelected(exercise.correctAnswer == false)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Explanation
            if (showExplanation) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedAnswer == exercise.correctAnswer) 
                            SuccessColor.copy(alpha = 0.1f) 
                        else ErrorColor.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (selectedAnswer == exercise.correctAnswer) 
                                    Icons.Default.CheckCircle 
                                else Icons.Default.Cancel,
                                contentDescription = if (selectedAnswer == exercise.correctAnswer) "Correcto" else "Incorrecto",
                                tint = if (selectedAnswer == exercise.correctAnswer) SuccessColor else ErrorColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (selectedAnswer == exercise.correctAnswer) "¡Correcto!" else "Incorrecto",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedAnswer == exercise.correctAnswer) SuccessColor else ErrorColor
                            )
                        }
                        Text(
                            text = exercise.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionButton(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isCorrect -> SuccessColor.copy(alpha = 0.2f)
        isWrong -> ErrorColor.copy(alpha = 0.2f)
        isSelected -> PrimaryGreen.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = when {
        isCorrect -> SuccessColor
        isWrong -> ErrorColor
        isSelected -> PrimaryGreen
        else -> MaterialTheme.colorScheme.outline
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CodeCompletionExercise(
    code: String,
    onAnswerSubmitted: (String) -> Unit
) {
    var userAnswer by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = code.replace("___", "_____"),
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = userAnswer,
                onValueChange = { userAnswer = it },
                placeholder = { Text("Tu respuesta aquí") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            
            Button(
                onClick = { onAnswerSubmitted(userAnswer) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                )
            ) {
                Text("Verificar")
            }
        }
    }
}

@Composable
private fun LessonCompleteCard(
    score: Int,
    totalExercises: Int,
    onContinue: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SuccessColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Lección completada",
                tint = SuccessColor,
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                text = "¡Lección Completada!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = SuccessColor
            )
            
            Text(
                text = "Puntuación: $score/$totalExercises",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                )
            ) {
                Text("Continuar")
            }
        }
    }
}

// Data Classes
data class ExerciseData(
    val type: ExerciseType,
    val question: String,
    val options: List<String>? = null,
    val code: String? = null,
    val correctAnswer: Any, // Int for multiple choice, String for code completion, Boolean for true/false
    val explanation: String
)

enum class ExerciseType {
    MULTIPLE_CHOICE,
    CODE_COMPLETION,
    TRUE_FALSE
}

// Function to get exercises based on module ID
private fun getExercisesForModule(moduleId: Int): List<ExerciseData> {
    return when (moduleId) {
        1 -> listOf( // Introducción a la Sintaxis Básica
            ExerciseData(
                type = ExerciseType.MULTIPLE_CHOICE,
                question = "¿Cuál es la forma correcta de imprimir 'Hola Mundo' en Python?",
                options = listOf(
                    "print('Hola Mundo')",
                    "echo('Hola Mundo')",
                    "console.log('Hola Mundo')",
                    "System.out.println('Hola Mundo')"
                ),
                correctAnswer = 0,
                explanation = "En Python usamos la función print() para mostrar texto en pantalla."
            ),
            ExerciseData(
                type = ExerciseType.CODE_COMPLETION,
                question = "Completa el código para crear un comentario en Python:",
                code = "# ___",
                correctAnswer = "Este es un comentario",
                explanation = "En Python, los comentarios de una línea comienzan con el símbolo #"
            ),
            ExerciseData(
                type = ExerciseType.TRUE_FALSE,
                question = "En Python, la indentación es importante para la estructura del código.",
                correctAnswer = true,
                explanation = "Python usa la indentación para definir bloques de código, a diferencia de otros lenguajes que usan llaves."
            ),
            ExerciseData(
                type = ExerciseType.MULTIPLE_CHOICE,
                question = "¿Cuál es la extensión de archivo para código Python?",
                options = listOf(
                    ".py",
                    ".python",
                    ".pyt",
                    ".pyc"
                ),
                correctAnswer = 0,
                explanation = "Los archivos de Python tienen la extensión .py"
            )
        )
        2 -> listOf( // Declaración de Variables y Tipos de Datos
            ExerciseData(
                type = ExerciseType.CODE_COMPLETION,
                question = "Declara una variable llamada 'edad' con el valor 25:",
                code = "___ = 25",
                correctAnswer = "edad",
                explanation = "En Python, las variables se declaran asignando un valor con el operador ="
            ),
            ExerciseData(
                type = ExerciseType.MULTIPLE_CHOICE,
                question = "¿Cuál es el tipo de dato de la variable: nombre = 'Ana'?",
                options = listOf(
                    "str (string)",
                    "int (entero)",
                    "float (decimal)",
                    "bool (booleano)"
                ),
                correctAnswer = 0,
                explanation = "Las cadenas de texto en Python son del tipo 'str' (string)"
            ),
            ExerciseData(
                type = ExerciseType.TRUE_FALSE,
                question = "En Python, una variable puede cambiar de tipo de dato durante la ejecución.",
                correctAnswer = true,
                explanation = "Python es de tipado dinámico, las variables pueden cambiar de tipo."
            ),
            ExerciseData(
                type = ExerciseType.CODE_COMPLETION,
                question = "Crea una variable booleana llamada 'activo' con valor True:",
                code = "activo = ___",
                correctAnswer = "True",
                explanation = "Los valores booleanos en Python son True y False (con mayúscula inicial)"
            ),
            ExerciseData(
                type = ExerciseType.MULTIPLE_CHOICE,
                question = "¿Cuál es el resultado de: type(3.14)?",
                options = listOf(
                    "<class 'float'>",
                    "<class 'int'>",
                    "<class 'str'>",
                    "<class 'bool'>"
                ),
                correctAnswer = 0,
                explanation = "Los números decimales en Python son del tipo 'float'"
            )
        )
        3 -> listOf( // Operadores
            ExerciseData(
                type = ExerciseType.CODE_COMPLETION,
                question = "¿Cuál es el resultado de: 10 // 3?",
                code = "resultado = 10 // 3  # resultado = ___",
                correctAnswer = "3",
                explanation = "El operador // realiza división entera, descartando la parte decimal"
            ),
            ExerciseData(
                type = ExerciseType.MULTIPLE_CHOICE,
                question = "¿Cuál operador se usa para obtener el resto de una división?",
                options = listOf(
                    "%",
                    "//",
                    "/",
                    "mod"
                ),
                correctAnswer = 0,
                explanation = "El operador % (módulo) devuelve el resto de una división"
            ),
            ExerciseData(
                type = ExerciseType.TRUE_FALSE,
                question = "En Python, 5 == 5.0 devuelve True.",
                correctAnswer = true,
                explanation = "Python considera que 5 y 5.0 son iguales en valor, aunque sean tipos diferentes"
            ),
            ExerciseData(
                type = ExerciseType.CODE_COMPLETION,
                question = "Completa la comparación: 7 ___ 5",
                code = "7 ___ 5  # ¿Qué operador usar para 'mayor que'?",
                correctAnswer = ">",
                explanation = "El operador > se usa para comparar si un número es mayor que otro"
            )
        )
        // Add more modules as needed...
        else -> listOf(
            ExerciseData(
                type = ExerciseType.MULTIPLE_CHOICE,
                question = "¿Cuál es la forma correcta de imprimir 'Hola Mundo' en Python?",
                options = listOf(
                    "print('Hola Mundo')",
                    "echo('Hola Mundo')",
                    "console.log('Hola Mundo')",
                    "System.out.println('Hola Mundo')"
                ),
                correctAnswer = 0,
                explanation = "En Python usamos la función print() para mostrar texto en pantalla."
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LessonScreenPreview() {
    KodeLearnTheme {
        LessonScreen()
    }
}
