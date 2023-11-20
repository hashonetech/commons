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
	implementation 'com.github.hashonetech:commons:v1.2.5'
}
```

### 📸 Screenshot
 <img alt="App image" src="https://github.com/hashonetech/commons/blob/master/screenshots/commons_1.png" width="22%"> &nbsp;&nbsp;&nbsp; 
 <img alt="App image" src="https://github.com/hashonetech/commons/blob/master/screenshots/commons_2.png" width="22%"> &nbsp;&nbsp;&nbsp; 
 <img alt="App image" src="https://github.com/hashonetech/commons/blob/master/screenshots/commons_3.png" width="22%"> &nbsp;&nbsp;&nbsp; 
 <img alt="App image" src="https://github.com/hashonetech/commons/blob/master/screenshots/commons_4.png" width="22%"> 

Table of contents
=================

<!--ts-->
* [AndroidManifest](#androidmanifest)
* [MyApplication](#myapplication)
* [BaseActivity](#baseactivity)
* [ContactUs](#contactus)
* [Language](#language)
* [Chromium WebView](#chromium-webview)
* [In-App Billing](#in-app-billing)
	* [Initialize BillingClient](#initialize-billingclient)
 	* [Checking User Premium Status](#checking-user-premium-status)
  	* [Retrieving Product and Subscription Details](#retrieving-product-and-subscription-details)
  	* [Setting Up Pro UI Items](#setting-up-pro-ui-items)
  	* [Purchase Process](#purchase-process)
  	* [Handling Purchase Events](#handling-purchase-events)
  	* [Handling Purchase](#handling-purchase)
  	* [PurchaseHistory](#purchasehistory)
* [Extension functions](https://github.com/hashonetech/commons/tree/master/commons/src/main/java/com/hashone/commons/extensions)
* [FlexBox](https://github.com/hashonetech/commons/tree/master/commons/src/main/java/com/hashone/commons/module/flexbox)
* [Utils](https://github.com/hashonetech/commons/tree/master/commons/src/main/java/com/hashone/commons/utils)
<!--te-->

 ### AndroidManifest

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

 ### MyApplication
 ```kotlin
//TODO: Extends Application class with CommonApplication:
MyApplication: CommonApplication()
```
  
 ### BaseActivity
 ```kotlin
//TODO: Extends BaseActivity class with Common Lib BaseActivity:
BaseActivity: BaseActivity()
```

### ContactUs
```kotlin
//TODO: Builder inner variable name updated

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
	    //TODO: Use for Premium purchase details
	    isPremium = false,
	    purchasedTitle = "",
	    orderId = "",
	    //TODO: If you want to pass content details use (contentId, contentTitle)
	    contentId = "",
	    contentTitle = "",
	    showKeyboard = true/false,
	),
	mediaBuilder = ContactUs.MediaBuilder(
	    //TODO: Show only Photo
	    allowPhotosOnly = true/false,
	    //TODO: Show only Video
	    allowVideosOnly = true/false,
	    //TODO: Show Photo and Video
	    allowBoth = true/false,
	    //TODO: Max file size in MB
	    maxFileSize = 15L,
	    //TODO: Ratio (messageBoxHeight / ScreenWidth)
	    messageBoxHeight = 0.4444444444,
	    //TODO: Ratio (attachmentBoxHeight / ScreenWidth)
	    attachmentBoxHeight = 0.588888888888889,
	    //TODO: Radio Button Item
	    optionItemsList = ArrayList<ContactUs.OptionItem>().apply {
		add(
		    ContactUs.OptionItem(
			text = "Feedback",
			message = "Type your feedback here.",
			isChecked = true/false
		    )
		)
		add(
		    ContactUs.OptionItem(
			text = "Issue",
			message = "Please describe issue in detail.",
			isChecked = true/false
		    )
		)
		add(
		    ContactUs.OptionItem(
			text = "Request",
			message = "Add your request here.",
			isChecked = true/false
		    )
		)
	    }),


	) {
	//TODO: Screen
	screenBuilder = ContactUs.ScreenBuilder(
	    isFullScreen = true/false,
	    windowBackgroundColor = R.color.extra_extra_light_gray,
	    statusBarColor = R.color.extra_extra_light_gray,
	    navigationBarColor = R.color.extra_extra_light_gray,
	)

	//TODO: Toolbar
	toolBarBuilder = ContactUs.ToolBarBuilder(
		toolBarColor = R.color.white,
		backIcon = R.drawable.ic_back,
		title = getString(com.hashone.commons.test.R.string.label_language),
		titleColor = R.color.black,
		titleFont = R.font.roboto_medium,
		titleSize = 16F,
	    )

	//TODO: Radio Buttons
	radioButtonBinding = ContactUs.RadioButtonBuilder(
	    selectedColor = R.color.black,
	    //TODO: New, now you set radio button default color
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
```

### Language

**Notes:** From now, XML Strings for Language Code and Name logic removed, You must prepare List

```gradle
//TODO: App implementation - In app.gradle file
resourceConfigurations += ["af", "en", "bn", "de-rDE", "es", "fil", "fr", "in", "it", "pt", "ru", "tr", "uk","zh-rCN" "b+zh+Hans+MO", "zh-rTW", "hi"]

```

* Add locale_config file in **app > src > main > res > xml** - [locale_config.xml](https://github.com/hashonetech/commons/blob/master/app/src/main/res/xml/locale_configs.xml)

```xml
<application
        ......
        android:localeConfig="@xml/locale_configs"
        ......>
        ......

        <!--//TODO: App implementation-->
        <!-- Let AndroidX handle auto-store locales for pre-T devices to hold the user's selected locale -->
        <service
            android:name="androidx.appcompat.app.AppLocalesMetadataHolderService"
            android:enabled="false"
            android:exported="false">
            <meta-data
                android:name="autoStoreLocales"
                android:value="true" />
        </service>

</application>
```

```kotlin

//TODO: Add this function in Your App Application class
override fun onCreate() {
	super.onCreate()
        mInstance = this
        //TODO: App implementation
        setupAppLocale()
}

//TODO: Add this function in Your App Application class
//TODO: App implementation
private fun setupAppLocale() {
	LocaleManager.prepareLanguageList(
	    arrayListOf(
		LanguageItem("bahasa Indonesia", "id", "Indonesian", false),
		LanguageItem("বাংলা", "bn", "Bangla", false),
		LanguageItem("Deutsche", "de-DE", "German", false),
		LanguageItem("English", "en", "", true),//TODO:No SubTitle
		LanguageItem("हिंदी", "hi", "Hindi", false),
		LanguageItem("Española", "es", "Spanish", false),
		LanguageItem("Filipino", "fil", "Filipino", false),
		LanguageItem("français", "fr", "French", false),
		LanguageItem("Italiano", "it", "Italian", false),
		LanguageItem("português", "pt", "Portuguese", false),
		LanguageItem("pусский", "ru", "Russian", false),
		LanguageItem("Türkçe", "tr", "Turkish", false),
		LanguageItem("yкраїнський", "uk", "Ukrainian", false),
		LanguageItem("Chinese Simplified", "zh-Hans", "Chinese Simplified", false),
		LanguageItem(
		    "Chinese (Taiwan) Traditional",
		    "zh-Hant",
		    "Chinese (Taiwan) Traditional",
		    false
		),
		LanguageItem(
		    "Chinese (Macao) Traditional",
		    "zh-Hant-MO",
		    "Chinese (Macao) Traditional",
		    false
		),
	    )
	)
}

//TODO: Builder inner variable name updated

override fun onStart() {
	val currentLocale = LocaleManager.getAppLocale()
	super.onStart()
	isContains = LocaleManager.isLocaleContains(currentLocale)
}

override fun onResume() {
        super.onResume()
        if (!isContains) {
            ActivityCompat.recreate(mActivity)
            return
        }
        //TODO: Update UI text
	......
}

//TODO: Update Selected Language item
LocaleManager.updateSelection()

mActivityLauncher.launch(
	Language.open(activity = this, Language.build(
	    languageItemsList = ArrayList<LanguageItem>().apply {
		add(
		    LanguageItem(
			languageName = "English",
			languageCode = "en",
			languageOriginalName = "United States",
			isChecked = true/false
		    )
		),
		 add(
		      LanguageItem(
			  languageName = "বাংলা",
			  languageCode = "bn",
			  languageOriginalName = "Bangla",
			  isChecked = true/false
			)
		),
		}
	) {

	    //TODO: Screen
	    screenBuilder = Language.ScreenBuilder(
		isFullScreen = true/false,
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
		paddingTop = 24,
		paddingBottom = 24,
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
```

### Chromium WebView
```kotlin
val customTabsIntent = CustomTabsIntent.Builder()
		.setShareState(CustomTabsIntent.SHARE_STATE_DEFAULT | CustomTabsIntent.SHARE_STATE_OFF | CustomTabsIntent.SHARE_STATE_ON)
		.setUrlBarHidingEnabled(true/false)
		.setInstantAppsEnabled(true/false)
		.setShowTitle(true/false)
		.setCloseButtonIcon(
		    BitmapFactory.decodeResource(mActivity.resources, R.drawable.ic_back)
		)
		.setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM | CustomTabsIntent.COLOR_SCHEME_DARK | CustomTabsIntent.COLOR_SCHEME_LIGHT)
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
		Uri.parse(<Your URL>),
		<Title>,
		WebViewFallback()
    )
```

## In-App Billing

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
To determine if a user has premium access, you can call the **isPremium()** method within your splash screen or any relevant location. Ensure you have an array of product IDs representing premium subscriptions and products.

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

Fetch the details of products and subscriptions to display related information in your Pro screen. Use the **queryAllProductDetails()** method and provide the list of product IDs

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

- Feel free to customize and adjust the provided code snippets as per your application's needs and design preferences.

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
To handle purchase events after the purchase is completed, implement the **onPurchaseReceived()** method. This method is triggered when a purchase event occurs.


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
// Note: you have to register the eventbus to retrieve callback 
```

### Handling Purchase

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

### License


```
Copyright 2023 Hashone Tech LLP

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements. See the NOTICE file distributed with this work for
additional information regarding copyright ownership. The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
