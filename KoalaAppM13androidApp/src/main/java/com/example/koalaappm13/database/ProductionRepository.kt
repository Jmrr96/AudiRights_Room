package com.example.koalaappm13.database

import kotlinx.coroutines.flow.Flow

class ProductionRepository(private val productionDao: ProductionDao) {

    fun getAllProductions(): Flow<List<Production>> = productionDao.getAllProductions()

    fun getProductionsForUser(username: String): Flow<List<Production>> =
        productionDao.getProductionsForUser(username)

    suspend fun insertProduction(production: Production): Long {
        return productionDao.insertProduction(production)
    }

    suspend fun deleteProduction(production: Production) {
        productionDao.deleteProduction(production)
    }

    suspend fun updateProduction(production: Production) { // ✅ nuevo
        productionDao.updateProduction(production)
    }

    fun getProductionById(id: Long): Flow<Production?> {
        return productionDao.getProductionById(id)
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
        return productionDao.searchProductionsWithFilters(
            nombre, apellidos, dni, telefono, mayorDeEdad, programa, conImagen
        )
    }
}
