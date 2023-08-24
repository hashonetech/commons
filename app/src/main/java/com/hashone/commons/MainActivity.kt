package com.hashone.commons

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.hashone.commons.base.BaseActivity
import com.hashone.commons.base.BetterActivityResult
import com.hashone.commons.base.CommonApplication
import com.hashone.commons.contactus.ContactUs
import com.hashone.commons.extensions.getLocaleString
import com.hashone.commons.extensions.serializable
import com.hashone.commons.languages.Language
import com.hashone.commons.languages.LanguageActivity.Companion.KEY_RETURN_LANGUAGE_DATA
import com.hashone.commons.languages.LanguageItem
import com.hashone.commons.languages.LocaleHelper
import com.hashone.commons.test.databinding.ActivityMainBinding
import com.hashone.commons.utils.ACTION_LANGUAGE_CHANGE
import com.hashone.commons.utils.DEFAULT_LANGUAGE
import com.hashone.commons.utils.DEFAULT_LANGUAGE_NAME

class MainActivity : BaseActivity() {

    lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.buttonContactus.text =
            CommonApplication.mInstance.getText(com.hashone.commons.test.R.string.label_contact)
        mBinding.buttonLanguage.text =
            CommonApplication.mInstance.getText(com.hashone.commons.test.R.string.label_language)

        mBinding.buttonContactus.setOnClickListener {
            ContactUs.open(activity = this, ContactUs.build(
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
               /* screenBuilder = ContactUs.ScreenBuilder(
                    isFullScreen = false,
                    windowBackgroundColor = R.color.extra_extra_light_gray,
                    statusBarColor = R.color.white,
                    navigationBarColor = R.color.extra_extra_light_gray,
                )

                //TODO: Toolbar
                toolBarBuilder = ContactUs.ToolBarBuilder(
                    toolBarColor = R.color.white,
                    backPressIcon = R.drawable.ic_back_contact_us,
                    backPressIconDescription = "",
                    toolBarTitle = "",
                    toolBarTitleColor = R.color.black,
                    toolBarTitleFont = R.font.outfit_semi_bold,
                    toolBarTitleSize = 16F,
                )

                //TODO: Radio Buttons
                radioButtonBinding = ContactUs.RadioButtonBuilder(
                    radioButtonTextColor = R.color.black,
                    radioButtonTextFont = R.font.roboto_medium,
                    radioButtonTextSize = 14F,
                )

                //TODO: Message UI
                messageBuilder = ContactUs.MessageBuilder(
                    messageCardBackgroundColor = R.color.white,
                    messageCardBackgroundRadius = 8F,
                    messageHint = "",
                    message = "",
                    messageColor = R.color.black,
                    messageFont = R.font.roboto_medium,
                    messageSize = 14F,
                )

                //TODO: Attachment UI
                attachmentBuilder = ContactUs.AttachmentBuilder(
                    attachmentCardBackgroundColor = R.color.extra_extra_light_gray,
                    attachmentCardBackgroundRadius = 8F,
                    attachmentBackgroundColor = R.color.white,
                    attachmentBackgroundRadius = 8F,
                    attachmentTitle = "",
                    attachmentTitleColor = R.color.light_gray,
                    attachmentTitleFont = R.font.roboto_medium,
                    attachmentTitleSize = 14F,
                    attachmentIcon = R.drawable.ic_contact_us_add_attachment,
                    attachmentDeleteIcon = R.drawable.ic_contact_us_img_delete,
                )

                //TODO: Action button
                actionButtonBuilder = ContactUs.ActionButtonBuilder(
                    buttonBackgroundInactiveColor = R.color.light_gray,
                    buttonBackgroundColor = R.color.black,
                    buttonRadius = 30F,
                    buttonText = "",
                    buttonTextColor = R.color.white,
                    buttonTextFont = R.font.outfit_bold,
                    buttonTextSize = 16F,
                )*/
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

        val languageList = LanguageItem().getLanguages(this)


        mBinding.buttonLanguage.setOnClickListener {
            languageList.forEachIndexed { index, languageItem ->
                languageItem.isChecked = (languageItem.languageCode == CommonApplication.mInstance.mStoreUserData.getString(DEFAULT_LANGUAGE))
                if (languageItem.isChecked) {
                    val localContext = LocaleHelper.setLocale(mActivity, languageItem.languageCode)
                    CommonApplication.mInstance.setLocaleContext(localContext!!)
                }
            }

            Language.open(activity = this, Language.build(
                languageItemsList = languageList
            ) {
                toolBarBuilder = Language.ToolBarBuilder(
                    toolBarColor = R.color.white,

                    )
            })

            mActivityLauncher.launch(
                Language.open(activity = this, Language.build(
                    languageItemsList = languageList
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
                        title = getLocaleString(com.hashone.commons.test.R.string.label_language),
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
                                            DEFAULT_LANGUAGE_NAME,
                                            languageItem.languageName
                                        )

                                        val localContext =
                                            LocaleHelper.setLocale(
                                                mActivity,
                                                languageItem.languageCode
                                            )
                                        CommonApplication.mInstance.mStoreUserData.setString(
                                            DEFAULT_LANGUAGE,
                                            languageItem.languageCode
                                        )
                                        CommonApplication.mInstance.mStoreUserData.setString(
                                            DEFAULT_LANGUAGE_NAME,
                                            languageItem.languageName
                                        )
                                        CommonApplication.mInstance.setLocaleContext(localContext!!)
                                        sendBroadcast(Intent().setAction(ACTION_LANGUAGE_CHANGE))
                                        /*Below Code use to tell System to set App language*/
                                        val localeList = LocaleListCompat.forLanguageTags(languageItem.languageCode)
                                        AppCompatDelegate.setApplicationLocales(localeList)

                                        mBinding.buttonContactus.text =
                                            getLocaleString(com.hashone.commons.test.R.string.label_contact)
                                        mBinding.buttonLanguage.text =
                                            getLocaleString(com.hashone.commons.test.R.string.label_language)
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }

    }
}