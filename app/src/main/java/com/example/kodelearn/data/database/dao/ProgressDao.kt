package com.example.kodelearn.data.database.dao

import androidx.room.*
import com.example.kodelearn.data.database.entities.Progress
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM progress WHERE userId = :userId AND moduleId = :moduleId")
    fun getProgressByModule(userId: Int, moduleId: Int): Flow<Progress?>

    @Query("SELECT * FROM progress WHERE userId = :userId")
    fun getAllProgressByUser(userId: Int): Flow<List<Progress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: Progress)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressList(progressList: List<Progress>)

    @Update
    suspend fun updateProgress(progress: Progress)

    @Query("UPDATE progress SET lessonsCompleted = :lessonsCompleted, progressPercentage = :percentage, isCompleted = :isCompleted WHERE userId = :userId AND moduleId = :moduleId")
    suspend fun updateModuleProgress(userId: Int, moduleId: Int, lessonsCompleted: Int, percentage: Float, isCompleted: Boolean)
}
