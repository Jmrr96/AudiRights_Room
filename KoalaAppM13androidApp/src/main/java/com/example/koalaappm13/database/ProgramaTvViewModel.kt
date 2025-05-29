package com.example.koalaappm13.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProgramaTvViewModel(
    private val repository: ProgramaTvRepository
) : ViewModel() {

    val allProgramas: StateFlow<List<ProgramaTV>> = repository.getAllProgramas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addPrograma(nombre: String) {
        if (nombre.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.insertPrograma(ProgramaTV(nombre = nombre.trim()))
            }
        }
    }

    fun updatePrograma(programa: ProgramaTV) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePrograma(programa)
        }
    }

    fun deletePrograma(programa: ProgramaTV) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePrograma(programa)
        }
    }
}
