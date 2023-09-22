package com.hashone.commons

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hashone.commons.base.BaseActivity
import com.hashone.commons.base.BetterActivityResult
import com.hashone.commons.base.CommonApplication
import com.hashone.commons.contactus.ContactUs
import com.hashone.commons.extensions.serializable
import com.hashone.commons.languages.Language
import com.hashone.commons.languages.LanguageActivity.Companion.KEY_RETURN_LANGUAGE_DATA
import com.hashone.commons.languages.LanguageItem
import com.hashone.commons.languages.LocaleManager
import com.hashone.commons.test.databinding.ActivityMainBinding
import com.hashone.commons.utils.ACTION_LANGUAGE_CHANGE
import com.hashone.commons.utils.DEFAULT_LANGUAGE
import com.hashone.commons.utils.DEFAULT_LANGUAGE_COUNTY_CODE
import com.hashone.commons.utils.DEFAULT_LANGUAGE_NAME
import com.hashone.commons.webview.CustomTabActivityHelper
import com.hashone.commons.webview.WebViewFallback

class MainActivity : BaseActivity() {

    lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.buttonContactus.setOnClickListener {
            ContactUs.open(
                activity = this, ContactUs.build(
                    emailBuilder = ContactUs.EmailBuilder(
                        emailTitle = "",
                        feedbackEmail = "",
                        appName = "",
                        packageName = "",
                        versionName = "",
                        androidDeviceToken = "",
                        customerNumber = "",
                        countryCode = "",
                        isPremium = false,
                        purchasedTitle = "",
                        orderId = "",
                        contentId = "",
                        contentTitle = "",
                        showKeyboard = false,
                    ),
                    mediaBuilder = ContactUs.MediaBuilder(allowPhotosOnly = false,
                        allowVideosOnly = false,
                        allowBoth = true,
                        maxFileSize = 15L,
                        //TODO: Ratio (messageBoxHeight / ScreenWidth)
                        messageBoxHeight = 0.4444444444,
                        //TODO: Ratio (attachmentBoxHeight / ScreenWidth)
                        attachmentBoxHeight = 0.588888888888889,
                        optionItemsList = ArrayList<ContactUs.OptionItem>().apply {
                            add(
                                ContactUs.OptionItem(
                                    text = "Feedback",
                                    message = "Type your feedback here.",
                                    isChecked = true
                                )
                            )
                            add(
                                ContactUs.OptionItem(
                                    text = "Issue",
                                    message = "Please describe issue in detail.",
                                    isChecked = false
                                )
                            )
                            add(
                                ContactUs.OptionItem(
                                    text = "Request",
                                    message = "Add your request here.",
                                    isChecked = false
                                )
                            )
                        }),
                ) {
                    //TODO: Screen
                    screenBuilder = ContactUs.ScreenBuilder(
                        isFullScreen = false,
                        windowBackgroundColor = R.color.extra_extra_light_gray,
                        statusBarColor = R.color.white,
                        navigationBarColor = R.color.extra_extra_light_gray,
                    )

                    //TODO: Toolbar
                    toolBarBuilder = ContactUs.ToolBarBuilder(
                        barColor = R.color.white,
                        backIcon = R.drawable.ic_back_contact_us,
                        backIconDescription = "",
                        title = "",
                        titleColor = R.color.black,
                        titleFont = R.font.outfit_semi_bold,
                        titleSize = 16F,
                    )

                    //TODO: Radio Buttons
                    radioButtonBinding = ContactUs.RadioButtonBuilder(
                        selectedColor = R.color.black,
                        defaultColor = R.color.black,
                        textFont = R.font.roboto_medium,
                        textSize = 14F,
                    )

                    //TODO: Message UI
                    messageBuilder = ContactUs.MessageBuilder(
                        backgroundColor = R.color.white,
                        backgroundRadius = 8F,
                        hint = "",
                        message = "",
                        color = R.color.black,
                        font = R.font.roboto_medium,
                        size = 14F,
                    )

                    //TODO: Attachment UI
                    attachmentBuilder = ContactUs.AttachmentBuilder(
                        cardBackgroundColor = R.color.extra_extra_light_gray,
                        cardBackgroundRadius = 8F,
                        backgroundColor = R.color.white,
                        backgroundRadius = 8F,
                        title = "",
                        titleColor = R.color.light_gray,
                        titleFont = R.font.roboto_medium,
                        titleSize = 14F,
                        addIcon = R.drawable.ic_contact_us_add_attachment,
                        deleteIcon = R.drawable.ic_contact_us_img_delete,
                    )

                    //TODO: Action button
                    actionButtonBuilder = ContactUs.ActionButtonBuilder(
                        backgroundInactiveColor = R.color.light_gray,
                        backgroundColor = R.color.black,
                        radius = 30F,
                        text = "",
                        textColor = R.color.white,
                        textFont = R.font.outfit_bold,
                        textSize = 16F,
                    )
                })
        }

        if (CommonApplication.mInstance.mStoreUserData.getString(DEFAULT_LANGUAGE) != null && CommonApplication.mInstance.mStoreUserData.getString(
                DEFAULT_LANGUAGE
            )!!.isEmpty()
        ) {
            CommonApplication.mInstance.mStoreUserData.setString(
                DEFAULT_LANGUAGE,
                "en"
            )
        }

        mBinding.buttonLanguage.setOnClickListener {
            val currentLocale = LocaleManager.getAppLocale()
            LocaleManager.mLanguagesList.forEachIndexed { index, languageItem ->
                languageItem.isChecked = currentLocale?.toLanguageTag() == languageItem.languageCode
            }

            mActivityLauncher.launch(
                Language.open(activity = this, Language.build(
                    languageItemsList = ArrayList(LocaleManager.mLanguagesList)
                ) {

                    //TODO: Screen
                    screenBuilder = Language.ScreenBuilder(
                        isFullScreen = false,
                        windowBackgroundColor = R.color.white,
                        statusBarColor = R.color.white,
                        navigationBarColor = R.color.white,
                    )

                    //TODO: Toolbar
                    toolBarBuilder = Language.ToolBarBuilder(
                        toolBarColor = R.color.white,
                        backIcon = R.drawable.ic_back,
                        title = getString(com.hashone.commons.test.R.string.label_language),
                        titleColor = R.color.black,
                        titleFont = R.font.roboto_medium,
                        titleSize = 16F,
                    )

                    //TODO: Language Item
                    languageItemBuilder = Language.LanguageItemBuilder(
                        selectedColor = R.color.black,
                        defaultColor = R.color.light_gray,
                        selectedIcon = R.drawable.ic_apply,
                        bgColor = R.color.white,
                        titleFont = R.font.roboto_medium,
                        titleSize = 14F,
                        paddingStart = 16,
                        paddingEnd = 0,
                        paddingTop = 16,
                        paddingBottom = 16,
                        iconPaddingStart = 8,
                        iconPaddingEnd = 8,
                        iconPaddingTop = 8,
                        iconPaddingBottom = 8,
                        originalNameFont = R.font.roboto_regular,
                        originalNameSize = 12F,
                        dividerColor = R.color.secondary_extra_light_gray,
                        dividerThickness = 1,
                    )
                }),
                onActivityResult = object : BetterActivityResult.OnActivityResult<ActivityResult> {
                    override fun onActivityResult(result: ActivityResult) {
                        if (result.resultCode == Activity.RESULT_OK) {
                            result.data?.let { intentData ->
                                if (intentData.hasExtra(KEY_RETURN_LANGUAGE_DATA)) {
                                    val languageItem =
                                        intentData.extras?.serializable<LanguageItem>(
                                            KEY_RETURN_LANGUAGE_DATA
                                        )
                                    languageItem?.let {

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
                                        sendBroadcast(Intent().setAction(ACTION_LANGUAGE_CHANGE))
                                    }

                                    ActivityCompat.recreate(mActivity)
                                }
                            }
                        }
                    }
                }
            )
        }


        mBinding.buttonWebView.setOnClickListener {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
                .setUrlBarHidingEnabled(false)
                .setInstantAppsEnabled(false)
                .setShowTitle(true)
                .setCloseButtonIcon(
                    BitmapFactory.decodeResource(mActivity.resources, R.drawable.ic_back)
                )
                .setColorScheme(CustomTabsIntent.COLOR_SCHEME_LIGHT)
                .setDefaultColorSchemeParams(
                    CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(ContextCompat.getColor(mActivity, R.color.white))
                        .setNavigationBarColor(ContextCompat.getColor(mActivity, R.color.white))
                        .setSecondaryToolbarColor(ContextCompat.getColor(mActivity, R.color.white))
                        .setNavigationBarDividerColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.white
                            )
                        )
                        .build()
                )
                .build()
            CustomTabActivityHelper.openCustomTab(
                mActivity,
                customTabsIntent,
                Uri.parse("https://policycreator.net/p/hashone/139/postplus"),
                "Privacy Policy",
                WebViewFallback()
            )
        }
    }

    override fun onRestart() {
        val currentLocale = LocaleManager.getAppLocale()
        super.onRestart()
        isContains = false
        LocaleManager.mLanguagesList.forEach {
            if (it.languageCode == currentLocale?.toLanguageTag())
                isContains = true

        }
    }

    override fun onResume() {
        super.onResume()
        if (!isContains) {
            ActivityCompat.recreate(mActivity)
            return
        }
        mBinding.buttonContactus.text =
            getString(com.hashone.commons.test.R.string.label_contact)
        mBinding.buttonLanguage.text =
            getString(com.hashone.commons.test.R.string.label_language)
        mBinding.buttonWebView.text =
            getString(com.hashone.commons.test.R.string.label_webview)
    }
}