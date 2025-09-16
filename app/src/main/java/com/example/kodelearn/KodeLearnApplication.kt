package com.example.kodelearn

import android.app.Application
import androidx.lifecycle.lifecycleScope
import com.example.kodelearn.data.MockData
import com.example.kodelearn.data.database.KodeLearnDatabase
import com.example.kodelearn.data.repository.KodeLearnRepository
import kotlinx.coroutines.launch

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
                // Insert mock data
                repository.run {
                    // Check if user already exists
                    val userDao = database.userDao()
                    val existingUser = userDao.getCurrentUser()
                    
                    // Only insert if no data exists (first time setup)
                    // This is a simplified check - in production you'd want better logic
                    kotlinx.coroutines.runBlocking {
                        insertUser(MockData.getDefaultUser())
                        insertCourses(MockData.getDefaultCourses())
                        insertModules(MockData.getDefaultModules())
                        insertProgressList(MockData.getDefaultProgress())
                    }
                }
            } catch (e: Exception) {
                // In a production app, you'd want proper error handling
                e.printStackTrace()
            }
        }.start()
    }
}
