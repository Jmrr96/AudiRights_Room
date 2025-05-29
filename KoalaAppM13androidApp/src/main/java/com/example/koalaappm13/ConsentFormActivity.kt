package com.example.koalaappm13

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.koalaappm13.database.AppDatabase
import com.example.koalaappm13.database.ConsentFormViewModel
import com.example.koalaappm13.database.ConsentFormRepository
import androidx.lifecycle.lifecycleScope
import com.example.koalaappm13.SessionManager
import com.example.koalaappm13.ui.ConsentFormScreen
import com.example.koalaappm13.ui.KoalaAppM13Theme

class ConsentFormActivity : ComponentActivity() {
    private lateinit var viewModel: ConsentFormViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        val currentUsername = sessionManager.getCurrentUsername()

        // Si no hay usuario logueado, redirigir al login
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Obtener la base de datos y los DAOs
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val consentFormDao = database.consentFormDao()
        val productionDao = database.productionDao()

        // Crear el repositorio con ambos DAOs
        val repository = ConsentFormRepository(
            consentFormDao = consentFormDao,
            productionDao = productionDao
        )

        viewModel = ConsentFormViewModel(repository, currentUsername)

        setContent {
            KoalaAppM13Theme {
                ConsentFormScreen(
                    viewModel = viewModel,
                    onConsentSaved = {
                        finish()
                    }
                )
            }
        }
    }
}