package com.example.kodelearn.data.content

/**
 * Contenido de las lecciones del módulo "Introducción a la sintaxis básica"
 */
object ModuleContent {
    
    val introductionModule = ModuleContentData(
        moduleId = 1,
        moduleName = "Introducción a la Sintaxis Básica",
        description = "Aprende los fundamentos de la sintaxis de programación",
        totalLessons = 5,
        lessons = listOf(
            LessonContent(
                id = 1,
                title = "¿Qué es la sintaxis?",
                description = "Conceptos básicos de sintaxis en programación",
                content = """
                    ## ¿Qué es la sintaxis?
                    
                    La **sintaxis** es el conjunto de reglas que define cómo escribir código correctamente en un lenguaje de programación.
                    
                    ### Características importantes:
                    - **Puntuación**: Los signos de puntuación tienen significado especial
                    - **Palabras clave**: Palabras reservadas del lenguaje
                    - **Estructura**: Cómo organizar el código
                    
                    ### Ejemplo básico:
                    ```kotlin
                    fun main() {
                        println("¡Hola Mundo!")
                    }
                    ```
                    
                    ### Puntos clave:
                    1. Las llaves `{}` definen bloques de código
                    2. Los paréntesis `()` encierran parámetros
                    3. El punto y coma `;` puede ser opcional en Kotlin
                """.trimIndent(),
                questions = listOf(
                    Question(
                        id = 1,
                        question = "¿Qué define la sintaxis en programación?",
                        options = listOf(
                            "El color del código",
                            "Las reglas para escribir código correctamente",
                            "La velocidad de ejecución",
                            "El tamaño del archivo"
                        ),
                        correctAnswer = 1,
                        explanation = "La sintaxis son las reglas que definen cómo escribir código correctamente."
                    ),
                    Question(
                        id = 2,
                        question = "¿Qué símbolos se usan para definir bloques de código?",
                        options = listOf(
                            "Paréntesis ()",
                            "Llaves {}",
                            "Corchetes []",
                            "Comillas \"\""
                        ),
                        correctAnswer = 1,
                        explanation = "Las llaves {} se usan para definir bloques de código en la mayoría de lenguajes."
                    )
                ),
                xpReward = 20,
                timeEstimate = 3 // minutos
            ),
            
            LessonContent(
                id = 2,
                title = "Variables y constantes",
                description = "Cómo declarar y usar variables en Kotlin",
                content = """
                    ## Variables y Constantes
                    
                    Las **variables** son contenedores que almacenan datos que pueden cambiar.
                    Las **constantes** almacenan datos que no cambian.
                    
                    ### Declaración de variables:
                    ```kotlin
                    var nombre = "Juan"        // Variable mutable
                    val edad = 25              // Constante (inmutable)
                    var numero: Int = 10       // Con tipo explícito
                    ```
                    
                    ### Tipos de datos básicos:
                    - `String`: Texto
                    - `Int`: Números enteros
                    - `Double`: Números decimales
                    - `Boolean`: Verdadero o falso
                    
                    ### Ejemplo práctico:
                    ```kotlin
                    val nombre = "María"
                    var edad = 20
                    edad = 21  // Cambio permitido
                    // nombre = "Ana"  // Error: no se puede cambiar
                    ```
                """.trimIndent(),
                questions = listOf(
                    Question(
                        id = 3,
                        question = "¿Cuál es la diferencia entre 'var' y 'val'?",
                        options = listOf(
                            "No hay diferencia",
                            "'var' es mutable, 'val' es inmutable",
                            "'val' es mutable, 'var' es inmutable",
                            "Solo son estilos diferentes"
                        ),
                        correctAnswer = 1,
                        explanation = "'var' permite cambios, 'val' crea constantes inmutables."
                    ),
                    Question(
                        id = 4,
                        question = "¿Qué tipo de dato usarías para almacenar un nombre?",
                        options = listOf(
                            "Int",
                            "String",
                            "Boolean",
                            "Double"
                        ),
                        correctAnswer = 1,
                        explanation = "String es para almacenar texto como nombres."
                    )
                ),
                xpReward = 25,
                timeEstimate = 4
            ),
            
            LessonContent(
                id = 3,
                title = "Funciones básicas",
                description = "Cómo crear y usar funciones en Kotlin",
                content = """
                    ## Funciones Básicas
                    
                    Las **funciones** son bloques de código reutilizables que realizan una tarea específica.
                    
                    ### Sintaxis básica:
                    ```kotlin
                    fun nombreFuncion(parametros): TipoRetorno {
                        // Código de la función
                        return valor
                    }
                    ```
                    
                    ### Ejemplos prácticos:
                    ```kotlin
                    // Función sin parámetros ni retorno
                    fun saludar() {
                        println("¡Hola!")
                    }
                    
                    // Función con parámetros
                    fun sumar(a: Int, b: Int): Int {
                        return a + b
                    }
                    
                    // Función con expresión única
                    fun multiplicar(a: Int, b: Int) = a * b
                    ```
                    
                    ### Llamar funciones:
                    ```kotlin
                    saludar()                    // Sin parámetros
                    val resultado = sumar(5, 3)  // Con parámetros
                    println(resultado)           // Imprime: 8
                    ```
                """.trimIndent(),
                questions = listOf(
                    Question(
                        id = 5,
                        question = "¿Qué palabra clave se usa para crear funciones en Kotlin?",
                        options = listOf(
                            "function",
                            "fun",
                            "def",
                            "method"
                        ),
                        correctAnswer = 1,
                        explanation = "En Kotlin se usa 'fun' para declarar funciones."
                    ),
                    Question(
                        id = 6,
                        question = "¿Cuál es el resultado de sumar(10, 5)?",
                        options = listOf(
                            "15",
                            "50",
                            "105",
                            "Error"
                        ),
                        correctAnswer = 0,
                        explanation = "sumar(10, 5) retorna 15."
                    )
                ),
                xpReward = 30,
                timeEstimate = 5
            ),
            
            LessonContent(
                id = 4,
                title = "Condicionales",
                description = "Uso de if/else para tomar decisiones",
                content = """
                    ## Condicionales
                    
                    Los **condicionales** permiten que el código tome decisiones basadas en condiciones.
                    
                    ### Estructura if/else:
                    ```kotlin
                    if (condicion) {
                        // Código si la condición es verdadera
                    } else {
                        // Código si la condición es falsa
                    }
                    ```
                    
                    ### Ejemplos prácticos:
                    ```kotlin
                    val edad = 18
                    
                    if (edad >= 18) {
                        println("Eres mayor de edad")
                    } else {
                        println("Eres menor de edad")
                    }
                    
                    // Múltiples condiciones
                    val nota = 85
                    if (nota >= 90) {
                        println("Excelente")
                    } else if (nota >= 70) {
                        println("Bien")
                    } else {
                        println("Necesitas mejorar")
                    }
                    
                    // Expresión condicional
                    val mensaje = if (edad >= 18) "Adulto" else "Menor"
                    ```
                    
                    ### Operadores de comparación:
                    - `==` : igual a
                    - `!=` : diferente de
                    - `>` : mayor que
                    - `<` : menor que
                    - `>=` : mayor o igual
                    - `<=` : menor o igual
                """.trimIndent(),
                questions = listOf(
                    Question(
                        id = 7,
                        question = "¿Qué operador se usa para 'igual a' en Kotlin?",
                        options = listOf(
                            "=",
                            "==",
                            "===",
                            "equals"
                        ),
                        correctAnswer = 1,
                        explanation = "Se usa '==' para comparar igualdad."
                    ),
                    Question(
                        id = 8,
                        question = "¿Qué imprimirá este código si edad = 16?\nif (edad >= 18) println('Adulto') else println('Menor')",
                        options = listOf(
                            "Adulto",
                            "Menor",
                            "Nada",
                            "Error"
                        ),
                        correctAnswer = 1,
                        explanation = "Como 16 < 18, se ejecuta el else y imprime 'Menor'."
                    )
                ),
                xpReward = 35,
                timeEstimate = 6
            ),
            
            LessonContent(
                id = 5,
                title = "Bucles básicos",
                description = "Uso de for y while para repetir código",
                content = """
                    ## Bucles Básicos
                    
                    Los **bucles** permiten repetir código múltiples veces.
                    
                    ### Bucle for:
                    ```kotlin
                    // Iterar sobre un rango
                    for (i in 1..5) {
                        println(i)
                    }
                    
                    // Iterar sobre una lista
                    val frutas = listOf("manzana", "banana", "naranja")
                    for (fruta in frutas) {
                        println(fruta)
                    }
                    
                    // Con índice
                    for ((index, fruta) in frutas.withIndex()) {
                        println("\\$index: \\$fruta")
                    }
                    ```
                    
                    ### Bucle while:
                    ```kotlin
                    var contador = 0
                    while (contador < 5) {
                        println("Contador: \\$contador")
                        contador++
                    }
                    ```
                    
                    ### Bucle do-while:
                    ```kotlin
                    var numero = 0
                    do {
                        println("Número: \\$numero")
                        numero++
                    } while (numero < 3)
                    ```
                    
                    ### Control de bucles:
                    - `break`: Termina el bucle completamente
                    - `continue`: Salta a la siguiente iteración
                """.trimIndent(),
                questions = listOf(
                    Question(
                        id = 9,
                        question = "¿Qué imprimirá 'for (i in 1..3) println(i)'?",
                        options = listOf(
                            "1, 2, 3",
                            "0, 1, 2, 3",
                            "1, 2",
                            "3, 2, 1"
                        ),
                        correctAnswer = 0,
                        explanation = "El rango 1..3 incluye 1, 2 y 3."
                    ),
                    Question(
                        id = 10,
                        question = "¿Cuál es la diferencia entre 'while' y 'do-while'?",
                        options = listOf(
                            "No hay diferencia",
                            "'do-while' ejecuta al menos una vez",
                            "'while' es más rápido",
                            "'do-while' solo funciona con números"
                        ),
                        correctAnswer = 1,
                        explanation = "'do-while' garantiza al menos una ejecución antes de verificar la condición."
                    )
                ),
                xpReward = 40,
                timeEstimate = 7
            )
        )
    )
}

/**
 * Datos del contenido de un módulo
 */
data class ModuleContentData(
    val moduleId: Int,
    val moduleName: String,
    val description: String,
    val totalLessons: Int,
    val lessons: List<LessonContent>
)

/**
 * Contenido de una lección
 */
data class LessonContent(
    val id: Int,
    val title: String,
    val description: String,
    val content: String,
    val questions: List<Question>,
    val xpReward: Int,
    val timeEstimate: Int // en minutos
)

/**
 * Pregunta de una lección
 */
data class Question(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int, // índice de la respuesta correcta (0-based)
    val explanation: String
)
