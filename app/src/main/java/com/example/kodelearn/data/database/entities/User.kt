package com.example.kodelearn.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val biography: String = "",
    val dailyStreak: Int = 0,
    val totalXP: Int = 0,
    val league: String = "Wooden",
    val avatarUrl: String = "",
    val hearts: Int = 5,
    val coins: Int = 0
)
