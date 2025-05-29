package com.example.koalaappm13.database

import androidx.room.*
import com.example.koalaappm13.ConsentForm
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsentFormDao {
    @Query("SELECT * FROM consent_forms")
    fun getAllConsentForms(): Flow<List<ConsentForm>>

    @Query("SELECT * FROM consent_forms WHERE usuarioCreador = :username ORDER BY created_at DESC")
    fun getConsentFormsForUser(username: String): Flow<List<ConsentForm>>

    @Query("SELECT * FROM consent_forms WHERE id = :id")
    suspend fun getConsentFormById(id: Long): ConsentForm?

    @Query("SELECT * FROM consent_forms WHERE id = :id")
    fun getConsentFormByIdFlow(id: Long): Flow<ConsentForm?>

    @Insert
    suspend fun insertConsentForm(form: ConsentForm): Long

    @Delete
    suspend fun deleteConsentForm(form: ConsentForm)
}