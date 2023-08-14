# Commons
Used for basic functions and data.

[![](https://jitpack.io/v/hashonetech/commons.svg)](https://jitpack.io/#hashonetech/commons)

```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
	dependencies {
	        implementation 'com.github.hashonetech:commons:v1.0.20'
	}
```

## 📸 Screenshot

 <img alt="App image" src="https://github.com/hashonetech/commons/assets/104345897/b494ac15-8716-462e-bc3b-c97f34c3b298" width="30%"> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;
 <img alt="App image" src="https://github.com/hashonetech/commons/assets/104345897/435c7343-778b-4bfd-bdd3-605bb91eb162" width="30%"> 


 ## AndroidManifest.xml

```xml
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
```

 ## MyApplication
 ```kotlin
 	//TODO: Extends Application class with CommonApplication:
 	MyApplication: CommonApplication()
```
  
 ## BaseActivity
 ```kotlin
 	//TODO: Extends BaseActivity class with Common Lib BaseActivity:
 	BaseActivity: BaseActivity()
```

## ContactUs
```kotlin
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
```

## Language
```kotlin
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
```

# In-App Billing

### Initialize BillingClient

```kotlin
	MyApplication: CommonApplication(){
  		override fun onCreate() {
    		super.onCreate()
    		PurchaseManager.initializeBillingClient(this)
		}
	}
```

### Checking User Premium Status
To determine if a user has premium access, you can call the isPremium method within your splash screen or any relevant location. Ensure you have an array of product IDs representing premium subscriptions and products.

```kotlin
	val premiumArray = arrayListOf(Constants.SUB_YEAR, Constants.SUB_MONTH, Constants.PROD_LIFETIME)
	PurchaseManager.isPremium(premiumArray) { isPremium ->
		if (isPremium) {
    		// User is premium
    		// Proceed with premium features
		} else {
    		// User is not premium
    		// Provide non-premium features or prompt to upgrade
	 	}
	}
```

### Retrieving Product and Subscription Details

Fetch the details of products and subscriptions to display related information in your Pro screen. Use the queryAllProductDetails method and provide the list of product IDs

```kotlin
	val subscriptions = listOf(Constants.SUB_YEAR, Constants.SUB_MONTH)
	val products = listOf(Constants.PROD_LIFETIME)

	PurchaseManager.queryAllProductDetails(subscriptions, products, object : PurchaseListener() {
 
		override fun onBillingError(responseCode: Int, debugMessage: String) {
                	// Handle error
            	}

		override fun onProductDetail(productDetails: List<ProductDetails>) {
    			runOnUiThread {
        		productDetails.forEach { product ->
            		setupProUIItem(product)           		
        		}

			setSelection(Constants.SUB_YEAR)
			// Example usage: pre-select the subscription
    		   }
		}
	})
```

### Setting Up Pro UI Items
In order to display subscription and product details on your Pro screen, you can use the following example code. This code sets up the UI elements based on the retrieved **ProductDetails**.

-Feel free to customize and adjust the provided code snippets as per your application's needs and design preferences.

```kotlin
	private fun setupProUIItem(skuDetails: ProductDetails) {
        try {
            var trialString = ""
            var hasTrail = false

	    // Example of PurchaseManager.getFreeTrial(skuDetails) function

            if (PurchaseManager.getFreeTrial(skuDetails).isNotEmpty()) {
                hasTrail = true

                val trialPeriod = PurchaseManager.getFreeTrial(skuDetails)[PurchaseManager.TrialPeriod.KEY_OFFER_DURATION.name].orEmpty()
                val dayOrMonth = PurchaseManager.getFreeTrial(skuDetails)[PurchaseManager.TrialPeriod.KEY_OFFER_TYPE.name]

                when {
                    dayOrMonth.equals(PurchaseManager.TrialPeriod.WEEK.name, ignoreCase = true) -> {
                        trialString = "$trialPeriod Days Trial"
			// Note if trial period is week it return 7 day so no need to caluclate day * 7
                    }

                    dayOrMonth.equals(PurchaseManager.TrialPeriod.DAY.name, ignoreCase = true) -> {
                        trialString = "$trialPeriod Days Trial"
                    }

                    dayOrMonth.equals(PurchaseManager.TrialPeriod.MONTH.name, ignoreCase = true) -> {
                        trialString = "$trialPeriod Month Trial"
                    }

                    else -> {
                    }
                }
            }

            if (MyApplication.mInstance.isCountryExcluded()) hasTrail = false

            // Example of PurchaseManager.getFormattedPrice(skuDetails) function.

            when (skuDetails.productId) {
                Constants.SUB_MONTH -> {
                    monthSkuDetails = skuDetails
                    val subtitleString = if (hasTrail) {
                        trialString
                    } else {
                        "Start today"
                    }
                    val titleString = "${
                        PurchaseManager.getFormattedPrice(skuDetails)
                    }/ Month"

                }

                Constants.LIFETIME_PACK -> {
                    lifeTimeSkuDetails = skuDetails
                     val titleString = String.format("%s for Lifetime", PurchaseManager.getFormattedPrice(skuDetails))
                }

                Constants.SUB_YEAR -> {
                    yearSkuDetails = skuDetails
                    val subtitleString = if (hasTrail) {
                        trialString
                    } else {
                        "Start today"
                    }
                    val titleString = "${
                        PurchaseManager.getFormattedPrice(skuDetails)
                    }/ Year"
                    
		    // Example of PurchaseManager.getPriceValue(skuDetails) function.

                    val yearMonthValue = PurchaseManager.getPriceValue(skuDetails) * 12
                    val yearValue = PurchaseManager.getPriceValue(skuDetails)
                    val finalPercentage = abs(100F - ((100 * yearValue) / yearMonthValue))
                    
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
```
### Purchase Process

**Note:** purchaseSubscription & purchaseProduct is an Extension function which requires activity to use.

```kotlin
	binding.layoutContinue.setOnClickListener {
                if (Utils.checkClickTime()) {
                    if (Utils.isNetworkAvailable(mActivity)) {
                        if (selectedSkuId.isNotEmpty()) {
                            if (selectedSkuId == Constants.PROD_LIFETIME) {
                                lifeTimeSkuDetails?.let { it1 -> purchaseProduct(it1) }
                            } else {
                                (if (selectedSkuId == Constants.SUB_MONTH) monthSkuDetails else yearSkuDetails)?.let { it1 -> purchaseSubscription(it1) }
                            }
                        }
                    }
                }
            }
```
### Handling Purchase Events
To handle purchase events after the purchase is completed, implement the onPurchaseReceived method. This method is triggered when a purchase event occurs.


Subscribe to the event using @Subscribe annotation and specify the thread mode.
```kotlin
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onPurchaseReceived(event: PurchaseEventModel?) {
		event?.let { it1 ->
    		if (it1.isPurchaseSuccess) {
        		it1.purchaseData?.let { handlePurchase(it)
			}
    		} else {
        		Log.d("onPurchaseReceived", "it1 ${it1.errorCode}")
    		}
	   }
	}
	// Note: you have to register the event bus to retrieve callback 
```

Handling Purchase

```kotlin
	private fun handlePurchase(purchase: PurchaseData) {
    	PurchaseManager.storePurchaseData?.savePurchaseData(CURRENT_PURCHASE, purchase)
    	PurchaseManager.storePurchaseData?.setPremiumPurchase(true)
    	showSuccessScreen()
	}
	// Note: You have to store purchase related data in preference.
	// if purchased item is premium then save in **CURRENT_PURCHASE key** & setPremiumPurchase **true** otherwise save as **productId**.
```
### PurchaseHistory:
Retrieves the purchase history of both subscription and in-app products and notifies the listener with the history.

```kotlin
	PurchaseManager.getPurchaseHistory(object : PurchaseListener() {
	override fun onPurchaseHistoryReceived(historyList: List<HistoryPurchaseData>) {
    		if (historyList.isNotEmpty()) {
        		// Display purchase history to the user
        		for (history in historyList) {
            		val productType = if (history.productType == ProductType.SUBS) "Subscription" else "In-App"
            		val autoRenewingStatus = if (history.isAutoRenewing) "Auto-Renewing" else "Not Auto-Renewing"
            		Log.d("PurchaseHistory", "Product: ${history.productId}, Type: $productType, Order ID: ${history.orderId}, Time: ${history.purchaseTime}, $autoRenewingStatus")
        	}
    		} else {
        	// No purchase history available
        	Log.d("PurchaseHistory", "No purchase history available.")
    		}
	  }
	})


	//Below is sample function to convert history data to JSONArray for our Userdetail API.

        fun convertHistoryDataToJsonArray(array: ArrayList<HistoryPurchaseData>): JSONArray {
    		val jsonArray = JSONArray()
    		array.forEach { historyData ->
        		jsonArray.put(JSONObject().apply {
            		put("product_id", historyData.productId)
            		put("order_id", historyData.orderId)
            		put("purchase_time", historyData.purchaseTime)
            		qput("auto_renew", historyData.autoRenew)
        		})
    		}
    	return jsonArray
	}
```
