package com.example.koalaappm13

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream

fun exportImageToGallery(context: Context, bitmap: Bitmap, fileName: String = "firma_koala.png"): Boolean {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/KoalaConsents")
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    return uri?.let {
        val stream: OutputStream? = resolver.openOutputStream(it)
        val result = stream?.use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) } ?: false
        result
    } ?: false
}