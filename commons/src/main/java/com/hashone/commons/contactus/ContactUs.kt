package com.hashone.commons.contactus

import android.app.Activity
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.FontRes
import com.hashone.commons.R
import java.io.Serializable

open class ContactUs(val builder: Builder) : Serializable {

    companion object {
        inline fun build(
            emailData: EmailData = EmailData("", ""),
            appData: AppData,
            purchases: Purchases,
            extraContents: HashMap<String, String> = hashMapOf(),
            exportToFile: Boolean = false,
            showKeyboard: Boolean = false,
            //TODO: Allow Media Pick
            mediaBuilder: MediaBuilder = MediaBuilder(),
            block: Builder.() -> Unit
        ) = Builder(
            emailData,
            appData,
            purchases,
            extraContents,
            exportToFile,
            showKeyboard,
            mediaBuilder,
        ).apply(block).build()

        fun open(activity: Activity, contactUs: ContactUs) {
            ContactUsActivity.newIntent(context = activity, contactUs = contactUs)
                .also { activity.startActivity(it) }
        }
    }

    class Builder(
        var emailData: EmailData,
        var appData: AppData,
        var purchases: Purchases,
        var extraContents: HashMap<String, String> = hashMapOf(),
        var exportToFile: Boolean = false,
        var showKeyboard: Boolean = false,
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
        var barColor: Int = R.color.extra_extra_light_gray,
        @DrawableRes
        var backIcon: Int = R.drawable.ic_back_contact_us,
        var backIconDescription: String = "",
        var title: String = "",
        @ColorRes
        var titleColor: Int = R.color.black,
        @FontRes
        var titleFont: Int = R.font.outfit_semi_bold,
        @FloatRange
        var titleSize: Float = 16F,
    ) : Serializable

    class RadioButtonBuilder(
        @ColorRes
        var selectedColor: Int = R.color.black,
        @ColorRes
        var defaultColor: Int = R.color.black,
        @FontRes
        var textFont: Int = R.font.roboto_medium,
        @FloatRange
        var textSize: Float = 14F,
    ) : Serializable

    class MessageBuilder(
        @ColorRes
        var backgroundColor: Int = R.color.extra_extra_light_gray,
        @FloatRange
        var backgroundRadius: Float = 8F,
        var hint: String = "",
        @ColorRes
        var hintColor: Int = R.color.light_gray,
        var message: String = "",
        @ColorRes
        var color: Int = R.color.black,
        @FontRes
        var font: Int = R.font.roboto_medium,
        @FloatRange
        var size: Float = 14F,
    ) : Serializable

    class AttachmentBuilder(
        @ColorRes
        var cardBackgroundColor: Int = R.color.extra_extra_light_gray,
        @FloatRange
        var cardBackgroundRadius: Float = 8F,
        @ColorRes
        var backgroundColor: Int = R.color.white,
        @FloatRange
        var backgroundRadius: Float = 8F,
        var title: String = "",
        @ColorRes
        var titleColor: Int = R.color.light_gray,
        @FontRes
        var titleFont: Int = R.font.roboto_medium,
        @FloatRange
        var titleSize: Float = 14F,
        @DrawableRes
        var addIcon: Int = R.drawable.ic_contact_us_add_attachment,
        @DrawableRes
        var deleteIcon: Int = R.drawable.ic_contact_us_img_delete,
    ) : Serializable

    class ActionButtonBuilder(
        @ColorRes
        var backgroundInactiveColor: Int = R.color.light_gray,
        @ColorRes
        var backgroundColor: Int = R.color.black,
        @FloatRange
        var radius: Float = 30F,
        var text: String = "",
        @ColorRes
        var textColor: Int = R.color.white,
        @FontRes
        var textFont: Int = R.font.outfit_bold,
        @FloatRange
        var textSize: Float = 16F,
    ) : Serializable

    class EmailData(
        var subject: String = "",
        var email: String
    ): Serializable

    class AppData(
        var mPackage: String,
        var name: String,
        var appVersionData: AppVersionData,
        var token: String = "",
        var customerNumber: String = "",
        var languageData: LanguageData = LanguageData(),
        var countryData: CountryData = CountryData()
    ): Serializable

    class CountryData(
        var name: String = "",
        var code: String = ""
    ): Serializable

    class AppVersionData(
        var name: String,
        var code: String
    ): Serializable

    class LanguageData(
        var name: String = "",
        var code: String = ""
    ): Serializable

    class Purchases(
        var isPremium: Boolean = false,
        var title: String = "",
        var orderId: String = "",
    ): Serializable

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