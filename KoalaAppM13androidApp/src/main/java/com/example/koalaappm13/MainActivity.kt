package com.example.koalaappm13

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.koalaappm13.database.UserViewModel
import com.example.koalaappm13.database.UserViewModelFactory
import com.example.koalaappm13.ui.ComposeLoginSection
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var userViewModel: UserViewModel
    private var showLogin by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            navigateToAppropriateScreen()
            return
        }

        userViewModel = ViewModelProvider(
            this, UserViewModelFactory((application as KoalaApp).userRepository)
        )[UserViewModel::class.java]

        findViewById<ComposeView>(R.id.composeView).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                WelcomeScreen(
                    onAnonymousStart = { navigateToCreateSession() },
                    onLoginClick = { showLogin = true }
                )
            }
        }
    }

    @Composable
    private fun WelcomeScreen(
        onAnonymousStart: () -> Unit,
        onLoginClick: () -> Unit
    ) {
        if (showLogin) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center
            ) {
                ComposeLoginSection(
                    onLogin = { username, password -> handleLogin(username, password) },
                    onForgotPasswordClick = { navigateToRecoverActivity() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showLogin = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF6C63FF))
                ) {
                    Text("Volver", color = androidx.compose.ui.graphics.Color.White)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo/avatar grande
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Person,
                    contentDescription = "Logo",
                    tint = androidx.compose.ui.graphics.Color(0xFF6C63FF),
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            color = androidx.compose.ui.graphics.Color(0xFFE0E7FF),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(45.dp)
                        )
                        .padding(18.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onAnonymousStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF6C63FF))
                ) {
                    Text("Crear nueva cesión de derechos", color = androidx.compose.ui.graphics.Color.White, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF3A3A6A))
                ) {
                    Text("Iniciar sesión", color = androidx.compose.ui.graphics.Color.White, fontSize = 18.sp)
                }
            }
        }
    }

    private fun handleLogin(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Usuario y contraseña requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        val loadingToast = Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT)
        loadingToast.show()

        userViewModel.getUserByUsername(username) { user ->
            runOnUiThread {
                loadingToast.cancel()
                if (user != null && user.verifyPassword(password)) {
                    when (user.role.lowercase()) {
                        "admin" -> {
                            sessionManager.saveUser(user.username, true)
                            navigateTo(AdminPanelActivity::class.java)
                        }
                        "editor" -> {
                            sessionManager.saveUser(user.username, false)
                            navigateTo(HomeActivity::class.java)
                        }
                        else -> {
                            Toast.makeText(this, "Este usuario no tiene acceso permitido.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateTo(target: Class<*>) {
        val intent = Intent(this, target)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToAppropriateScreen() {
        val intent = if (sessionManager.isAdmin()) {
            Intent(this, AdminPanelActivity::class.java)
        } else {
            Intent(this, HomeActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToRecoverActivity() {
        startActivity(Intent(this, RecoverActivity::class.java))
    }

    private fun navigateToCreateSession() {
        startActivity(Intent(this, CreateProductionActivity::class.java))
    }
}
