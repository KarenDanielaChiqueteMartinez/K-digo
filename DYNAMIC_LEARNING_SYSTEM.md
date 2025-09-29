# Sistema de Aprendizaje Dinámico

## Descripción General

Se ha implementado un sistema completo de aprendizaje dinámico que genera datos reales de estudio, reemplazando los datos estáticos. Este sistema permite probar el algoritmo KNN con datos de aprendizaje reales y actualizados en tiempo real.

## Componentes Implementados

### 1. LearningSessionService.kt
**Ubicación:** `app/src/main/java/com/example/kodelearn/data/learning/LearningSessionService.kt`

**Funcionalidades:**
- **Gestión de sesiones**: Inicia, actualiza y termina sesiones de aprendizaje
- **Cálculo dinámico de XP**: Basado en respuestas correctas y tiempo invertido
- **Seguimiento de tiempo**: Monitorea tiempo real de estudio por lección
- **Simulación de aprendizaje**: Genera sesiones automáticas para testing
- **Estadísticas en tiempo real**: Calcula precisión, velocidad y progreso

**Métodos principales:**
- `startLearningSession()`: Inicia nueva sesión
- `updateProgress()`: Actualiza progreso durante la sesión
- `completeLesson()`: Marca lección como completada
- `endCurrentSession()`: Termina sesión y guarda estadísticas
- `simulateLearningSession()`: Simula sesión completa para testing
- `getUserLearningStats()`: Obtiene estadísticas del usuario

### 2. LearningSessionViewModel.kt
**Ubicación:** `app/src/main/java/com/example/kodelearn/ui/viewmodel/LearningSessionViewModel.kt`

**Funcionalidades:**
- **Estado reactivo**: Maneja estado de sesiones con StateFlow
- **Integración UI**: Conecta servicio con la interfaz
- **Formateo de datos**: Convierte datos para mostrar en UI
- **Gestión de errores**: Manejo centralizado de errores
- **Generación de datos de prueba**: Crea múltiples sesiones automáticamente

### 3. TestLearningScreen.kt
**Ubicación:** `app/src/main/java/com/example/kodelearn/ui/screens/TestLearningScreen.kt`

**Funcionalidades:**
- **Interfaz de testing**: Pantalla completa para probar el sistema
- **Controles de sesión**: Botones para iniciar/terminar sesiones
- **Configuración flexible**: Ajustar módulo, lecciones y usuario
- **Visualización en tiempo real**: Muestra progreso actual
- **Historial de sesiones**: Lista de sesiones anteriores
- **Estadísticas del usuario**: Métricas de aprendizaje

### 4. Métodos adicionales en KodeLearnRepository.kt
**Funcionalidades:**
- `createTestUser()`: Crea usuarios de prueba
- `getUserById()`: Obtiene usuario específico
- `resetUserProgress()`: Resetea progreso para testing
- `getModuleById()`: Obtiene módulo específico

## Características del Sistema

### 🎯 **Datos Dinámicos Generados:**

1. **XP Dinámico**:
   - Base: 10 XP por respuesta correcta, 5 XP por incorrecta
   - Bonus por tiempo: +5 XP si termina rápido, +3 si normal, +1 si lento
   - Acumulación en tiempo real

2. **Tiempo de Aprendizaje**:
   - Seguimiento preciso por lección
   - Tiempo promedio por dificultad (Easy: 1min, Medium: 1.5min, Hard: 2min)
   - Cálculo de velocidad de aprendizaje

3. **Progreso Real**:
   - Actualización automática de porcentaje de módulos
   - Marcado de lecciones completadas
   - Seguimiento de precisión por sesión

4. **Estadísticas de Usuario**:
   - Total de sesiones realizadas
   - Tiempo total invertido
   - XP total ganado
   - Precisión promedio
   - Racha de días de estudio

### 🔄 **Integración con KNN:**

Los datos generados se integran automáticamente con el algoritmo KNN:

- **UserFeatureVector**: Se actualiza con datos reales de aprendizaje
- **Recomendaciones**: Basadas en comportamiento real de estudio
- **Clasificación**: Usuarios categorizados por rendimiento real
- **Predicciones**: Basadas en patrones de aprendizaje reales

## Uso del Sistema

### 1. **Pantalla de Prueba**

Accede a `TestLearningScreen` para:
- Configurar parámetros de testing
- Iniciar sesiones manuales o automáticas
- Generar múltiples sesiones de prueba
- Ver estadísticas en tiempo real

### 2. **Configuración de Prueba**

```kotlin
// Configurar usuario, módulo y número de lecciones
val userId = 1
val moduleId = 1
val numLessons = 3
```

### 3. **Generar Datos de Prueba**

```kotlin
// Generar 5 sesiones automáticas
viewModel.generateTestData(userId, 5)

// O simular una sesión específica
viewModel.simulateLearningSession(userId, moduleId, numLessons)
```

### 4. **Sesión Manual**

```kotlin
// Iniciar sesión
viewModel.startLearningSession(userId, moduleId, 1)

// Actualizar progreso
viewModel.updateProgress(userId, moduleId, 1, isCorrect = true, timeSpent = 30000)

// Completar lección
viewModel.completeLesson(userId, moduleId, 1)

// Terminar sesión
viewModel.endCurrentSession(userId)
```

## Datos Generados

### **Ejemplo de Sesión de Aprendizaje:**

```kotlin
LearningSession(
    id = "session_1703123456789_123",
    userId = 1,
    moduleId = 1,
    lessonId = 1,
    startTime = 1703123456789,
    endTime = 1703123656789,
    duration = 200000, // 3.33 minutos
    xpEarned = 25,
    lessonsCompleted = 3,
    accuracy = 0.85f,
    difficulty = LessonDifficulty.MEDIUM
)
```

### **Estadísticas de Usuario:**

```kotlin
LearningStats(
    totalSessions = 15,
    totalTimeSpent = 1800000, // 30 minutos
    totalXPEarned = 450,
    averageAccuracy = 0.78f,
    lessonsCompleted = 45,
    averageSessionDuration = 120000, // 2 minutos
    streakDays = 5,
    lastStudyDate = 1703123456789
)
```

## Beneficios para el Algoritmo KNN

### 1. **Datos Realistas**
- Comportamiento de aprendizaje real
- Patrones de estudio auténticos
- Variabilidad natural en rendimiento

### 2. **Métricas Precisas**
- Tiempo real de estudio por lección
- XP ganado basado en rendimiento
- Progreso actualizado dinámicamente

### 3. **Testing Completo**
- Múltiples usuarios con diferentes patrones
- Variedad en dificultades y módulos
- Datos suficientes para entrenar KNN

### 4. **Validación del Modelo**
- Pruebas con datos reales vs simulados
- Verificación de precisión de recomendaciones
- Optimización de parámetros del algoritmo

## Próximos Pasos

1. **Integrar con UI existente**: Conectar con pantallas de aprendizaje
2. **Persistencia de datos**: Guardar sesiones en base de datos
3. **Analytics avanzados**: Métricas más detalladas
4. **Gamificación**: Logros y recompensas basados en rendimiento
5. **Personalización**: Adaptar dificultad según rendimiento

## Conclusión

El sistema de aprendizaje dinámico proporciona una base sólida para probar y validar el algoritmo KNN con datos reales de aprendizaje. Esto permite generar recomendaciones más precisas y personalizadas basadas en el comportamiento real de los usuarios.
