package com.hashone.commons.extensions

import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.CompoundButtonCompat
import com.hashone.commons.utils.checkClickTime

inline fun View.doOnGlobalLayout(crossinline action: (view: View) -> Unit) {
    val vto = viewTreeObserver
    vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            action(this@doOnGlobalLayout)
            when {
                vto.isAlive -> {
                    vto.removeOnGlobalLayoutListener(this)
                }

                else -> {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        }
    })
}

fun View.addBackgroundRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}

fun View.addBackgroundCircleRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
    setBackgroundResource(resourceId)
}

fun View.addForegroundRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    foreground = ContextCompat.getDrawable(context, resourceId)
}

fun View.addForegroundCircleRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
    foreground = ContextCompat.getDrawable(context, resourceId)
}

fun RadioButton.applyTintColor(color: Int) {
    if (color != -1) {
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.textColorSecondary), //disabled
                intArrayOf(android.R.attr.colorAccent) //enabled
            ), intArrayOf(
                color //disabled
                , color //enabled
            )
        )
        CompoundButtonCompat.setButtonTintList(this, colorStateList)
    }
}

fun RadioButton.applyTextStyle(color: Int, font: Int, size: Float) {
    if (color != -1)
        setTextColor(color)
    if (font != -1)
        typeface = ResourcesCompat.getFont(context, font)
    if (size != -1F)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
}

inline fun View.onClick(crossinline action: (view: View) -> Unit) {
    setOnClickListener {
        if (checkClickTime())
            action(this@onClick)
    }
}