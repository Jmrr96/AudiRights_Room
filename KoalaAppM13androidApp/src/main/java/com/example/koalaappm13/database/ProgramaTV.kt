package com.example.koalaappm13.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "programas_tv")
data class ProgramaTV(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String
)
