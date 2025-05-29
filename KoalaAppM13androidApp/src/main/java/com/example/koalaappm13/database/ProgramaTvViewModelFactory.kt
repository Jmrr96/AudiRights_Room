package com.example.koalaappm13.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProgramaTvViewModelFactory(
    private val repository: ProgramaTvRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgramaTvViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgramaTvViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
