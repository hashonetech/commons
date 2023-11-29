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

fun getCacheDirectoryName(context: Context): File {
    val contextWrapper = ContextWrapper(context)
    val rootDir = contextWrapper.getDir(context.cacheDir.name, Context.MODE_PRIVATE)
    if (!rootDir.exists()) {
        rootDir.mkdirs()
        rootDir.mkdir()
    }
    return context.cacheDir
}

fun writeTextInFile(context: Context, content: String): File {
    val dataFile = File(getCacheDirectoryName(context), "device-info.txt")
    dataFile.setReadable(true)
    dataFile.setWritable(true, false)
    dataFile.writeText(content)
    return dataFile
}