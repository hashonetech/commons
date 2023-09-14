package com.hashone.commons.languages

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.hashone.commons.R
import com.hashone.commons.base.CommonApplication
import com.hashone.commons.extensions.getLocaleContext
import com.hashone.commons.languages.LanguageActivity.Companion.KEY_RETURN_LANGUAGE_DATA
import com.hashone.commons.languages.ui.theme.CircularIndeterminateProgressBar
import com.hashone.commons.utils.ACTION_LANGUAGE_CHANGE
import com.hashone.commons.utils.DEFAULT_LANGUAGE
import com.hashone.commons.utils.DEFAULT_LANGUAGE_COUNTY_CODE
import com.hashone.commons.utils.DEFAULT_LANGUAGE_NAME


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(languageBuilder: Language.Builder) {

    val activity = (LocalContext.current as? ComponentActivity)
    var loading by remember { mutableStateOf(true) }
    val languageList by remember {
        mutableStateOf(
            languageBuilder.languageItemsList
        )
    }

    Scaffold(
        modifier = Modifier.padding(0.dp),
        topBar = {
            TopAppBar(
                modifier = Modifier, title = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp),
                        text = languageBuilder.toolBarBuilder.title,
                        fontSize = languageBuilder.toolBarBuilder.titleSize.sp,
                        color = colorResource(languageBuilder.toolBarBuilder.titleColor),
                        fontFamily = FontFamily(Font(languageBuilder.toolBarBuilder.titleFont))
                    )
                },

                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            ImageVector.vectorResource(id = languageBuilder.toolBarBuilder.backIcon),
                            contentDescription = stringResource(id = R.string.label_back)
                        )
                    }
                }, colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = colorResource(
                        languageBuilder.toolBarBuilder.toolBarColor
                    )
                )
            )
        },
    ) {
        MainContent(
            it, languageBuilder, languageList, loading,
            onLoadLanguage = {
                loading = false
            }
        ) { languageItem ->
            activity?.let { myActivity ->
                val localContext =
                    LocaleHelper.setLocale(
                        myActivity,
                        languageItem.languageCode,
                        languageItem.countryCode
                    )
                CommonApplication.mInstance.setLocaleContext(localContext!!)
                myActivity.sendBroadcast(Intent().setAction(ACTION_LANGUAGE_CHANGE))
                /*Below Code use to tell System to set App language*/
                val localeList = LocaleListCompat.forLanguageTags(languageItem.languageCode)

                CommonApplication.mInstance.mStoreUserData.setString(
                    DEFAULT_LANGUAGE,
                    languageItem.languageCode
                )
                CommonApplication.mInstance.mStoreUserData.setString(
                    DEFAULT_LANGUAGE_COUNTY_CODE,
                    languageItem.countryCode
                )
                CommonApplication.mInstance.mStoreUserData.setString(
                    DEFAULT_LANGUAGE_NAME,
                    languageItem.languageName
                )

                AppCompatDelegate.setApplicationLocales(localeList)

                myActivity.setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(KEY_RETURN_LANGUAGE_DATA, languageItem)
                })
                myActivity.finish()
            }
        }
        CircularIndeterminateProgressBar(isDisplayed = loading)
    }
}

@Composable
fun MainContent(
    paddingValues: PaddingValues,
    languageBuilder: Language.Builder,
    languageList: ArrayList<LanguageItem>,
    loading: Boolean,
    onLoadLanguage: () -> Unit,
    onLanguageChange: (item: LanguageItem) -> Unit
) {
    val activity = (LocalContext.current as? Activity)
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .background(colorResource(id = languageBuilder.screenBuilder.windowBackgroundColor))
    ) {
        LanguageSelection(languageBuilder = languageBuilder,
            languageList = languageList,
            activity = activity!!,
            onLoadLanguage = {
                onLoadLanguage()
            },
            onLanguageChange = {
                onLanguageChange(it)
            })
    }

}

@Composable
fun LanguageSelection(
    languageBuilder: Language.Builder,
    languageList: java.util.ArrayList<LanguageItem>,
    activity: Activity,
    onLoadLanguage: () -> Unit,
    onLanguageChange: (item: LanguageItem) -> Unit
) {

    val initialSelectedIndex = remember {
        val languageItem = languageList.first {
            it.isChecked
        }
        languageList.indexOf(languageItem)
    }

    AnimatedLanguageSelection(languageBuilder = languageBuilder,
        languageList = languageList,
        initialSelectedIndex = initialSelectedIndex,
        activity = activity,
        onLanguageChange = {
            onLanguageChange(it)
        })
    onLoadLanguage()
}

