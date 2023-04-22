package com.hashone.commons.extensions

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable

fun corneredDrawable(color: Int, radius: Float): Drawable {
    return GradientDrawable().apply {
        cornerRadius = radius
        setColor(color)
        shape = GradientDrawable.RECTANGLE
    }
}