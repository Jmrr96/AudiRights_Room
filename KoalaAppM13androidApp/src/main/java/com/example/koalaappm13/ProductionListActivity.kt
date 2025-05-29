package com.example.koalaappm13

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.koalaappm13.database.AppDatabase
import com.example.koalaappm13.database.ProductionRepository
import com.example.koalaappm13.database.ProductionViewModel
import com.example.koalaappm13.ui.KoalaAppM13Theme
import com.example.koalaappm13.ui.ProductionListScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductionListActivity : ComponentActivity() {

    private lateinit var viewModel: ProductionViewModel
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

        val dao = AppDatabase.getDatabase(applicationContext, lifecycleScope).productionDao()
        val repository = ProductionRepository(dao)
        viewModel = ProductionViewModel(repository, currentUsername)

        setContent {
            KoalaAppM13Theme {
                ProductionListScreen(
                    viewModel = viewModel,
                    onProductionSelected = { productionId ->
                        val intent = Intent(this, ProductionDetailActivity::class.java)
                        intent.putExtra("production_id", productionId)
                        intent.putExtra("current_username", currentUsername)
                        startActivity(intent)
                    },
                    onCreateNew = {
                        val intent = Intent(this, CreateProductionActivity::class.java)
                        intent.putExtra("current_username", currentUsername)
                        startActivity(intent)
                    }
                )
            }
        }
    }
} 