package com.example.koalaappm13.database

import kotlinx.coroutines.flow.Flow

class ProgramaTvRepository(private val dao: ProgramaTvDao) {

    fun getAllProgramas(): Flow<List<ProgramaTV>> {
        return dao.getAllProgramas()
    }

    suspend fun insertPrograma(programa: ProgramaTV) {
        dao.insert(programa)
    }

    suspend fun updatePrograma(programa: ProgramaTV) {
        dao.update(programa)
    }

    suspend fun deletePrograma(programa: ProgramaTV) {
        dao.delete(programa)
    }
}
