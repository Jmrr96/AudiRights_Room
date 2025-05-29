package com.example.koalaappm13.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    private val sessionDao: SessionDao
) {
    private val securityInterceptor = SecurityInterceptor

    suspend fun insertUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.insertUser(user)
        }
    }

    suspend fun getAllUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            userDao.getAllUsers()
        }
    }

    suspend fun deleteUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.deleteUser(user)
        }
    }

    suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.updateUser(user)
        }
    }

    suspend fun login(username: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username)
        if (user == null) return@withContext false

        if (!securityInterceptor.checkUserAccess(username)) return@withContext false

        val isPasswordValid = User.hashPasswordWithSalt(password, user.salt) == user.passwordHash

        if (isPasswordValid) {
            securityInterceptor.resetFailedAttempts(username)
            userDao.updateLastLoginDate(username, System.currentTimeMillis())
            userDao.updateFailedLoginAttempts(username, 0)
            userDao.updateAccountLockStatus(username, false, null)

            sessionDao.insertSession(Session(username = username, timestamp = System.currentTimeMillis()))
            true
        } else {
            securityInterceptor.recordFailedAttempt(username)
            val attempts = (user.failedLoginAttempts + 1)
            userDao.updateFailedLoginAttempts(username, attempts)
            if (attempts >= 5) {
                val lockUntil = System.currentTimeMillis() + (30 * 60 * 1000)
                userDao.updateAccountLockStatus(username, true, lockUntil)
            }
            false
        }
    }

    suspend fun findUserByEmailAndDni(email: String, dni: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.findUserByEmailAndDni(email, dni)
        }
    }

    suspend fun getUserById(userId: Int): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(userId)
        }
    }

    suspend fun getUserByUsername(username: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByUsername(username)
        }
    }

    suspend fun updatePassword(username: String, newPassword: String) {
        withContext(Dispatchers.IO) {
            val user = userDao.getUserByUsername(username)
            user?.let {
                val hashed = User.hashPasswordWithSalt(newPassword, it.salt)
                userDao.updatePassword(username, hashed)
            }
        }
    }
}
