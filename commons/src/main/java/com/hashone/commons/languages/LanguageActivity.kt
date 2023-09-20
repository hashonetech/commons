package com.hashone.commons.languages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.hashone.commons.base.BaseActivity
import com.hashone.commons.extensions.getColorCode
import com.hashone.commons.extensions.hideSystemUI
import com.hashone.commons.extensions.navigationUI
import com.hashone.commons.extensions.serializable
import com.hashone.commons.extensions.setStatusBarColor

class LanguageActivity : BaseActivity() {

    private lateinit var builder: Language.Builder
    companion object {
        const val KEY_LANGUAGE_DATA = "KEY_LANGUAGE_DATA"
        const val KEY_RETURN_LANGUAGE_DATA = "KEY_RETURN_LANGUAGE_DATA"
        fun newIntent(context: Context, language: Language): Intent {
            return Intent(context, LanguageActivity::class.java).apply {
                Bundle().apply { putSerializable(KEY_LANGUAGE_DATA, language) }
                    .also { this.putExtras(it) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        builder = (intent!!.extras!!.serializable<Language>(KEY_LANGUAGE_DATA)!!).builder
        setWindowUI()
        setContent {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LanguageScreen(builder)
                }
        }
    }

    //TODO: Screen UI - Start
    private fun setWindowUI() {
        if (builder.screenBuilder.isFullScreen) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            setStatusBarColor(getColorCode(builder.screenBuilder.statusBarColor))
            navigationUI(false, getColorCode(builder.screenBuilder.navigationBarColor))
            hideSystemUI()

        } else {
            if (builder.screenBuilder.statusBarColor != -1) {
                setStatusBarColor(getColorCode(builder.screenBuilder.statusBarColor))
                navigationUI(true, getColorCode(builder.screenBuilder.navigationBarColor))
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = true
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LanguagePreview() {
//    CommonsTheme {
//        LanguageActivityUI(null)
//    }
}