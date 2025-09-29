# Implementación del Algoritmo KNN en KodeLearn

## Descripción General

Se ha implementado un sistema completo de recomendaciones basado en el algoritmo K-Nearest Neighbors (KNN) para la aplicación KodeLearn. Este sistema permite analizar patrones de aprendizaje de usuarios y proporcionar recomendaciones personalizadas.

## Componentes Implementados

### 1. UserFeatureVector.kt
**Ubicación:** `app/src/main/java/com/example/kodelearn/data/ml/UserFeatureVector.kt`

Representa el vector de características de un usuario con las siguientes métricas:

- **accuracyRate**: Tasa de aciertos (0.0 - 1.0)
- **learningSpeed**: Velocidad de aprendizaje (lecciones/día)
- **studyFrequency**: Frecuencia de estudio (sesiones/semana)
- **consistency**: Consistencia en el estudio (0.0 - 1.0)
- **difficultyPreference**: Preferencia por dificultad (0.0 - 1.0)
- **timePerLesson**: Tiempo promedio por lección (minutos)
- **completionRate**: Tasa de finalización de módulos (0.0 - 1.0)
- **streakLength**: Longitud de racha actual (normalizada)
- **xpEarned**: XP ganado (normalizado)

**Funcionalidades:**
- Cálculo de distancia euclidiana entre vectores
- Cálculo de distancia ponderada
- Creación automática desde datos de usuario y progreso
- Normalización de características

### 2. KNNAlgorithm.kt
**Ubicación:** `app/src/main/java/com/example/kodelearn/data/ml/KNNAlgorithm.kt`

Implementación del algoritmo KNN con las siguientes características:

- **Configuración flexible**: Número de vecinos (k), distancia ponderada
- **Clasificación**: Categorización de usuarios (beginner, intermediate, advanced, expert)
- **Recomendaciones**: Encuentra usuarios similares
- **Predicciones**: Predice rendimiento futuro
- **Validación**: Validación cruzada del modelo
- **Optimización**: Optimización automática de pesos

**Métodos principales:**
- `train()`: Entrena el modelo con datos de usuarios
- `findNearestNeighbors()`: Encuentra los K vecinos más cercanos
- `classifyUser()`: Clasifica a un usuario
- `recommendSimilarUsers()`: Recomienda usuarios similares
- `predictPerformance()`: Predice rendimiento futuro
- `validateModel()`: Valida el modelo

### 3. UserRecommendationService.kt
**Ubicación:** `app/src/main/java/com/example/kodelearn/data/ml/UserRecommendationService.kt`

Servicio que integra el algoritmo KNN con el repositorio de datos:

- **Inicialización**: Entrena el modelo con todos los usuarios
- **Recomendaciones**: Obtiene usuarios similares
- **Análisis**: Analiza patrones de aprendizaje
- **Predicciones**: Predice rendimiento de usuarios
- **Validación**: Valida el modelo con datos reales

### 4. RecommendationViewModel.kt
**Ubicación:** `app/src/main/java/com/example/kodelearn/ui/viewmodel/RecommendationViewModel.kt`

ViewModel que maneja el estado de la UI para recomendaciones:

- **Estado reactivo**: Usa StateFlow para actualizaciones automáticas
- **Gestión de errores**: Manejo centralizado de errores
- **Carga asíncrona**: Operaciones no bloqueantes
- **Formateo de datos**: Convierte datos para la UI

### 5. RecommendationScreen.kt
**Ubicación:** `app/src/main/java/com/example/kodelearn/ui/screens/RecommendationScreen.kt`

Pantalla de UI que muestra:

- **Clasificación del usuario**: Categoría de aprendizaje actual
- **Predicción de rendimiento**: Métricas predichas
- **Estadísticas de similitud**: Comparación con otros usuarios
- **Patrones de aprendizaje**: Análisis de comportamiento
- **Usuarios similares**: Lista de usuarios con características similares

## Uso del Sistema

### 1. Inicialización
```kotlin
val recommendationService = UserRecommendationService(repository)
recommendationService.initialize()
```

### 2. Obtener Recomendaciones
```kotlin
val similarUsers = recommendationService.getSimilarUsers(userId, limit = 10)
```

### 3. Clasificar Usuario
```kotlin
val classification = recommendationService.classifyUser(userId)
```

### 4. Predecir Rendimiento
```kotlin
val prediction = recommendationService.predictUserPerformance(userId)
```

### 5. Analizar Patrones
```kotlin
val analysis = recommendationService.analyzeLearningPatterns(userId)
```

## Configuración del Algoritmo

### Parámetros KNN
- **k**: Número de vecinos más cercanos (default: 5)
- **useWeightedDistance**: Usar distancia ponderada (default: false)
- **featureWeights**: Pesos para características específicas

### Pesos de Características
```kotlin
val weights = FeatureWeights(
    accuracy = 1.0,
    learningSpeed = 1.0,
    studyFrequency = 1.0,
    consistency = 1.0,
    difficultyPreference = 1.0,
    timePerLesson = 1.0,
    completionRate = 1.0,
    streakLength = 1.0,
    xpEarned = 1.0
)
```

## Integración con la UI

### 1. Agregar a la Navegación
```kotlin
// En KodeLearnNavigation.kt
composable("recommendations") {
    RecommendationScreen(
        onNavigateBack = { navController.popBackStack() }
    )
}
```

### 2. Usar en ViewModel
```kotlin
class SomeViewModel(
    private val recommendationViewModel: RecommendationViewModel
) {
    fun loadRecommendations() {
        recommendationViewModel.loadUserRecommendations()
    }
}
```

## Validación y Testing

### 1. Validación del Modelo
```kotlin
val validationResult = recommendationService.validateModel()
println("Precisión: ${validationResult.accuracy * 100}%")
```

### 2. Optimización de Pesos
```kotlin
val optimizedWeights = knnAlgorithm.optimizeWeights(validationData)
```

## Consideraciones de Rendimiento

1. **Caching**: Los vectores de características se calculan una vez y se reutilizan
2. **Lazy Loading**: Los datos se cargan solo cuando se necesitan
3. **Background Processing**: Las operaciones pesadas se ejecutan en background
4. **Memory Management**: Los datos se liberan cuando no se necesitan

## Extensiones Futuras

1. **Clustering**: Implementar algoritmos de clustering para agrupar usuarios
2. **Deep Learning**: Integrar redes neuronales para predicciones más precisas
3. **Real-time Updates**: Actualizar recomendaciones en tiempo real
4. **A/B Testing**: Probar diferentes configuraciones del algoritmo
5. **Analytics**: Métricas detalladas de uso del sistema de recomendaciones

## Troubleshooting

### Problemas Comunes

1. **Modelo no inicializado**: Verificar que `initialize()` se haya llamado
2. **Datos insuficientes**: Necesita al menos k+1 usuarios para funcionar
3. **Memoria insuficiente**: Reducir el número de usuarios o características
4. **Precisión baja**: Ajustar pesos de características o aumentar k

### Logs de Debug
```kotlin
// Habilitar logs detallados
Log.d("KNN", "Entrenando modelo con ${trainingData.size} usuarios")
Log.d("KNN", "Clasificación: $classification")
Log.d("KNN", "Precisión: ${validationResult.accuracy}")
```

## Conclusión

La implementación del algoritmo KNN en KodeLearn proporciona un sistema robusto y escalable para recomendaciones personalizadas. El sistema está diseñado para ser fácil de usar, mantener y extender, con una arquitectura modular que permite futuras mejoras y optimizaciones.
