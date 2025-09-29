package com.example.kodelearn.data.learning

import com.example.kodelearn.data.content.Module
import com.example.kodelearn.data.content.ModuleContent
import com.example.kodelearn.data.content.Module2Content
import com.example.kodelearn.data.repository.KodeLearnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Servicio que maneja el desbloqueo de módulos
 */
class ModuleUnlockService(
    private val repository: KodeLearnRepository
) {
    
    /**
     * Obtiene todos los módulos con su estado de desbloqueo
     */
    suspend fun getAllModulesWithUnlockStatus(userId: Int): List<Module> {
        val modules = mutableListOf<Module>()
        
        // Módulo 1 - Siempre desbloqueado
        val module1Data = ModuleContent.introductionModule
        val module1 = Module(
            id = module1Data.moduleId,
            title = module1Data.moduleName,
            description = module1Data.description,
            difficulty = "Básico",
            estimatedTime = "30 minutos",
            isUnlocked = true,
            requiredModuleId = null,
            lessons = module1Data.lessons,
            totalLessons = module1Data.totalLessons,
            totalXP = module1Data.lessons.sumOf { it.xpReward },
            isCompleted = false
        )
        modules.add(module1)
        
        // Módulo 2 - Se desbloquea al completar el módulo 1
        val module1Progress = repository.getProgressByModule(userId, 1).first()
        val module1Completed = (module1Progress?.progressPercentage ?: 0f) >= 100f
        
        val module2 = Module2Content.variablesModule.copy(
            isUnlocked = module1Completed
        )
        modules.add(module2)
        
        return modules
    }
    
    /**
     * Verifica si un módulo está desbloqueado
     */
    suspend fun isModuleUnlocked(userId: Int, moduleId: Int): Boolean {
        return when (moduleId) {
            1 -> true // Módulo 1 siempre desbloqueado
            2 -> {
                // Módulo 2 se desbloquea al completar módulo 1
                val module1Progress = repository.getProgressByModule(userId, 1).first()
                module1Progress?.progressPercentage >= 100f
            }
            else -> false
        }
    }
    
    /**
     * Desbloquea un módulo específico
     */
    suspend fun unlockModule(userId: Int, moduleId: Int): Boolean {
        val isUnlocked = isModuleUnlocked(userId, moduleId)
        if (!isUnlocked) {
            // Aquí podrías implementar lógica adicional para desbloquear
            // Por ahora, el desbloqueo es automático basado en el progreso
            return false
        }
        return true
    }
    
    /**
     * Obtiene el progreso de desbloqueo para un módulo
     */
    suspend fun getUnlockProgress(userId: Int, moduleId: Int): UnlockProgress {
        return when (moduleId) {
            1 -> UnlockProgress(
                moduleId = 1,
                isUnlocked = true,
                requiredModuleId = null,
                requiredProgress = 0f,
                currentProgress = 100f,
                unlockMessage = "Módulo disponible desde el inicio"
            )
            2 -> {
                val module1Progress = repository.getProgressByModule(userId, 1).first()
                val currentProgress = module1Progress?.progressPercentage ?: 0f
                val isUnlocked = currentProgress >= 100f
                
                UnlockProgress(
                    moduleId = 2,
                    isUnlocked = isUnlocked,
                    requiredModuleId = 1,
                    requiredProgress = 100f,
                    currentProgress = currentProgress,
                    unlockMessage = if (isUnlocked) {
                        "¡Módulo desbloqueado! Completa el módulo anterior."
                    } else {
                        "Completa el 100% del módulo anterior para desbloquear este módulo."
                    }
                )
            }
            else -> UnlockProgress(
                moduleId = moduleId,
                isUnlocked = false,
                requiredModuleId = null,
                requiredProgress = 0f,
                currentProgress = 0f,
                unlockMessage = "Módulo no disponible"
            )
        }
    }
    
    /**
     * Obtiene el siguiente módulo a desbloquear
     */
    suspend fun getNextModuleToUnlock(userId: Int): Module? {
        val modules = getAllModulesWithUnlockStatus(userId)
        return modules.find { !it.isUnlocked }
    }
    
    /**
     * Verifica si hay módulos recién desbloqueados
     */
    suspend fun checkForNewlyUnlockedModules(userId: Int): List<Module> {
        val modules = getAllModulesWithUnlockStatus(userId)
        return modules.filter { it.isUnlocked }
    }
}

/**
 * Datos de progreso de desbloqueo de un módulo
 */
data class UnlockProgress(
    val moduleId: Int,
    val isUnlocked: Boolean,
    val requiredModuleId: Int?,
    val requiredProgress: Float,
    val currentProgress: Float,
    val unlockMessage: String
)
