package com.hashone.commons

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hashone.commons.contactus.ContactUs
import com.hashone.commons.test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonContactus.setOnClickListener {
            ContactUs.open(activity = this, ContactUs.build(
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
                allowPhotosOnly = false,
                allowVideosOnly = false,
                allowBoth = true,
                maxFileSize = 15L,
                //TODO: Ratio (messageBoxHeight / ScreenWidth)
                messageBoxHeight = 0.4444444444,
                //TODO: Ratio (attachmentBoxHeight / ScreenWidth)
                attachmentBoxHeight = 0.588888888888889,
                optionItemsList = ArrayList<ContactUs.Builder.OptionItem>().apply {
                    add(ContactUs.Builder.OptionItem(
                        text = "Feedback",
                        message = "Type your feedback here.",
                        isChecked = true
                    ))
                    add(ContactUs.Builder.OptionItem(
                        text = "Issue",
                        message = "Please describe issue in detail.",
                        isChecked = false
                    ))
                    add(ContactUs.Builder.OptionItem(
                        text = "Request",
                        message = "Add your request here.",
                        isChecked = false
                    ))
                }
            ) {
//                //TODO: Screen
//                isFullScreen = false
//                windowBackgroundColor = R.color.extra_extra_light_gray
//                statusBarColor = R.color.extra_extra_light_gray
//                navigationBarColor = R.color.extra_extra_light_gray
//                //TODO: Toolbar
//                toolBarColor = R.color.white
//                backPressIcon = R.drawable.ic_back_contact_us
//                backPressIconDescription = ""
//                toolBarTitle = ""
//                toolBarTitleColor = R.color.black
//                toolBarTitleFont = R.font.outfit_semi_bold
//                toolBarTitleSize = 16F
//                //TODO: Radio Buttons
//                radioButtonTextColor = R.color.black
//                radioButtonTextFont = R.font.roboto_medium
//                radioButtonTextSize = 14F
//                //TODO: Message UI
//                messageCardBackgroundColor = R.color.extra_extra_light_gray
//                messageCardBackgroundRadius = 8F
//                messageHint = ""
//                message = ""
//                messageColor = R.color.black
//                messageFont = R.font.roboto_medium
//                messageSize = 14F
//                //TODO: Attachment UI
//                attachmentCardBackgroundColor = R.color.extra_extra_light_gray
//                attachmentCardBackgroundRadius = 8F
//                attachmentBackgroundColor = R.color.white
//                attachmentBackgroundRadius = 8F
//                attachmentTitle = ""
//                attachmentTitleColor = R.color.light_gray
//                attachmentTitleFont = R.font.roboto_medium
//                attachmentTitleSize = 14F
//                attachmentIcon = R.drawable.ic_contact_us_add_attachment
//                attachmentDeleteIcon = R.drawable.ic_contact_us_img_delete
//                //TODO: Action button
//                buttonBackgroundInactiveColor = R.color.light_gray
//                buttonBackgroundColor = R.color.black
//                buttonRadius = 30F
//                buttonText = ""
//                buttonTextColor = R.color.white
//                buttonTextFont = R.font.outfit_bold
//                buttonTextSize = 16F
            })
        }
    }
}