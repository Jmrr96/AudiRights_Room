package com.example.koalaappm13

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.example.koalaappm13.database.*
import com.example.koalaappm13.ui.KoalaAppM13Theme
import com.example.koalaappm13.ui.ProductionDetailScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductionDetailActivity : ComponentActivity() {

    private lateinit var viewModel: ProductionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val productionId = intent.getLongExtra("production_id", -1)
        val currentUsername = intent.getStringExtra("current_username") ?: ""

        val db = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val dao = db.productionDao()
        val userDao = db.userDao()
        val repository = ProductionRepository(dao)
        viewModel = ProductionViewModel(repository, currentUsername)

        setContent {
            KoalaAppM13Theme {
                if (productionId != -1L) {
                    var loaded by remember { mutableStateOf(false) }
                    var production by remember { mutableStateOf<Production?>(null) }
                    var currentRole by remember { mutableStateOf<String?>(null) }

                    // Cargar la producción
                    LaunchedEffect(productionId) {
                        viewModel.getProductionById(productionId).collectLatest {
                            production = it
                            loaded = true
                        }
                    }

                    // Obtener el rol del usuario
                    LaunchedEffect(currentUsername) {
                        val user = userDao.getUserByUsername(currentUsername)
                        currentRole = user?.role
                    }

                    if (loaded && production != null && currentRole != null) {
                        val isAdmin = currentRole == "admin"
                        val isEditor = currentRole == "editor"
                        val canValidate = isAdmin || isEditor
                        val canDelete = isAdmin // ❗ Solo admin

                        ProductionDetailScreen(
                            production = production!!,
                            viewModel = viewModel,
                            onDelete = {
                                if (canDelete) {
                                    lifecycleScope.launch {
                                        viewModel.deleteProduction(production!!)
                                        Toast.makeText(
                                            this@ProductionDetailActivity,
                                            "Producción eliminada",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    }
                                }
                            },
                            onEstadoChange = { newEstado ->
                                if (canValidate) {
                                    lifecycleScope.launch {
                                        viewModel.updateProduction(
                                            production!!.copy(
                                                estadoSesion = newEstado,
                                                estadoValidadoPor = currentUsername
                                            )
                                        )
                                        Toast.makeText(
                                            this@ProductionDetailActivity,
                                            "Estado actualizado correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            onBack = { finish() },
                            isEditable = canValidate,
                            showDeleteButton = canDelete // 👈 Se pasa explícitamente
                        )
                    } else if (loaded) {
                        Toast.makeText(this, "Producción no encontrada", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this, "ID de producción inválido", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
