package com.hashone.commons.contactus

import android.app.Activity
import com.hashone.commons.R
import java.io.Serializable

open class ContactUs constructor(val builder: Builder) : Serializable {

    companion object {
        inline fun build(
            //TODO: Email details
            emailTitle: String,
            feedbackEmail: String,
            appName: String,
            packageName: String,
            versionName: String,
            androidDeviceToken: String = "",
            customerNumber: String = "",
            countryCode: String = "",
            isPremium: Boolean = false,
            purchasedTitle: String = "",
            orderId: String = "",
            showKeyboard: Boolean = false,
            //TODO: Allow Media Pick
            allowPhotosOnly: Boolean = false,
            allowVideosOnly: Boolean = false,
            allowBoth: Boolean = true,
            maxFileSize: Long = 25L,
            messageBoxHeight: Double = 0.4444444444,
            attachmentBoxHeight: Double = 0.588888888888889,
            optionItemsList: ArrayList<Builder.OptionItem> = arrayListOf(),
            block: Builder.() -> Unit
        ) = Builder(
            emailTitle,
            feedbackEmail,
            appName,
            packageName,
            versionName,
            androidDeviceToken,
            customerNumber,
            countryCode,
            isPremium,
            purchasedTitle,
            orderId,
            showKeyboard,
            allowPhotosOnly,
            allowVideosOnly,
            allowBoth,
            maxFileSize,
            messageBoxHeight,
            attachmentBoxHeight,
            optionItemsList
        ).apply(block).build()

        fun open(activity: Activity, contactUs: ContactUs) {
            ContactUsActivity.newIntent(context = activity, contactUs = contactUs)
                .also { activity.startActivity(it) }
        }
    }

    class Builder(
        var emailTitle: String,
        var feedbackEmail: String,
        var appName: String,
        var packageName: String,
        var versionName: String,
        var androidDeviceToken: String = "",
        var customerNumber: String = "",
        var countryCode: String = "",
        var isPremium: Boolean = false,
        var purchasedTitle: String = "",
        var orderId: String = "",
        var showKeyboard: Boolean = false,
        //TODO: Allow Media Pick
        var allowPhotosOnly: Boolean = false,
        var allowVideosOnly: Boolean = false,
        var allowBoth: Boolean = true,
        var maxFileSize: Long = 0L,
        var messageBoxHeight: Double = 0.0,
        var attachmentBoxHeight: Double = 0.0,
        var optionItemsList: ArrayList<OptionItem> = arrayListOf()
    ) : Serializable {
        //TODO: Screen
        var isFullScreen: Boolean = false
        var windowBackgroundColor: Int = R.color.white
        var statusBarColor: Int = R.color.extra_extra_light_gray
        var navigationBarColor: Int = R.color.extra_extra_light_gray

        //TODO: Toolbar
        var toolBarColor: Int = R.color.extra_extra_light_gray
        var backPressIcon: Int = R.drawable.ic_back_contact_us
        var backPressIconDescription: String = ""
        var toolBarTitle: String = ""
        var toolBarTitleColor: Int = R.color.black
        var toolBarTitleFont: Int = R.font.outfit_semi_bold
        var toolBarTitleSize: Float = 16F

        //TODO: Radio Buttons
        var radioButtonTextColor: Int = R.color.black
        var radioButtonTextFont: Int = R.font.roboto_medium
        var radioButtonTextSize: Float = 14F

        //TODO: Message UI
        var messageCardBackgroundColor: Int = R.color.extra_extra_light_gray
        var messageCardBackgroundRadius: Float = 8F
        var messageHint: String = ""
        var messageHintColor: Int = R.color.light_gray
        var message: String = ""
        var messageColor: Int = R.color.black
        var messageFont: Int = R.font.roboto_medium
        var messageSize: Float = 14F

        //TODO: Attachment UI
        var attachmentCardBackgroundColor: Int = R.color.extra_extra_light_gray
        var attachmentCardBackgroundRadius: Float = 8F
        var attachmentBackgroundColor: Int = R.color.white
        var attachmentBackgroundRadius: Float = 8F
        var attachmentTitle: String = ""
        var attachmentTitleColor: Int = R.color.light_gray
        var attachmentTitleFont: Int = R.font.roboto_medium
        var attachmentTitleSize: Float = 14F
        var attachmentIcon: Int = R.drawable.ic_contact_us_add_attachment
        var attachmentDeleteIcon: Int = R.drawable.ic_contact_us_img_delete

        //TODO: Action button
        var buttonBackgroundInactiveColor: Int = R.color.light_gray
        var buttonBackgroundColor: Int = R.color.black
        var buttonRadius: Float = 30F
        var buttonText: String = ""
        var buttonTextColor: Int = R.color.white
        var buttonTextFont: Int = R.font.outfit_bold
        var buttonTextSize: Float = 16F

        data class OptionItem(
            val text: String = "",
            val message: String = "",
            val isChecked: Boolean = false
        ) : Serializable

        fun build() = ContactUs(this)
    }
}