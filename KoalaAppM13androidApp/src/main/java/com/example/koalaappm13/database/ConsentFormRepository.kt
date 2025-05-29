package com.example.koalaappm13.database

import com.example.koalaappm13.ConsentForm
import kotlinx.coroutines.flow.Flow

class ConsentFormRepository(
    private val consentFormDao: ConsentFormDao,
    private val productionDao: ProductionDao // Añadir esto
) {

    fun getAllConsentForms(): Flow<List<ConsentForm>> = consentFormDao.getAllConsentForms()

    suspend fun insertConsentForm(form: ConsentForm): Long {
        return consentFormDao.insertConsentForm(form)
    }

    suspend fun getConsentFormById(id: Long): ConsentForm? {
        return consentFormDao.getConsentFormById(id)
    }

    fun getConsentFormByIdFlow(id: Long): Flow<ConsentForm?> {
        return consentFormDao.getConsentFormByIdFlow(id)
    }

    fun getConsentFormsForUser(username: String): Flow<List<ConsentForm>> {
        return consentFormDao.getConsentFormsForUser(username)
    }
     fun getProductionsForUser(username: String): Flow<List<Production>> {
        return productionDao.getProductionsForUser(username)
    }

    suspend fun deleteConsentForm(form: ConsentForm) {
        consentFormDao.deleteConsentForm(form)
    }
}