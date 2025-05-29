package com.example.koalaappm13

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consent_forms")
data class ConsentForm(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    // Datos del sujeto
    val nombre: String,
    val apellidos: String,
    val dni: String?,
    val fechaNacimiento: Long,
    val mayorEdad: Boolean,
    val menorEdad: Boolean,

    // Datos de contacto
    val telefono: String,
    val email: String?, // opcional

    // Grabación
    val fechaGrabacion: Long,
    val lugarGrabacion: String,

    // Firma digital
    val firmaPath: String?, // Ruta local a la firma

    // Progenitores (si menor de edad)
    val progenitor1Nombre: String?,
    val progenitor1Apellidos: String?,
    val progenitor1Dni: String?,
    val progenitor1Telefono: String?,
    val progenitor1Direccion: String?,
    val progenitor1Consentimiento: Boolean = false,

    val progenitor2Nombre: String?,
    val progenitor2Apellidos: String?,
    val progenitor2Dni: String?,
    val progenitor2Telefono: String?,
    val progenitor2Direccion: String?,
    val progenitor2Consentimiento: Boolean = false,

    val soloUnProgenitor: Boolean = false,

    // Información interna
    val descripcionUso: String,
    val usuarioCreador: String,
    val productionId: Long? = null,
    val created_by: String,
    val created_at: Long = System.currentTimeMillis()
)
