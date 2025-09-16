package com.example.kodelearn.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modules")
data class Module(
    @PrimaryKey val id: Int,
    val courseId: Int,
    val name: String,
    val description: String,
    val totalLessons: Int,
    val order: Int,
    val isLocked: Boolean = true,
    val xpReward: Int = 10
)
