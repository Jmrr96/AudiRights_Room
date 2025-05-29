package com.example.koalaappm13.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramaTvDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(programa: ProgramaTV)

    @Delete
    suspend fun delete(programa: ProgramaTV)

    @Update
    suspend fun update(programa: ProgramaTV)

    @Query("SELECT * FROM programas_tv ORDER BY nombre ASC")
    fun getAllProgramas(): Flow<List<ProgramaTV>>
}
