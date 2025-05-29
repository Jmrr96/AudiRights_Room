package com.example.koalaappm13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.koalaappm13.database.UserViewModel
import com.example.koalaappm13.database.UserViewModelFactory
import com.example.koalaappm13.ui.ChangePasswordScreen
import com.example.koalaappm13.ui.KoalaAppM13Theme

class ChangePasswordActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as KoalaApp
        val sessionManager = SessionManager(this)
        val currentUsername = sessionManager.getCurrentUsername()

        setContent {
            KoalaAppM13Theme {
                val userViewModel: UserViewModel = viewModel(
                    factory = UserViewModelFactory(app.userRepository)
                )

                ChangePasswordScreen(
                    viewModel = userViewModel,
                    currentUsername = currentUsername,
                    onBack = { finish() }
                )
            }
        }
    }
}
