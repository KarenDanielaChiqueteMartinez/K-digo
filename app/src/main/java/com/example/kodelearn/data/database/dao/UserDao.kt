package com.example.kodelearn.data.database.dao

import androidx.room.*
import com.example.kodelearn.data.database.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1")
    fun getCurrentUser(): Flow<User?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET hearts = :hearts WHERE id = 1")
    suspend fun updateHearts(hearts: Int)

    @Query("UPDATE users SET coins = :coins WHERE id = 1")
    suspend fun updateCoins(coins: Int)

    @Query("UPDATE users SET totalXP = :xp WHERE id = 1")
    suspend fun updateXP(xp: Int)

    @Query("UPDATE users SET dailyStreak = :streak WHERE id = 1")
    suspend fun updateDailyStreak(streak: Int)
}
