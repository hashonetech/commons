# Commons
Used for basic functions and data.

[![](https://jitpack.io/v/hashonetech/commons.svg)](https://jitpack.io/#hashonetech/commons)

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
	dependencies {
	        implementation 'com.github.hashonetech:commons:v1.0.19'
	}
## 📸 Screenshot

 <img alt="App image" src="https://github.com/hashonetech/commons/assets/104345897/b494ac15-8716-462e-bc3b-c97f34c3b298" width="30%"> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
 <img alt="App image" src="https://github.com/hashonetech/commons/assets/104345897/435c7343-778b-4bfd-bdd3-605bb91eb162" width="30%"> 


 ## AndroidManifest.xml

	<application
		...
		tools:replace="android:theme,android:name">
		...

		<provider
		    android:name="androidx.core.content.FileProvider"
		    android:authorities="${applicationId}.provider"
		    ...
		    tools:replace="android:resource"
		    ...>
		</provider>
	 </application>

 ## MyApplication
 	//TODO: Extends Application class with CommonApplication:
 	MyApplication: CommonApplication()
  
 ## BaseActivity
 	//TODO: Extends BaseActivity class with Common Lib BaseActivity:
 	BaseActivity: BaseActivity()
  
## ContactUs

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
                    statusBarColor = R.color.extra_extra_light_gray,
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
                    messageCardBackgroundColor = R.color.extra_extra_light_gray,
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
                )
            })
 
## Language

	mActivityLauncher.launch(
                Language.open(activity = this, Language.build(
                    languageItemsList = ArrayList<LanguageItem>().apply {
                	add(
                            LanguageItem(
                                languageName = "English",
                                languageCode = "en",
                                isChecked = true
                            )
                        ),
			 add(
	                      LanguageItem(
	                          languageName = "বাংলা",
	                          languageCode = "bn",
	                          isChecked = false
	                        )
	                ),
			}
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
                        titlePaddingStart = 16,
                        titlePaddingEnd = 0,
                        titlePaddingTop = 24,
                        titlePaddingBottom = 24,
                        iconPaddingStart = 8,
                        iconPaddingEnd = 8,
                        iconPaddingTop = 8,
                        iconPaddingBottom = 8,
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
                                        val localeList =
                                            LocaleListCompat.forLanguageTags(languageItem.languageCode)
                                        AppCompatDelegate.setApplicationLocales(localeList)

                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
