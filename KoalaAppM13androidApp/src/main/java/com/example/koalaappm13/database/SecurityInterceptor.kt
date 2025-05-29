package com.example.koalaappm13.database

import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object SecurityInterceptor {
    private const val MAX_FAILED_ATTEMPTS = 5
    private const val LOCK_DURATION_MINUTES = 30L
    private val failedAttempts = ConcurrentHashMap<String, Int>()
    private val lockedAccounts = ConcurrentHashMap<String, Long>()

    fun checkUserAccess(username: String): Boolean {
        val lockUntil = lockedAccounts[username]
        if (lockUntil != null) {
            if (System.currentTimeMillis() < lockUntil) {
                return false
            }
            lockedAccounts.remove(username)
            failedAttempts.remove(username)
        }
        return true
    }

    fun recordFailedAttempt(username: String) {
        val attempts = failedAttempts.compute(username) { _, count ->
            (count ?: 0) + 1
        } ?: 1

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            val lockUntil = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(LOCK_DURATION_MINUTES)
            lockedAccounts[username] = lockUntil
            Log.w("Security", "Account locked: $username until ${lockUntil}")
        }
    }

    fun resetFailedAttempts(username: String) {
        failedAttempts.remove(username)
        lockedAccounts.remove(username)
    }

    fun validateOperation(username: String, operation: String): Boolean {
        return when (operation) {
            "delete" -> checkUserAccess(username)
            "update" -> checkUserAccess(username)
            else -> true
        }
    }
} 