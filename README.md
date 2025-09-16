# KodeLearn - Aplicación de Aprendizaje Gamificado de Programación

Una aplicación móvil Android inspirada en Duolingo para aprender programación de manera gamificada, desarrollada con Kotlin, Jetpack Compose y arquitectura MVVM.

## 🚀 Características Principales

### Pantalla de Perfil
- ✅ Avatar circular (imagen por defecto)
- ✅ Nombre del usuario con opción de "Añadir biografía"
- ✅ Estadísticas completas:
  - Racha diaria (con emoji de fuego 🔥)
  - XP total (con sistema de puntos)
  - Liga actual (Wooden, Bronze, Silver, Gold)
- ✅ Botón "Compartir mi progreso"
- ✅ Botón promocional "Prueba PRO Gratis"
- ✅ Sección de amigos con mensaje "Aún sin amigos/as" y botón para añadir

### Pantalla de Aprendizaje
- ✅ Barra superior con:
  - ❤️ Vidas (hearts)
  - 🪙 Monedas (coins)
  - 🔥 Racha diaria
- ✅ Título del curso: "Python"
- ✅ Módulos en forma de camino/ruta:
  - "Python Básico" con progreso visual
  - Sistema de bloqueo hasta completar módulo anterior
  - Indicador de progreso en porcentaje
  - Estado visual (desbloqueado/bloqueado/completado)

## 🏗️ Arquitectura Técnica

### Stack Tecnológico
- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Base de datos**: Room (SQLite)
- **Navegación**: Navigation Compose
- **Gestión de estado**: StateFlow y Compose State

### Estructura del Proyecto
```
app/
├── src/main/java/com/example/kodelearn/
│   ├── data/
│   │   ├── database/
│   │   │   ├── entities/          # User, Course, Module, Progress
│   │   │   ├── dao/               # Data Access Objects
│   │   │   └── KodeLearnDatabase.kt
│   │   ├── repository/            # Repository pattern
│   │   └── MockData.kt           # Datos de prueba
│   ├── ui/
│   │   ├── components/           # Componentes reusables
│   │   ├── screens/              # ProfileScreen, LearningScreen
│   │   ├── viewmodel/            # ViewModels MVVM
│   │   └── theme/                # Colors, Typography, Theme
│   ├── navigation/               # Navigation Compose setup
│   ├── KodeLearnApplication.kt   # Application class
│   └── MainActivity.kt           # Activity principal
```

### Componentes Reusables Creados
- **KodeLearnButton**: Botones personalizados con variantes (Primary, Secondary, Outline)
- **StatCard**: Tarjetas de estadísticas con iconos y valores
- **ModuleItem**: Elementos de módulo con progreso y estados (bloqueado/desbloqueado/completado)
- **LearningTopBar**: Barra superior con vidas, monedas y racha
- **ProgressOverview**: Vista general del progreso del curso

## 🎨 Diseño y UI

### Tema Oscuro
- ✅ Paleta de colores dark-friendly
- ✅ Verde principal similar a Duolingo (#58CC02)
- ✅ Azul secundario para elementos complementarios
- ✅ Colores específicos para:
  - Hearts (❤️): Rojo
  - Coins (🪙): Dorado  
  - XP: Púrpura
  - Progress: Verde
  - League: Madera, Bronce, Plata, Oro

### Componentes UI
- Cards con elevación y bordes redondeados
- Botones con estados y efectos de presión
- Barras de progreso animadas
- Icons de Material Design Extended

## 📊 Base de Datos

### Entidades Room
1. **User**: Perfil del usuario, estadísticas, configuración
2. **Course**: Información de cursos (Python, JavaScript, Kotlin)
3. **Module**: Módulos dentro de cada curso
4. **Progress**: Progreso del usuario por módulo

### Mock Data Incluido
- Usuario por defecto con 130 XP, 5 vidas, 220 monedas
- Curso de Python con 3 módulos:
  - "Python Básico" (5 lecciones)
  - "Estructuras de Control" (4 lecciones) 
  - "Funciones y Métodos" (6 lecciones)
- Progreso inicial en el primer módulo (40% completado)

## 🔧 Instalación y Configuración

### Prerrequisitos
- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 8 o superior
- Android SDK API 24+ (Android 7.0)
- Dispositivo/emulador Android

### Pasos de Instalación

1. **Clonar o descargar el proyecto**
   ```bash
   # Si tienes git configurado
   git clone <repository-url>
   cd KodeLearn
   ```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar la carpeta del proyecto
   - Esperar a que Android Studio indexe y sincronice

3. **Sincronizar dependencias**
   - Android Studio debería mostrar "Sync Now" automáticamente
   - O ir a File → Sync Project with Gradle Files

4. **Compilar la aplicación**
   - Build → Make Project (Ctrl+F9)
   - O usar el botón de compilación en la toolbar

5. **Ejecutar la aplicación**
   - Conectar dispositivo Android o iniciar emulador
   - Run → Run 'app' (Shift+F10)
   - O usar el botón de play verde

### Verificación de Instalación
- La app debería iniciar en la pantalla de "Aprender"
- Navegar a "Perfil" usando la barra inferior
- Verificar que se muestran las estadísticas mock
- Verificar que el módulo "Python Básico" muestra 40% de progreso

## 🎯 Funcionalidades Implementadas

### ✅ Completamente Funcional
- Navegación entre pantallas Profile ↔ Learning
- Visualización de estadísticas del usuario
- Lista de módulos con estados visuales
- Tema oscuro completo
- Base de datos local con Room
- Mock data precargada

### 🔄 Para Futuras Iteraciones
- Sistema de lecciones individuales
- Quiz y ejercicios interactivos
- Sistema de logros y badges
- Funcionalidad social real (añadir amigos)
- Compartir progreso en redes sociales
- Sistema de notificaciones
- Más cursos y lenguajes de programación
- Sistema de pago para PRO

## 🚀 Próximos Pasos de Desarrollo

1. **Pantalla de Lección**: Implementar lecciones individuales con ejercicios
2. **Sistema de Quiz**: Preguntas de opción múltiple y código interactivo
3. **Logros y Badges**: Sistema de reconocimientos gamificado
4. **Social Features**: Chat con amigos, leaderboards, competencias
5. **Contenido Expandido**: Más cursos, ejercicios avanzados
6. **Monetización**: Suscripción PRO con funciones premium

## 🧪 Testing

### Para probar la aplicación:
1. Compilar y ejecutar según instrucciones anteriores
2. Navegar entre las dos pantallas principales
3. Verificar datos de perfil (130 XP, 5 ❤️, 220 🪙)
4. Comprobar progreso en módulo "Python Básico" (40%)
5. Intentar editar biografía en perfil
6. Verificar que módulos bloqueados no son clickeables

## 📱 Compatibilidad
- **Mínimo**: Android 7.0 (API 24)
- **Target**: Android 14 (API 34)
- **Compilación**: Android 14 (API 34)

## 👥 Contribución
Este es un proyecto base listo para expandir. Para añadir nuevas funcionalidades:
1. Seguir la arquitectura MVVM establecida
2. Usar los componentes reusables existentes
3. Mantener consistencia con el tema de colores
4. Actualizar Room database con nuevas entidades si es necesario

¡La aplicación está lista para compilar y usar! 🎉
