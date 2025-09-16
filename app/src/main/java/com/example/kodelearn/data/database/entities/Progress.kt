package com.example.kodelearn.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress")
data class Progress(
    @PrimaryKey val id: Int,
    val userId: Int,
    val moduleId: Int,
    val lessonsCompleted: Int = 0,
    val progressPercentage: Float = 0f,
    val isCompleted: Boolean = false,
    val lastAccessedAt: Long = System.currentTimeMillis()
)
