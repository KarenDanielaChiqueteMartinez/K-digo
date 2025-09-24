# Diseño de Camino de Aprendizaje Estilo Candy Crush

## Descripción General

Se ha implementado un nuevo diseño para la pantalla de aprendizaje que presenta los módulos del curso en un camino curvo estilo Candy Crush, creando una experiencia visual atractiva y motivadora para los usuarios.

## Características Principales

### 🎮 Diseño Visual
- **Camino Curvo**: Los módulos se muestran siguiendo una trayectoria en forma de S con efectos de onda animados
- **Colores Vibrantes**: Cada módulo tiene un color único inspirado en Candy Crush (rojo, naranja, amarillo, verde, azul, morado, rosa)
- **Animaciones Suaves**: Entrada escalonada de módulos con animaciones de escala y opacidad
- **Efectos Visuales**: Líneas de conexión animadas con puntos brillantes y efectos de resplandor

### 📱 Diseño Responsive
- **Adaptativo**: Se ajusta automáticamente a diferentes tamaños de pantalla
- **Tablet Optimizado**: Tamaños de módulos y espaciado mayores en tablets
- **Orientación**: Funciona tanto en modo vertical como horizontal

### 🎯 Interactividad
- **Click en Cualquier Módulo**: Los usuarios pueden hacer clic en cualquier módulo (incluso bloqueados)
- **Popup Informativo**: Se abre un diálogo detallado con información del módulo
- **Estados Visuales**: Diferentes estilos para módulos disponibles, bloqueados, en progreso y completados

## Componentes Creados

### 1. ModulePath
**Archivo**: `app/src/main/java/com/example/kodelearn/ui/components/ModulePath.kt`

**Funcionalidades**:
- Renderiza el camino curvo con animaciones
- Calcula posiciones de módulos en trayectoria S
- Maneja animaciones de entrada escalonadas
- Dibuja líneas de conexión animadas

**Parámetros**:
- `modules`: Lista de módulos con progreso
- `onModuleClick`: Callback para clicks en módulos
- `modifier`: Modificador de layout

### 2. PathModule
**Componente interno de ModulePath**

**Funcionalidades**:
- Representa cada módulo individual en el camino
- Muestra número de módulo y estado (bloqueado, en progreso, completado)
- Colores dinámicos basados en el orden del módulo
- Animaciones de entrada con escala y opacidad

### 3. ModulePopup
**Archivo**: `app/src/main/java/com/example/kodelearn/ui/components/ModulePopup.kt`

**Funcionalidades**:
- Dialog emergente con información detallada del módulo
- Muestra estadísticas (lecciones, XP, progreso)
- Botones de acción contextuales (Comenzar, Continuar, Repasar)
- Mensajes informativos para módulos bloqueados
- Animaciones de entrada con escala y opacidad

**Estados del Popup**:
- **Disponible**: Botón "Comenzar" o "Continuar"
- **Completado**: Botón "Repasar"
- **Bloqueado**: Mensaje explicativo y botón "Entendido"

### 4. ResponsiveExtensions
**Archivo**: `app/src/main/java/com/example/kodelearn/ui/components/ResponsiveExtensions.kt`

**Funcionalidades**:
- Extensiones para diseño responsive
- Dimensiones adaptativas para diferentes pantallas
- Espaciado responsive
- Duración de animaciones adaptativa

## Paleta de Colores

### Colores Principales
- **PrimaryGreen**: `#58CC02` - Color principal de la app
- **SuccessColor**: `#10B981` - Módulos completados
- **ModuleLocked**: `#9E9E9E` - Módulos bloqueados

### Colores Candy Crush
- **CandyRed**: `#FF6B6B`
- **CandyOrange**: `#FFA726`
- **CandyYellow**: `#FFEB3B`
- **CandyGreen**: `#4CAF50`
- **CandyBlue**: `#2196F3`
- **CandyPurple**: `#9C27B0`
- **CandyPink**: `#E91E63`

### Colores de Camino
- **PathColor**: Color de las líneas de conexión
- **ConnectionDot**: Puntos de conexión
- **PathGlow**: Efectos de resplandor

## Estados de Módulos

### 1. Disponible
- Color: Colores Candy Crush variados
- Icono: Flecha de reproducción
- Acción: Abre popup con opción de comenzar

### 2. En Progreso
- Color: Azul Candy
- Icono: Flecha de reproducción
- Progreso: Porcentaje visible
- Acción: Abre popup con opción de continuar

### 3. Completado
- Color: Verde de éxito
- Icono: Checkmark
- Indicador: Marca de verificación
- Acción: Abre popup con opción de repasar

### 4. Bloqueado
- Color: Gris opaco
- Icono: Candado
- Estado: Semi-transparente
- Acción: Abre popup con mensaje explicativo

## Animaciones

### 1. Entrada de Módulos
- **Tipo**: Animación escalonada
- **Duración**: 200ms entre módulos
- **Efecto**: Escala y opacidad con spring bounce

### 2. Camino Animado
- **Tipo**: Líneas punteadas en movimiento
- **Duración**: 3-4 segundos (responsive)
- **Efecto**: Movimiento continuo de puntos

### 3. Popup
- **Tipo**: Escala y opacidad
- **Duración**: 300-400ms (responsive)
- **Efecto**: Spring bounce

## Integración

### LearningScreen Actualizada
La pantalla principal de aprendizaje ahora incluye:
- Header del curso
- Resumen de progreso
- Título "Tu camino de aprendizaje"
- Componente ModulePath
- Gestión de estado del popup

### Navegación
- Los clicks en módulos abren el popup
- El popup permite navegar a lecciones específicas
- Mantiene la funcionalidad existente de navegación

## Responsive Design

### Pantallas Móviles
- Módulos: 80dp
- Espaciado: 100dp
- Popup: 90% del ancho
- Animaciones: 300ms

### Tablets
- Módulos: 100dp
- Espaciado: 120dp
- Popup: 60% del ancho
- Animaciones: 400ms

## Uso

```kotlin
// En LearningScreen
ModulePath(
    modules = uiState.modulesWithProgress,
    onModuleClick = { moduleWithProgress ->
        selectedModule = moduleWithProgress
        showPopup = true
    }
)

// Popup
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
```

## Beneficios

1. **Experiencia Visual Atractiva**: Diseño colorido y animado que motiva al usuario
2. **Progresión Clara**: El camino visual muestra claramente el progreso del usuario
3. **Interactividad Mejorada**: Información detallada accesible con un click
4. **Responsive**: Funciona perfectamente en todos los dispositivos
5. **Accesibilidad**: Módulos bloqueados siguen siendo visibles e informativos
6. **Gamificación**: Colores y animaciones que hacen el aprendizaje más divertido

## Consideraciones Técnicas

- **Performance**: Animaciones optimizadas para 60fps
- **Memoria**: Componentes eficientes con recomposición mínima
- **Accesibilidad**: Contraste adecuado y tamaños táctiles apropiados
- **Mantenibilidad**: Código modular y bien documentado
