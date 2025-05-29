package com.example.koalaappm13.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    fun insertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertUser(user)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getAllUsers(callback: (List<User>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val users = repository.getAllUsers()
                withContext(Dispatchers.Main) {
                    callback(users)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(emptyList())
                }
            }
        }
    }

    fun deleteUser(user: User, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteUser(user)
                withContext(Dispatchers.Main) {
                    callback()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUser(user: User, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateUser(user)
                withContext(Dispatchers.Main) {
                    callback()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun login(username: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = repository.getUserByUsername(username)
                val success = user?.verifyPassword(password) == true
                withContext(Dispatchers.Main) {
                    callback(success)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }

    fun findUserByEmailAndDni(email: String, dni: String, callback: (User?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = repository.findUserByEmailAndDni(email, dni)
                withContext(Dispatchers.Main) {
                    callback(user)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(null)
                }
            }
        }
    }

    fun getUserById(userId: Int, callback: (User?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = repository.getUserById(userId)
                withContext(Dispatchers.Main) {
                    callback(user)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(null)
                }
            }
        }
    }

    fun getUserByUsername(username: String, callback: (User?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = repository.getUserByUsername(username)
                withContext(Dispatchers.Main) {
                    callback(user)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(null)
                }
            }
        }
    }

    // ✅ CAMBIAR CONTRASEÑA
    fun changePassword(user: User, newPassword: String, callback: (Boolean) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val salt = User.generateSalt()
                val hashedPassword = User.hashPasswordWithSalt(newPassword, salt)
                val updatedUser = user.copy(
                    passwordHash = hashedPassword,
                    salt = salt,
                    lastPasswordChange = System.currentTimeMillis()
                )
                repository.updateUser(updatedUser)
                withContext(Dispatchers.Main) {
                    callback(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }
}
