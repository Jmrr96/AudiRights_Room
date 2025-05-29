package com.example.koalaappm13.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductionViewModel(
    private val repository: ProductionRepository,
    val currentUsername: String
) : ViewModel() {

    val allProductions: StateFlow<List<Production>> = repository
        .getProductionsForUser(currentUsername)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addProduction(production: Production) {
        viewModelScope.launch {
            repository.insertProduction(
                production.copy(
                    created_at = System.currentTimeMillis()
                )
            )
        }
    }


    fun getProductionById(id: Long): Flow<Production?> {
        return repository.getProductionById(id)
    }

    fun deleteProduction(production: Production) {
        viewModelScope.launch {
            repository.deleteProduction(production)
        }
    }

    fun updateProduction(production: Production) {
        viewModelScope.launch {
            repository.updateProduction(production)
        }
    }
    fun getAllProductions(): Flow<List<Production>> {
        return repository.getAllProductions()
    }


    fun searchProductionsWithFilters(
        nombre: String?,
        apellidos: String?,
        dni: String?,
        telefono: String?,
        mayorDeEdad: Boolean?,
        programa: String?,
        conImagen: Boolean?
    ): Flow<List<Production>> {
        return repository.searchProductionsWithFilters(
            nombre?.takeIf { it.isNotBlank() },
            apellidos?.takeIf { it.isNotBlank() },
            dni?.takeIf { it.isNotBlank() },
            telefono?.takeIf { it.isNotBlank() },
            mayorDeEdad,
            programa?.takeIf { it.isNotBlank() },
            conImagen
        )
    }
}
