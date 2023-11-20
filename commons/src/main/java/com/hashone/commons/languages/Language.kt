package com.hashone.commons.languages

import android.app.Activity
import android.content.Intent
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.FontRes
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import com.hashone.commons.R
import java.io.Serializable

open class Language(val builder: Builder) : Serializable {

    companion object {
        inline fun build(
            languageItemsList: ArrayList<LanguageItem>,
            block: Builder.() -> Unit
        ) = Builder(
            languageItemsList
        ).apply(block).build()

        fun open(activity: Activity, language: Language): Intent =
            LanguageActivity.newIntent(context = activity, language = language)
    }

    class Builder(
        var languageItemsList: ArrayList<LanguageItem>
    ) : Serializable {
        //TODO: Screen
        var screenBuilder = ScreenBuilder()

        //TODO: Toolbar
        var toolBarBuilder = ToolBarBuilder()

        //TODO: Language Item
        var languageItemBuilder = LanguageItemBuilder()

        fun build() = Language(this)
    }

    class ScreenBuilder(
        var isFullScreen: Boolean = false,
        @ColorRes
        var windowBackgroundColor: Int = R.color.white,
        @ColorRes
        var statusBarColor: Int = R.color.white,
        @ColorRes
        var navigationBarColor: Int = R.color.white,
    ) : Serializable

    class ToolBarBuilder(
        @ColorRes
        var toolBarColor: Int = R.color.white,
        @DrawableRes
        var backIcon: Int = R.drawable.ic_back,
        var title: String = "",
        @ColorRes
        var titleColor: Int = R.color.black,
        @FontRes
        var titleFont: Int = R.font.roboto_medium,
        @FloatRange
        var titleSize: Float = 16F,
    ) : Serializable

    class LanguageItemBuilder(
        @ColorRes
        var selectedColor: Int = R.color.black,
        @ColorRes
        var defaultColor: Int = R.color.light_gray,
        @DrawableRes
        var selectedIcon: Int = R.drawable.ic_apply,
        @ColorRes
        var bgColor: Int = R.color.white,
        @FontRes
        var titleFont: Int = R.font.roboto_medium,
        @FloatRange
        var titleSize: Float = 14F,
        @IntRange
        var paddingStart: Int = 16,
        @IntRange
        var paddingEnd: Int = 0,
        @IntRange
        var paddingTop: Int = 16,
        @IntRange
        var paddingBottom: Int = 16,
        @IntRange
        var paddingAll: Int = -1,

        @FontRes
        var originalNameFont: Int = R.font.roboto_regular,
        @FloatRange
        var originalNameSize: Float = 12F,

        @IntRange
        var iconPaddingStart: Int = 8,
        @IntRange
        var iconPaddingEnd: Int = 8,
        @IntRange
        var iconPaddingTop: Int = 8,
        @IntRange
        var iconPaddingBottom: Int = 8,
        @IntRange
        var iconPaddingAll: Int = -1,
        @ColorRes
        var dividerColor: Int = R.color.secondary_extra_light_gray,
        @IntRange
        var dividerThickness: Int = 1,
    ) : Serializable

}