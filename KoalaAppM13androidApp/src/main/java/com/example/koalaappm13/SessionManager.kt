package com.example.koalaappm13

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("KoalaAppPrefs", Context.MODE_PRIVATE)

    fun saveUser(username: String, isAdmin: Boolean) {
        with(prefs.edit()) {
            putString("username", username)
            putBoolean("isAdmin", isAdmin)
            apply()
        }
    }

    fun getUser(): String? {
        return prefs.getString("username", null)
    }

    fun isAdmin(): Boolean {
        return prefs.getBoolean("isAdmin", false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getUser() != null
    }

    fun getCurrentUsername(): String {
        return getUser() ?: ""
    }
}
