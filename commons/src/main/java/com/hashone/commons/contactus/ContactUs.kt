package com.hashone.commons.contactus

import android.app.Activity
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.FontRes
import androidx.annotation.LongDef
import androidx.compose.material3.RadioButton
import com.hashone.commons.R
import com.hashone.commons.languages.Language
import java.io.Serializable

open class ContactUs(val builder: Builder) : Serializable {

    companion object {
        inline fun build(
            //TODO: Email details
            emailBuilder: EmailBuilder,
            //TODO: Allow Media Pick
            mediaBuilder: MediaBuilder = MediaBuilder(),
            block: Builder.() -> Unit
        ) = Builder(
            emailBuilder,
            mediaBuilder,
        ).apply(block).build()

        fun open(activity: Activity, contactUs: ContactUs) {
            ContactUsActivity.newIntent(context = activity, contactUs = contactUs)
                .also { activity.startActivity(it) }
        }
    }

    class Builder(
        var emailBuilder: EmailBuilder,
        //TODO: Allow Media Pick
        var mediaBuilder: MediaBuilder = MediaBuilder()

    ) : Serializable {

        //TODO: Screen
        var screenBuilder = ScreenBuilder()

        //TODO: Toolbar
        var toolBarBuilder = ToolBarBuilder()

        //TODO: Radio Buttons
        var radioButtonBinding = RadioButtonBuilder()

        //TODO: Message UI
        var messageBuilder = MessageBuilder()

        //TODO: Attachment UI
        var attachmentBuilder = AttachmentBuilder()

        //TODO: Action button
        var actionButtonBuilder = ActionButtonBuilder()

        fun build() = ContactUs(this)
    }

    class ScreenBuilder(
        var isFullScreen: Boolean = false,
        @ColorRes
        var windowBackgroundColor: Int = R.color.white,
        @ColorRes
        var statusBarColor: Int = R.color.extra_extra_light_gray,
        @ColorRes
        var navigationBarColor: Int = R.color.extra_extra_light_gray,
    ) : Serializable

    class ToolBarBuilder(
        @ColorRes
        var toolBarColor: Int = R.color.extra_extra_light_gray,
        @DrawableRes
        var backPressIcon: Int = R.drawable.ic_back_contact_us,
        var backPressIconDescription: String = "",
        var toolBarTitle: String = "",
        @ColorRes
        var toolBarTitleColor: Int = R.color.black,
        @FontRes
        var toolBarTitleFont: Int = R.font.outfit_semi_bold,
        @FloatRange
        var toolBarTitleSize: Float = 16F,
    ) : Serializable

    class RadioButtonBuilder(
        @ColorRes
        var radioButtonTextColor: Int = R.color.black,
        @FontRes
        var radioButtonTextFont: Int = R.font.roboto_medium,
        @FloatRange
        var radioButtonTextSize: Float = 14F,
    ) : Serializable

    class MessageBuilder(
        @ColorRes
        var messageCardBackgroundColor: Int = R.color.extra_extra_light_gray,
        @FloatRange
        var messageCardBackgroundRadius: Float = 8F,
        var messageHint: String = "",
        @ColorRes
        var messageHintColor: Int = R.color.light_gray,
        var message: String = "",
        @ColorRes
        var messageColor: Int = R.color.black,
        @FontRes
        var messageFont: Int = R.font.roboto_medium,
        @FloatRange
        var messageSize: Float = 14F,
    ) : Serializable

    class AttachmentBuilder(
        @ColorRes
        var attachmentCardBackgroundColor: Int = R.color.extra_extra_light_gray,
        @FloatRange
        var attachmentCardBackgroundRadius: Float = 8F,
        @ColorRes
        var attachmentBackgroundColor: Int = R.color.white,
        @FloatRange
        var attachmentBackgroundRadius: Float = 8F,
        var attachmentTitle: String = "",
        @ColorRes
        var attachmentTitleColor: Int = R.color.light_gray,
        @FontRes
        var attachmentTitleFont: Int = R.font.roboto_medium,
        @FloatRange
        var attachmentTitleSize: Float = 14F,
        @DrawableRes
        var attachmentIcon: Int = R.drawable.ic_contact_us_add_attachment,
        @DrawableRes
        var attachmentDeleteIcon: Int = R.drawable.ic_contact_us_img_delete,
    ) : Serializable

    class ActionButtonBuilder(
        @ColorRes
        var buttonBackgroundInactiveColor: Int = R.color.light_gray,
        @ColorRes
        var buttonBackgroundColor: Int = R.color.black,
        @FloatRange
        var buttonRadius: Float = 30F,
        var buttonText: String = "",
        @ColorRes
        var buttonTextColor: Int = R.color.white,
        @FontRes
        var buttonTextFont: Int = R.font.outfit_bold,
        @FloatRange
        var buttonTextSize: Float = 16F,
    ) : Serializable

    class EmailBuilder(
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
        var contentId: String = "",
        var contentTitle: String = "",
        var showKeyboard: Boolean = false,
    ) : Serializable
    class MediaBuilder(
        var allowPhotosOnly: Boolean = false,
        var allowVideosOnly: Boolean = false,
        var allowBoth: Boolean = true,
        var maxFileSize: Long = 0L,
        var messageBoxHeight: Double = 0.0,
        var attachmentBoxHeight: Double = 0.0,
        var optionItemsList: ArrayList<OptionItem> = arrayListOf()
    ) : Serializable

    data class OptionItem(
        val text: String = "",
        val message: String = "",
        val isChecked: Boolean = false
    ) : Serializable

}