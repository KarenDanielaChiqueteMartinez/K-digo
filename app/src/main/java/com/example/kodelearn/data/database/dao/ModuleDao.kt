package com.example.kodelearn.data.database.dao

import androidx.room.*
import com.example.kodelearn.data.database.entities.Module
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleDao {
    @Query("SELECT * FROM modules WHERE courseId = :courseId ORDER BY `order` ASC")
    fun getModulesByCourse(courseId: Int): Flow<List<Module>>

    @Query("SELECT * FROM modules WHERE id = :id")
    fun getModuleById(id: Int): Flow<Module?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: Module)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<Module>)

    @Update
    suspend fun updateModule(module: Module)
}
