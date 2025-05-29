package com.example.koalaappm13.database

import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun loginUser(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email AND dni = :dni LIMIT 1")
    suspend fun findUserByEmailAndDni(email: String, dni: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET lastLoginDate = :timestamp WHERE username = :username")
    suspend fun updateLastLoginDate(username: String, timestamp: Long)

    @Query("UPDATE users SET failedLoginAttempts = :attempts WHERE username = :username")
    suspend fun updateFailedLoginAttempts(username: String, attempts: Int)

    @Query("UPDATE users SET accountLocked = :locked, accountLockedUntil = :lockedUntil WHERE username = :username")
    suspend fun updateAccountLockStatus(username: String, locked: Boolean, lockedUntil: Long?)

    // ✅ NUEVA: Actualizar la contraseña cifrada directamente
    @Query("UPDATE users SET passwordHash = :hashedPassword WHERE username = :username")
    suspend fun updatePassword(username: String, hashedPassword: String)
}
