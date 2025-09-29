package com.example.kodelearn.data.content

import com.example.kodelearn.data.content.LessonContent
import com.example.kodelearn.data.content.Question

/**
 * Contenido del Módulo 2: Declaración de variables y tipos de datos
 * Se desbloquea al completar el Módulo 1
 */
object Module2Content {
    
    val variablesModule = Module(
        id = 2,
        title = "Declaración de Variables y Tipos de Datos",
        description = "Aprende a declarar variables y trabajar con diferentes tipos de datos en Kotlin",
        difficulty = "Intermedio",
        estimatedTime = "45 minutos",
        isUnlocked = false, // Se desbloquea al completar el módulo 1
        requiredModuleId = 1, // Requiere completar el módulo 1
        lessons = listOf(
            LessonContent(
                id = 6,
                title = "Variables inmutables (val) y mutables (var)",
                description = "Diferencia entre val y var en Kotlin",
                content = """
                    ## Variables en Kotlin
                    
                    En Kotlin, las variables se declaran usando `val` o `var`:
                    
                    ### Variables Inmutables (val):
                    ```kotlin
                    val nombre = "Juan"
                    val edad = 25
                    val esEstudiante = true
                    ```
                    
                    - **No se pueden reasignar** después de la inicialización
                    - Son **thread-safe** por defecto
                    - Recomendadas para la mayoría de casos
                    
                    ### Variables Mutables (var):
                    ```kotlin
                    var contador = 0
                    var precio = 19.99
                    var activo = false
                    ```
                    
                    - **Se pueden reasignar** múltiples veces
                    - Útiles cuando el valor cambia durante la ejecución
                    - Requieren más cuidado con la concurrencia
                    
                    ### Ejemplos Prácticos:
                    ```kotlin
                    // Correcto - val para valores que no cambian
                    val PI = 3.14159
                    val nombreUsuario = "admin"
                    
                    // Correcto - var para valores que cambian
                    var temperatura = 20
                    temperatura = 25 // OK
                    
                    // Error - no se puede reasignar val
                    val numero = 10
                    // numero = 20 // Error de compilación
                    ```
                """.trimIndent(),
                questions = listOf(
                    Question(
                        id = 11,
                        question = "¿Cuál es la diferencia principal entre 'val' y 'var'?",
                        options = listOf(
                            "val es más rápido que var",
                            "val no se puede reasignar, var sí",
                            "var solo funciona con números",
                            "No hay diferencia"
                        ),
                        correctAnswer = 1,
                        explanation = "val crea variables inmutables que no se pueden reasignar, mientras que var crea variables mutables que sí se pueden reasignar."
                    ),
                    Question(
                        id = 12,
                        question = "¿Cuál de estos códigos es correcto?",
                        options = listOf(
                            "val x = 5; x = 10",
                            "var y = 5; y = 10",
                            "val z; z = 5",
                            "var w; w = 5"
                        ),
                        correctAnswer = 1,
                        explanation = "var permite reasignación, por lo que 'var y = 5; y = 10' es correcto."
                    )
                ),
                xpReward = 20,
                timeEstimate = 8
            ),
            
            LessonContent(
                id = 7,
                title = "Tipos de datos básicos",
                description = "Int, String, Boolean, Double y más",
                content = """
                    ## Tipos de Datos Básicos en Kotlin
                    
                    Kotlin tiene varios tipos de datos integrados:
                    
                    ### Números Enteros:
                    ```kotlin
                    val byte: Byte = 127        // 8 bits (-128 a 127)
                    val short: Short = 32767    // 16 bits (-32,768 a 32,767)
                    val int: Int = 2147483647   // 32 bits (-2^31 a 2^31-1)
                    val long: Long = 9223372036854775807L // 64 bits
                    ```
                    
                    ### Números Decimales:
                    ```kotlin
                    val float: Float = 3.14f    // 32 bits
                    val double: Double = 3.141592653589793 // 64 bits
                    ```
                    
                    ### Texto:
                    ```kotlin
                    val char: Char = 'A'        // Un solo carácter
                    val string: String = "Hola Mundo" // Cadena de texto
                    ```
                    
                    ### Lógico:
                    ```kotlin
                    val boolean: Boolean = true // true o false
                    ```
                    
                    ### Inferencia de Tipos:
                    ```kotlin
                    // Kotlin puede inferir el tipo automáticamente
                    val numero = 42        // Int
                    val decimal = 3.14     // Double
                    val texto = "Hola"     // String
                    val activo = true      // Boolean
                    ```
                    
                    ### Conversiones de Tipo:
                    ```kotlin
                    val intValue = 42
                    val longValue = intValue.toLong()
                    val stringValue = intValue.toString()
                    val doubleValue = intValue.toDouble()
                    ```
                """.trimIndent(),
                questions = listOf(
                    Question(
                        id = 13,
                        question = "¿Cuál es el tipo de dato para números enteros de 32 bits?",
                        options = listOf(
                            "Byte",
                            "Short", 
                            "Int",
                            "Long"
                        ),
                        correctAnswer = 2,
                        explanation = "Int es el tipo de dato para números enteros de 32 bits en Kotlin."
                    ),
                    Question(
                        id = 14,
                        question = "¿Cómo se declara un número decimal en Kotlin?",
                        options = listOf(
                            "val x = 3.14",
                            "val x = 3.14f",
                            "val x = 3.14L",
                            "Todas las anteriores"
                        ),
                        correctAnswer = 0,
                        explanation = "Por defecto, los números decimales son Double. Para Float se necesita la 'f' al final."
                    )
                ),
                xpReward = 25,
                timeEstimate = 10
            ),
            
            LessonContent(
                id = 8,
                title = "Strings y interpolación",
                description = "Trabajar con cadenas de texto",
                content = """
                    ## Strings en Kotlin
                    
                    Los strings son secuencias de caracteres inmutables.
                    
                    ### Declaración de Strings:
                    ```kotlin
                    val nombre = "Juan"
                    val apellido = "Pérez"
                    val mensaje = "Hola Mundo"
                    ```
                    
                    ### Concatenación:
                    ```kotlin
                    val nombreCompleto = "Juan" + " " + "Pérez"
                    val saludo = "Hola " + "Juan"
                    ```
                    
                    ### Interpolación de Strings (String Templates):
                    ```kotlin
                    val edad = 25
                    val mensaje = "Hola, me llamo Juan y tengo 25 años"
                    val resultado = "El resultado es: 5"
                    ```
                    
                    ### Operaciones con Strings:
                    ```kotlin
                    val texto = "  Hola Mundo  "
                    
                    // Longitud
                    val longitud = texto.length
                    
                    // Eliminar espacios
                    val sinEspacios = texto.trim()
                    
                    // Convertir a mayúsculas/minúsculas
                    val mayusculas = texto.uppercase()
                    val minusculas = texto.lowercase()
                    
                    // Substring
                    val subcadena = texto.substring(2, 7)
                    
                    // Dividir
                    val palabras = "uno,dos,tres".split(",")
                    ```
                    
                    ### Strings Multilínea:
                    ```kotlin
                    val mensajeLargo = "Este es un mensaje que ocupa múltiples líneas"
                    ```
                    
                    ### Comparación de Strings:
                    ```kotlin
                    val str1 = "Hola"
                    val str2 = "Hola"
                    
                    // Comparación de contenido
                    val sonIguales = str1 == str2  // true
                    
                    // Comparación de referencia
                    val mismaReferencia = str1 === str2  // false
                    ```
                """.trimIndent(),
                questions = listOf(
                    Question(
                        id = 15,
                        question = "¿Cuál es la forma correcta de interpolar variables en strings?",
                        options = listOf(
                            "val mensaje = 'Hola $nombre'",
                            "val mensaje = \"Hola $nombre\"",
                            "val mensaje = 'Hola + nombre'",
                            "val mensaje = \"Hola + nombre\""
                        ),
                        correctAnswer = 1,
                        explanation = "La interpolación de strings usa comillas dobles y el símbolo $ para variables."
                    ),
                    Question(
                        id = 16,
                        question = "¿Qué hace el método trim() en un string?",
                        options = listOf(
                            "Convierte a mayúsculas",
                            "Elimina espacios al inicio y final",
                            "Divide el string en partes",
                            "Convierte a minúsculas"
                        ),
                        correctAnswer = 1,
                        explanation = "trim() elimina los espacios en blanco al inicio y final del string."
                    )
                ),
                xpReward = 30,
                timeEstimate = 12
            ),
            
            LessonContent(
                id = 9,
                title = "Arrays y Listas",
                description = "Colecciones de datos ordenadas",
                content = """
                    ## Arrays y Listas en Kotlin
                    
                    ### Arrays (Tamaño fijo):
                    ```kotlin
                    // Declaración
                    val numeros = arrayOf(1, 2, 3, 4, 5)
                    val nombres = arrayOf("Ana", "Luis", "María")
                    
                    // Acceso a elementos
                    val primerNumero = numeros[0]
                    val ultimoNombre = nombres[nombres.size - 1]
                    
                    // Modificar elementos
                    numeros[0] = 10
                    ```
                    
                    ### Listas (Tamaño variable):
                    ```kotlin
                    // Lista inmutable
                    val frutas = listOf("manzana", "banana", "naranja")
                    
                    // Lista mutable
                    val colores = mutableListOf("rojo", "verde", "azul")
                    colores.add("amarillo")
                    colores.remove("verde")
                    
                    // Acceso y modificación
                    val primerColor = colores[0]
                    colores[1] = "morado"
                    ```
                    
                    ### Operaciones Comunes:
                    ```kotlin
                    val numeros = listOf(1, 2, 3, 4, 5)
                    
                    // Tamaño
                    val cantidad = numeros.size
                    
                    // Verificar si está vacía
                    val vacia = numeros.isEmpty()
                    
                    // Buscar elemento
                    val contiene = numeros.contains(3)
                    val indice = numeros.indexOf(3)
                    
                    // Filtrar
                    val pares = numeros.filter { it % 2 == 0 }
                    
                    // Transformar
                    val dobles = numeros.map { it * 2 }
                    
                    // Reducir
                    val suma = numeros.sum()
                    val producto = numeros.reduce { acc, num -> acc * num }
                    ```
                    
                    ### Iteración:
                    ```kotlin
                    val frutas = listOf("manzana", "banana", "naranja")
                    
                    // For tradicional
                    for (fruta in frutas) {
                        println(fruta)
                    }
                    
                    // ForEach
                    frutas.forEach { fruta ->
                        println(fruta)
                    }
                    
                    // Con índice
                    for ((indice, fruta) in frutas.withIndex()) {
                        println("0: manzana")
                    }
                    ```
                """.trimIndent(),
                questions = listOf(
                    Question(
                        id = 17,
                        question = "¿Cuál es la diferencia principal entre Array y List?",
                        options = listOf(
                            "Array es más rápido que List",
                            "Array tiene tamaño fijo, List es variable",
                            "List solo funciona con números",
                            "No hay diferencia"
                        ),
                        correctAnswer = 1,
                        explanation = "Array tiene un tamaño fijo definido al crear, mientras que List puede cambiar de tamaño dinámicamente."
                    ),
                    Question(
                        id = 18,
                        question = "¿Cómo se agrega un elemento a una lista mutable?",
                        options = listOf(
                            "lista[0] = elemento",
                            "lista.add(elemento)",
                            "lista.insert(elemento)",
                            "lista.append(elemento)"
                        ),
                        correctAnswer = 1,
                        explanation = "Se usa el método add() para agregar elementos a una lista mutable."
                    )
                ),
                xpReward = 35,
                timeEstimate = 15
            ),
            
            LessonContent(
                id = 10,
                title = "Null Safety",
                description = "Manejo seguro de valores nulos",
                content = """
                    ## Null Safety en Kotlin
                    
                    Kotlin previene errores de NullPointerException con null safety.
                    
                    ### Tipos Nullables:
                    ```kotlin
                    // Tipo no nullable (por defecto)
                    val nombre: String = "Juan"  // No puede ser null
                    
                    // Tipo nullable
                    val apellido: String? = null  // Puede ser null
                    val edad: Int? = null
                    ```
                    
                    ### Verificación de Null:
                    ```kotlin
                    val texto: String? = "Hola"
                    
                    // Verificación explícita
                    if (texto != null) {
                        println(texto.length)  // Safe call
                    }
                    
                    // Operador de llamada segura
                    val longitud = texto?.length  // Retorna null si texto es null
                    
                    // Operador Elvis (?:)
                    val longitudSegura = texto?.length ?: 0  // 0 si texto es null
                    ```
                    
                    ### Operador de Aserción No-Null (!!):
                    ```kotlin
                    val texto: String? = "Hola"
                    val longitud = texto!!.length  // Lanza excepción si es null
                    ```
                    
                    ### Let para Null Safety:
                    ```kotlin
                    val texto: String? = "Hola"
                    
                    texto?.let { 
                        println("El texto es: Hola")
                        println("Longitud: 4")
                    }
                    ```
                    
                    ### Inicialización Tardía (lateinit):
                    ```kotlin
                    class MiClase {
                        lateinit var nombre: String
                        
                        fun inicializar() {
                            nombre = "Juan"  // Debe inicializarse antes de usar
                        }
                        
                        fun usar() {
                            if (::nombre.isInitialized) {
                                println(nombre)
                            }
                        }
                    }
                    ```
                    
                    ### Ejemplos Prácticos:
                    ```kotlin
                    fun procesarTexto(texto: String?) {
                        // Método 1: Verificación explícita
                        if (texto != null && texto.isNotEmpty()) {
                            println("Texto: HOLA")
                        }
                        
                        // Método 2: Operador Elvis
                        val textoProcesado = texto?.uppercase() ?: "Texto vacío"
                        println("Resultado: HOLA")
                        
                        // Método 3: Let
                        texto?.let { t ->
                            if (t.isNotEmpty()) {
                                println("Procesando: Hola")
                            }
                        }
                    }
                    ```
                """.trimIndent(),
                questions = listOf(
                    Question(
                        id = 19,
                        question = "¿Qué operador se usa para llamadas seguras en Kotlin?",
                        options = listOf(
                            "!",
                            "?",
                            "!!",
                            "??"
                        ),
                        correctAnswer = 1,
                        explanation = "El operador ? se usa para llamadas seguras, evitando NullPointerException."
                    ),
                    Question(
                        id = 20,
                        question = "¿Qué hace el operador Elvis (?:)?",
                        options = listOf(
                            "Convierte a mayúsculas",
                            "Proporciona un valor por defecto si es null",
                            "Elimina espacios",
                            "Divide strings"
                        ),
                        correctAnswer = 1,
                        explanation = "El operador Elvis (?:) proporciona un valor por defecto cuando la expresión es null."
                    )
                ),
                xpReward = 40,
                timeEstimate = 18
            )
        ),
        totalLessons = 5,
        totalXP = 150,
        isCompleted = false
    )
}