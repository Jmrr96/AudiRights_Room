package com.example.koalaappm13

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, filename: String): String {
    val name = "$filename-${System.currentTimeMillis()}.png"
    val file = File(context.filesDir, name)
    FileOutputStream(file).use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }
    return file.absolutePath
}