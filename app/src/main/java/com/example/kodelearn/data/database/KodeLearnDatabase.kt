package com.example.kodelearn.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.kodelearn.data.database.dao.*
import com.example.kodelearn.data.database.entities.*

@Database(
    entities = [
        User::class,
        Course::class,
        Module::class,
        Progress::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KodeLearnDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun moduleDao(): ModuleDao
    abstract fun progressDao(): ProgressDao

    companion object {
        @Volatile
        private var INSTANCE: KodeLearnDatabase? = null

        fun getDatabase(context: Context): KodeLearnDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KodeLearnDatabase::class.java,
                    "kodelearn_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
