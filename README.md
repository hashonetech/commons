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
	        implementation 'com.github.hashonetech:commons:v1.0.13'
	}

In AndroidManifest.xml

	<application
		...
		tools:replace="android:theme">
		...

		<provider
		    android:name="androidx.core.content.FileProvider"
		    android:authorities="${applicationId}.provider"
		    ...
		    tools:replace="android:resource"
		    ...>
		</provider>
	 </application>

Call below function to open ContactUs Screen

	ContactUs.open(activity, build(
		//TODO: Required
		emailTitle = "Email Title",
		feedbackEmail = "abc@email.com",
		appName = "App Name",
		packageName = "PackageName",
		versionName = "App Version Name",

		//TODO: Optional
		androidDeviceToken = "Device Token",
		customerNumber = "",
		countryCode = "Country Code",
		isPremium = true/false,
		purchasedTitle = "Purchase Title",
		orderId = "Order Id",
		showKeyboard = true/false,
		allowPhotosOnly = true/false,
		allowVideosOnly = true/false,
		allowBoth = true/false,
		//TODO: In MB
		maxFileSize: Long = 25L,
		//TODO: Ratio (messageBoxHeight / ScreenWidth)
		messageBoxHeight = 0.4444444444,
		//TODO: Ratio (attachmentBoxHeight / ScreenWidth)
		attachmentBoxHeight = 0.588888888888889,
		optionItemsList = ArrayList<ContactUs.Builder.OptionItem>().apply {
		    add(
			ContactUs.Builder.OptionItem(
			    text = "Feedback",
			    message = "Type your feedback here.",
			    isChecked = true/false
			)
		    )
		    add(
			ContactUs.Builder.OptionItem(
			    text = "Issue",
			    message = "Please describe issue in detail.",
			    isChecked = false/false
			)
		    )
		}
	) {
		//TODO: Optional - Below fields are for UI theme.
		//TODO: Screen
		isFullScreen = false
		windowBackgroundColor = R.color.extra_extra_light_gray
		statusBarColor = R.color.extra_extra_light_gray
		navigationBarColor = R.color.extra_extra_light_gray

		//TODO: Toolbar
		toolBarColor = R.color.white
		backPressIcon = R.drawable.ic_back_contact_us
		backPressIconDescription = ""
		toolBarTitle = ""
		toolBarTitleColor = R.color.black
		toolBarTitleFont = R.font.outfit_semi_bold
		toolBarTitleSize = 16F

		//TODO: Radio Buttons
		radioButtonTextColor = R.color.black
		radioButtonTextFont = R.font.roboto_medium
		radioButtonTextSize = 14F

		//TODO: Message UI
		messageCardBackgroundColor = R.color.extra_extra_light_gray
		messageCardBackgroundRadius = 8F
		messageHint = ""
		messageHintColor = R.color.light_gray
		message = ""
		messageColor = R.color.black
		messageFont = R.font.roboto_medium
		messageSize = 14F

		//TODO: Attachment UI
		attachmentCardBackgroundColor = R.color.extra_extra_light_gray
		attachmentCardBackgroundRadius = 8F
		attachmentBackgroundColor = R.color.white
		attachmentBackgroundRadius = 8F
		attachmentTitle = ""
		attachmentTitleColor = R.color.light_gray
		attachmentTitleFont = R.font.roboto_medium
		attachmentTitleSize = 14F
		attachmentIcon = R.drawable.ic_contact_us_add_attachment
		attachmentDeleteIcon = R.drawable.ic_contact_us_img_delete

		//TODO: Action button
		buttonBackgroundInactiveColor = R.color.light_gray
		buttonBackgroundColor = R.color.black
		buttonRadius = 30F
		buttonText = ""
		buttonTextColor = R.color.white
		buttonTextFont = R.font.outfit_bold
		buttonTextSize = 16F
	})
