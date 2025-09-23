package com.example.kodelearn.data.repository

import com.example.kodelearn.data.database.dao.*
import com.example.kodelearn.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
data class ModuleWithProgress(
    val module: Module,
    val progress: Progress?
)

class KodeLearnRepository(
    private val userDao: UserDao,
    private val courseDao: CourseDao,
    private val moduleDao: ModuleDao,
    private val progressDao: ProgressDao
) {
    // User operations
    fun getCurrentUser(): Flow<User?> = userDao.getCurrentUser()
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun updateHearts(hearts: Int) = userDao.updateHearts(hearts)
    suspend fun updateCoins(coins: Int) = userDao.updateCoins(coins)
    suspend fun updateXP(xp: Int) = userDao.updateXP(xp)
    suspend fun updateDailyStreak(streak: Int) = userDao.updateDailyStreak(streak)

    // Course operations
    fun getAllCourses(): Flow<List<Course>> = courseDao.getAllCourses()
    fun getCourseById(id: Int): Flow<Course?> = courseDao.getCourseById(id)
    suspend fun insertCourses(courses: List<Course>) = courseDao.insertCourses(courses)

    // Module operations
    fun getModulesByCourse(courseId: Int): Flow<List<Module>> = moduleDao.getModulesByCourse(courseId)
    suspend fun insertModules(modules: List<Module>) = moduleDao.insertModules(modules)

    // Progress operations
    fun getAllProgressByUser(userId: Int): Flow<List<Progress>> = progressDao.getAllProgressByUser(userId)
    fun getProgressByModule(userId: Int, moduleId: Int): Flow<Progress?> = progressDao.getProgressByModule(userId, moduleId)
    suspend fun insertProgressList(progressList: List<Progress>) = progressDao.insertProgressList(progressList)
    suspend fun updateModuleProgress(userId: Int, moduleId: Int, lessonsCompleted: Int, percentage: Float, isCompleted: Boolean) =
        progressDao.updateModuleProgress(userId, moduleId, lessonsCompleted, percentage, isCompleted)

    // Combined operations
    fun getModulesWithProgress(courseId: Int, userId: Int): Flow<List<ModuleWithProgress>> {
        return combine(
            moduleDao.getModulesByCourse(courseId),
            progressDao.getAllProgressByUser(userId)
        ) { modules, progressList ->
            modules.map { module ->
                val progress = progressList.find { it.moduleId == module.id }
                ModuleWithProgress(module, progress)
            }
        }
    }
}
