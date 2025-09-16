package com.example.kodelearn.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val iconUrl: String = "",
    val totalModules: Int = 0,
    val isLocked: Boolean = false
)
