package com.life4.feedz.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.InputStream


fun saveFile(inputStream: InputStream, fileName: String, context: Context): String {
    lateinit var newDirection: File

    val direction = File(context.filesDir.toString()) // default folder /files

    newDirection = File(direction, "/media") // if you want to create new folder
    if (!newDirection.exists())
        newDirection.createNewFile()


    val file = File(newDirection, fileName)
    context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
        it.write(inputStream.readBytes())
        it.close()
    }
    return file.absolutePath
}

fun getMimeTypeExtension(uri: Uri, context: Context): String? {
    val cR: ContentResolver = context.contentResolver
    val mime = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(uri))
}

fun getMimeType(uri: Uri, context: Context): String? {
    val cR: ContentResolver = context.contentResolver
    return cR.getType(uri);
}
