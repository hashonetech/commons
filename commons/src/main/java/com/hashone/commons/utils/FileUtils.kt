package com.hashone.commons.utils

import android.content.Context
import android.content.ContextWrapper
import java.io.File
import java.io.FileWriter

fun getInternalCameraDir(context: Context): File {
    val contextWrapper = ContextWrapper(context)
    val rootDir = contextWrapper.getDir(context.filesDir.name, Context.MODE_PRIVATE)
    val imageDir = File(rootDir.absolutePath, "camera")
    imageDir.setReadable(true)
    imageDir.setWritable(true, false)
    if (!imageDir.exists()) {
        imageDir.mkdirs()
        imageDir.mkdir()
        val gpxFile = File(imageDir, ".nomedia")
        val writer = FileWriter(gpxFile)
        writer.flush()
        writer.close()
    }
    return imageDir
}