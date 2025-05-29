package com.example.koalaappm13.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true),
        Index(value = ["dni"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val email: String,
    val dni: String,
    val salt: ByteArray,
    val securityToken: String,
    val lastPasswordChange: Long = System.currentTimeMillis(),
    val failedLoginAttempts: Int = 0,
    val accountLocked: Boolean = false,
    val accountLockedUntil: Long? = null,
    val lastLoginDate: Long? = null,
    val created_at: Long = System.currentTimeMillis(),
    val role: String = "usuario" // 👈 Campo para roles
) {
    fun verifyPassword(password: String): Boolean {
        return passwordHash.isNotEmpty() && hashPasswordWithSalt(password, salt) == passwordHash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (username != other.username) return false
        if (email != other.email) return false
        if (dni != other.dni) return false
        if (!salt.contentEquals(other.salt)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + dni.hashCode()
        result = 31 * result + salt.contentHashCode()
        return result
    }

    companion object {
        private const val SALT_LENGTH = 16
        private const val ITERATIONS = 10000
        private const val KEY_LENGTH = 256

        fun generateSalt(): ByteArray {
            val random = SecureRandom()
            val salt = ByteArray(SALT_LENGTH)
            random.nextBytes(salt)
            return salt
        }

        fun hashPasswordWithSalt(password: String, salt: ByteArray): String {
            val spec = PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
            )
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val hash = factory.generateSecret(spec).encoded
            return hash.joinToString("") { "%02x".format(it) }
        }

        fun createUser(
            username: String,
            password: String,
            email: String,
            dni: String,
            role: String = "usuario"
        ): User {
            val salt = generateSalt()
            val hashedPassword = if (password.isNotEmpty()) {
                hashPasswordWithSalt(password, salt)
            } else {
                hashPasswordWithSalt("defaultPassword", salt)
            }

            return User(
                username = username,
                passwordHash = hashedPassword,
                email = email,
                dni = dni,
                salt = salt,
                securityToken = UUID.randomUUID().toString(),
                lastPasswordChange = System.currentTimeMillis(),
                failedLoginAttempts = 0,
                accountLocked = false,
                accountLockedUntil = null,
                lastLoginDate = System.currentTimeMillis(),
                created_at = System.currentTimeMillis(),
                role = role
            )
        }
    }
}
