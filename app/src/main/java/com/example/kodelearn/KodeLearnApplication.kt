package com.example.kodelearn

import android.app.Application
import com.example.kodelearn.data.MockData
import com.example.kodelearn.data.database.KodeLearnDatabase
import com.example.kodelearn.data.repository.KodeLearnRepository
import kotlinx.coroutines.runBlocking

class KodeLearnApplication : Application() {
    
    val database by lazy { KodeLearnDatabase.getDatabase(this) }
    val repository by lazy { 
        KodeLearnRepository(
            userDao = database.userDao(),
            courseDao = database.courseDao(),
            moduleDao = database.moduleDao(),
            progressDao = database.progressDao()
        )
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize with mock data
        initializeMockData()
    }
    
    private fun initializeMockData() {
        // Use a simple thread to avoid complexity in this base implementation
        Thread {
            try {
                // Insert mock data using runBlocking
                runBlocking {
                    repository.insertUser(MockData.getDefaultUser())
                    repository.insertCourses(MockData.getDefaultCourses())
                    repository.insertModules(MockData.getDefaultModules())
                    repository.insertProgressList(MockData.getDefaultProgress())
                }
            } catch (e: Exception) {
                // In a production app, you'd want proper error handling
                e.printStackTrace()
            }
        }.start()
    }
}
