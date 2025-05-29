package com.example.koalaappm13.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductionDao {

    @Query("SELECT * FROM productions")
    fun getAllProductions(): Flow<List<Production>>

    @Query("SELECT * FROM productions WHERE id = :id")
    fun getProductionById(id: Long): Flow<Production?>

    @Query("SELECT * FROM productions WHERE usuarioCreador = :username ORDER BY created_at DESC")
    fun getProductionsForUser(username: String): Flow<List<Production>>

    @Insert
    suspend fun insertProduction(production: Production): Long

    @Delete
    suspend fun deleteProduction(production: Production)

    @Update
    suspend fun updateProduction(production: Production)

    @Query(
        """
        SELECT * FROM productions
    WHERE 
        (:nombre IS NULL OR nombre LIKE '%' || :nombre || '%') AND
        (:apellidos IS NULL OR apellidos LIKE '%' || :apellidos || '%') AND
        (:dni IS NULL OR dni LIKE '%' || :dni || '%') AND
        (:telefono IS NULL OR telefono LIKE '%' || :telefono || '%') AND
        (
            :mayorDeEdad IS NULL OR 
            (LOWER(TRIM(edadCategoria)) = CASE 
                WHEN :mayorDeEdad = 1 THEN 'adulto' 
                WHEN :mayorDeEdad = 0 THEN 'menor' 
                ELSE LOWER(TRIM(edadCategoria)) 
            END)
        ) AND
        (:programa IS NULL OR programa = :programa) AND
        (:conImagen IS NULL OR 
    (:conImagen = 1 AND firmaPath IS NOT NULL AND firmaPath != '') OR 
    (:conImagen = 0 AND (firmaPath IS NULL OR firmaPath = ''))
)
    ORDER BY created_at DESC
        """
    )
    fun searchProductionsWithFilters(
        nombre: String?,
        apellidos: String?,
        dni: String?,
        telefono: String?,
        mayorDeEdad: Boolean?, // true → adultos, false → menores, null → sin filtro
        programa: String?,
        conImagen: Boolean?
    ): Flow<List<Production>>
}
