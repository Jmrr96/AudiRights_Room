package com.example.koalaappm13.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProductionViewModelFactory(
    private val repository: ProductionRepository,
    private val currentUsername: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductionViewModel::class.java)) {
            return ProductionViewModel(repository, currentUsername) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
