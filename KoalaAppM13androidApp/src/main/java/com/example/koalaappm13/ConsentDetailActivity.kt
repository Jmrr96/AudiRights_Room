package com.example.koalaappm13

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.example.koalaappm13.database.AppDatabase
import com.example.koalaappm13.database.ConsentFormRepository
import com.example.koalaappm13.database.ConsentFormViewModel
import com.example.koalaappm13.ui.ConsentDetailScreen
import com.example.koalaappm13.ui.KoalaAppM13Theme
import kotlinx.coroutines.launch
import java.io.File

class ConsentDetailActivity : ComponentActivity() {

    private lateinit var viewModel: ConsentFormViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val consentId = intent.getLongExtra("consent_id", -1)

        sessionManager = SessionManager(this)
        val currentUsername = sessionManager.getCurrentUsername()

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val consentFormDao = database.consentFormDao()
        val productionDao = database.productionDao()

        val repository = ConsentFormRepository(
            consentFormDao = consentFormDao,
            productionDao = productionDao
        )

        viewModel = ConsentFormViewModel(repository, currentUsername)

        setContent {
            KoalaAppM13Theme {
                if (consentId != -1L) {
                    val consentForm by viewModel.getConsentFormById(consentId).collectAsState(initial = null)

                    consentForm?.let { form ->
                        ConsentDetailScreen(
                            consentForm = form,
                            onDelete = {
                                lifecycleScope.launch {
                                    viewModel.deleteConsentForm(form)
                                    Toast.makeText(this@ConsentDetailActivity, "Consentimiento eliminado", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            },
                            onExport = {
                                form.firmaPath?.let { path ->
                                    val file = File(path)
                                    if (file.exists()) {
                                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                        val nombreCompleto = "${form.nombre} ${form.apellidos}".replace(" ", "_")
                                        val success = exportImageToGallery(this, bitmap, "firma_$nombreCompleto.png")
                                        val msg = if (success) "Firma exportada con éxito" else "Error al exportar firma"
                                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    } ?: Text("Consentimiento no encontrado")
                } else {
                    Text("Error cargando consentimiento")
                }
            }
        }
    }
}
