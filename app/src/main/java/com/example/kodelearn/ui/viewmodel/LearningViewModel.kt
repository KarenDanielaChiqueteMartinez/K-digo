package com.example.kodelearn.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kodelearn.data.database.entities.Course
import com.example.kodelearn.data.database.entities.User
import com.example.kodelearn.data.repository.KodeLearnRepository
import com.example.kodelearn.data.repository.ModuleWithProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class LearningUiState(
    val user: User? = null,
    val currentCourse: Course? = null,
    val modulesWithProgress: List<ModuleWithProgress> = emptyList(),
    val isLoading: Boolean = true
)

class LearningViewModel(
    private val repository: KodeLearnRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()

    private val currentCourseId = 1 // Python course ID

    init {
        loadLearningData()
    }

    private fun loadLearningData() {
        viewModelScope.launch {
            combine(
                repository.getCurrentUser(),
                repository.getCourseById(currentCourseId),
                repository.getModulesWithProgress(currentCourseId, 1) // User ID 1
            ) { user, course, modulesWithProgress ->
                _uiState.value = _uiState.value.copy(
                    user = user,
                    currentCourse = course,
                    modulesWithProgress = modulesWithProgress,
                    isLoading = false
                )
            }.collect { }
        }
    }

    fun startModule(moduleId: Int) {
        viewModelScope.launch {
            // Logic to start a module
            // This would navigate to lesson screen (not implemented in this base)
        }
    }

    fun completeLesson(moduleId: Int, lessonNumber: Int) {
        viewModelScope.launch {
            val module = _uiState.value.modulesWithProgress
                .find { it.module.id == moduleId }?.module ?: return@launch

            val currentProgress = _uiState.value.modulesWithProgress
                .find { it.module.id == moduleId }?.progress

            val newLessonsCompleted = (currentProgress?.lessonsCompleted ?: 0) + 1
            val percentage = (newLessonsCompleted.toFloat() / module.totalLessons) * 100f
            val isCompleted = newLessonsCompleted >= module.totalLessons

            repository.updateModuleProgress(
                userId = 1,
                moduleId = moduleId,
                lessonsCompleted = newLessonsCompleted,
                percentage = percentage,
                isCompleted = isCompleted
            )

            // Update user XP
            _uiState.value.user?.let { user ->
                repository.updateXP(user.totalXP + 10)
            }
        }
    }

    companion object {
        fun factory(repository: KodeLearnRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LearningViewModel(repository) as T
                }
            }
        }
    }
}
