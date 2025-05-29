package com.example.koalaappm13.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productions")
data class Production(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val apellidos: String,
    val dni: String?,                // Nuevo campo
    val telefono: String?,
    val direccion: String,
    val email: String?,             // 👈 nuevo campo opcional
    val lugarGrabacion: String?, // Nuevo campo
    val edadCategoria: String?,     // "mayor" o "menor"
    val descripcion: String,
    val fechaInicio: Long = System.currentTimeMillis(),
    val fechaFin: Long? = null,
    val usuarioCreador: String,
    val created_by: String,
    val programa: String = "Sin especificar",
    val firmaPath: String? = null,
    val created_at: Long = System.currentTimeMillis(),
    val grabacion: String? = null,

    // ✅ Nuevo campo unificado para el estado
    val estadoSesion: String? = null, // "válida", "no_válida" o null
    val estadoValidadoPor: String? = null,

    // Progenitor 1
    val progenitor1Nombre: String? = null,
    val progenitor1Apellidos: String? = null,
    val progenitor1Dni: String? = null,
    val progenitor1Telefono: String? = null,
    val progenitor1Direccion: String? = null,
    val progenitor1Consentimiento: Boolean? = null,

// Progenitor 2
    val progenitor2Nombre: String? = null,
    val progenitor2Apellidos: String? = null,
    val progenitor2Dni: String? = null,
    val progenitor2Telefono: String? = null,
    val progenitor2Direccion: String? = null,
    val progenitor2Consentimiento: Boolean? = null,

// Solo un progenitor
    val soloUnProgenitor: Boolean? = null,
    val imagenPath: String? = null,
    val descripcionImagen: String? = null


)
