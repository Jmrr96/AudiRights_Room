package com.example.koalaappm13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.koalaappm13.database.*
import com.example.koalaappm13.ui.CreateProductionScreen
import com.example.koalaappm13.ui.KoalaAppM13Theme

class CreateProductionActivity : ComponentActivity() {

    private lateinit var productionViewModel: ProductionViewModel

    // ✅ ViewModel de programas de TV usando factory
    private val programaViewModel: ProgramaTvViewModel by viewModels {
        ProgramaTvViewModelFactory((application as KoalaApp).programaTvRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ❌ Eliminado el uso de SessionManager aquí
        val productionDao = AppDatabase.getDatabase(applicationContext, lifecycleScope).productionDao()
        val productionRepository = ProductionRepository(productionDao)
        productionViewModel = ProductionViewModel(productionRepository, "")

        setContent {
            KoalaAppM13Theme {
                CreateProductionScreen(
                    viewModel = productionViewModel,
                    programaViewModel = programaViewModel,
                    currentUsername = "", // ❌ No se usa en la creación
                    onSaved = { finish() }
                )
            }
        }
    }
}
