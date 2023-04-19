package com.hashone.commons.extensions

import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import java.util.Arrays
import kotlin.math.roundToInt

fun AppCompatTextView.getMinWidthFromText(): Int {
    var minWord: String? = "W"
    minWord = Arrays.stream<String?>(
        text.toString().replace("\n", " ").split(" ".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray())
        .max(Comparator.comparingInt { obj: String? -> obj!!.length })
        .orElse(null)
    return paint.measureText(minWord, 0, minWord!!.length).roundToInt()
}

fun AppCompatTextView.getMaxWidthFromText(input: String): Int {
    var maxWord: String? = input
    maxWord = Arrays.stream<String?>(
        text.toString().split("\n".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray())
        .max(Comparator.comparingInt { obj: String? -> obj!!.length })
        .orElse(null)
    return paint.measureText(maxWord, 0, maxWord!!.length).roundToInt()
}

fun AppCompatTextView.applyTextStyle(color: Int, font: Int, size: Float) {
    setTextColor(color)
    typeface = ResourcesCompat.getFont(context, font)
    setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
}