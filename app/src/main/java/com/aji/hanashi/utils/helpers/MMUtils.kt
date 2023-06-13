package com.aji.hanashi.utils.helpers

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

private const val FORMAT = "dd-MM-yyyy"

val time: String = SimpleDateFormat(FORMAT, Locale.US).format(System.currentTimeMillis())

fun temp(context: Context): File {
    val dir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(time, ".jpg", dir)
}

fun uriToFile(selectedImage: Uri, context: Context): File{
    val contentResolver: ContentResolver = context.contentResolver
    val file = temp(context)
    val inputStream = contentResolver.openInputStream(selectedImage) as InputStream
    val outputStream: OutputStream = FileOutputStream(file)
    val buf = ByteArray(1024)
    var len: Int

    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()
    return file
}