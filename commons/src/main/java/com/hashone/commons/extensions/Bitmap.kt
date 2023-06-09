package com.hashone.commons.extensions

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import com.hashone.commons.utils.EXTENSION_JPG
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

fun Bitmap.getBitmap(width: Int, height: Int, mColorCode: Int): Bitmap {
    val colorBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val surfaceCanvas = Canvas(colorBitmap)
    val surfacePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    surfacePaint.isAntiAlias = true
    surfacePaint.isDither = true
    surfacePaint.isFilterBitmap = true
    surfacePaint.color = mColorCode
    surfaceCanvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), surfacePaint)
    return colorBitmap
}

fun Bitmap.rotate(degrees: Float) =
    Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(degrees) }, true)

fun Bitmap.copy(): Bitmap? = copy(config, isMutable)

fun Bitmap.flip(xFlip: Boolean = false, yFlip: Boolean = false): Bitmap? {
    return let {
        Bitmap.createBitmap(
            it, 0, 0, width, height, Matrix().apply {
                postScale(
                    if (xFlip) -1F else 1F,
                    if (yFlip) -1F else 1F,
                    width / 2f,
                    height / 2f
                )
            }, true
        )
    }
}

fun Bitmap.radius(mRadiusValue: Float = 0F): Bitmap? {
    return if (mRadiusValue > 0F) {
        val output = Bitmap.createBitmap(width, height, config)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, width, height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true
        val minSizeValue = min(width, height)
        val localRadiusValue: Float = ((minSizeValue / 2F) * mRadiusValue) / 100F
        canvas.drawRoundRect(rectF, localRadiusValue, localRadiusValue, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(this, rect, rect, paint)
        output
    } else {
        this
    }
}

@SuppressLint("SetWorldWritable")
fun Bitmap.saveToFile(
    fileName: String, saveDir: File, compressFormat: String = ""
): File {
    val dir = File(saveDir.absolutePath)
    dir.setReadable(true)
    dir.setExecutable(true)
    dir.setWritable(true, false)
    if (!dir.exists()) {
        dir.mkdirs()
        dir.mkdir()
    }
    val file = File(dir, fileName)
    if (file.exists()) {
        file.delete()
    }
    try {
        file.createNewFile()
        val fOut = FileOutputStream(file)
        compress(
            if (compressFormat == EXTENSION_JPG) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG,
            100,
            fOut
        )
        fOut.flush()
        fOut.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return file
}