package com.example.koalaappm13.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.koalaappm13.ConsentForm
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ConsentFormViewModel(
    private val repository: ConsentFormRepository,
    val currentUsername: String
) : ViewModel() {
    
    val allConsentForms: StateFlow<List<ConsentForm>> = repository
        .getConsentFormsForUser(currentUsername)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _formInsertState = MutableStateFlow<Long?>(null)
    val formInsertState: StateFlow<Long?> = _formInsertState

    fun insertConsentForm(form: ConsentForm) {
        viewModelScope.launch {
            val formWithAudit = form.copy(
                usuarioCreador = currentUsername,
                created_at = System.currentTimeMillis()
            )
            val id = repository.insertConsentForm(formWithAudit)
            _formInsertState.value = id
        }
    }

    fun deleteConsentForm(form: ConsentForm) {
        if (form.usuarioCreador == currentUsername) {
            viewModelScope.launch {
                repository.deleteConsentForm(form)
            }
        }
    }

    fun getAvailableProductions(): Flow<List<Production>> {
        return repository.getProductionsForUser(currentUsername)
    }

    fun getConsentFormById(id: Long) = repository.getConsentFormByIdFlow(id)
        .map { form ->
            if (form?.usuarioCreador == currentUsername) form else null
        }
}

