# Commons
Used for basic functions and data.

[![](https://jitpack.io/v/hashonetech/Commons.svg)](https://jitpack.io/#hashonetech/Commons)

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
	dependencies {
	        implementation 'com.github.hashonetech:Commons:v1.0.6'
	}

Call below function to open ContactUs Screen

	fun openContactUs(activity: Activity) {
	    ContactUs.open(activity, build(
	    	//TODO: Required
		emailTitle = "Email Title",
		feedbackEmail = "abc@email.com",
		appName = "App Name",
		packageName = "PackageName",
		versionName = "App Version Name",
		
		//TODO: Optional
		androidDeviceToken = "Device Token",
		countryCode = "Country Code",
		isPremium = true/false,
		purchasedTitle = "Purchase Title",
		orderId = "Order Id",
		showKeyboard = true/false,
		allowPhotosOnly = true/false,
		allowVideosOnly = true/false,
		allowBoth = true/false,
		messageBoxHeight: Double = 0.0,
            	attachmentBoxHeight: Double = 0.0,
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
	}
