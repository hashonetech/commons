package com.hashone.commons.contactus

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.hashone.commons.R
import com.hashone.commons.base.BaseActivity
import com.hashone.commons.databinding.ActivityContactUsBinding
import com.hashone.commons.extensions.applyTextStyle
import com.hashone.commons.extensions.applyTintColor
import com.hashone.commons.extensions.corneredDrawable
import com.hashone.commons.extensions.getColorCode
import com.hashone.commons.extensions.getLocaleString
import com.hashone.commons.extensions.getMediaPickIntent
import com.hashone.commons.extensions.getScreenWidth
import com.hashone.commons.extensions.hideSystemUI
import com.hashone.commons.extensions.length
import com.hashone.commons.extensions.navigationUI
import com.hashone.commons.extensions.setStatusBarColor
import com.hashone.commons.module.flexbox.AlignContent
import com.hashone.commons.module.flexbox.AlignItems
import com.hashone.commons.module.flexbox.FlexDirection
import com.hashone.commons.module.flexbox.FlexWrap
import com.hashone.commons.module.flexbox.FlexboxLayout.LayoutParams
import com.hashone.commons.module.flexbox.JustifyContent
import com.hashone.commons.utils.checkClickTime
import com.hashone.commons.utils.dpToPx
import com.hashone.commons.utils.openKeyboard
import com.hashone.commons.utils.sendContactEmail
import com.hashone.commons.utils.showSnackBar
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.roundToInt


class ContactUsActivity : BaseActivity() {

    private lateinit var binding: ActivityContactUsBinding

    private lateinit var builder: ContactUs.Builder

    private var attachmentUri1: Uri? = null
    private var attachmentUri2: Uri? = null
    private var attachmentUri3: Uri? = null

    private var attachmentFileSize: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        builder = (intent!!.extras!!.getSerializable(KEY_CONTACT_US) as ContactUs).builder

        if (builder.screenBuilder.isFullScreen) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            setStatusBarColor(getColorCode(builder.screenBuilder.statusBarColor))
            navigationUI(false, getColorCode(builder.screenBuilder.navigationBarColor))
            hideSystemUI()
        } else {
            if (builder.screenBuilder.statusBarColor != -1) {
                setStatusBarColor(getColorCode(builder.screenBuilder.statusBarColor))
                navigationUI(true, getColorCode(builder.screenBuilder.navigationBarColor))
            }
        }

        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setScreenUI()
        setToolbarUI()
        setRadioButtonUI()
        setMessageUI()
        setAttachmentUI()
        setActionButtonUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        const val KEY_CONTACT_US = "KEY_CONTACT_US"

        const val REQUEST_CODE_ATTACHMENT_1 = 121
        const val REQUEST_CODE_ATTACHMENT_2 = 122
        const val REQUEST_CODE_ATTACHMENT_3 = 123

        fun newIntent(context: Context, contactUs: ContactUs): Intent {
            return Intent(context, ContactUsActivity::class.java).apply {
                Bundle().apply { putSerializable(KEY_CONTACT_US, contactUs) }
                    .also { this.putExtras(it) }
            }
        }
    }

    var registerActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data != null && result.resultCode == Activity.RESULT_OK) {
                try {
                    val selectedUri = result.data!!.data
                    if (selectedUri != null) {
                        val path = selectedUri
                        when (mRequestCode) {
                            REQUEST_CODE_ATTACHMENT_1 -> {
                                attachmentUri1 = selectedUri
                                Glide.with(mActivity.applicationContext).load(path)
                                    .into(binding.imageViewAttachment1)
                                binding.imageViewThumb1.isVisible =
                                    attachmentUri1.toString().isEmpty()
                                binding.imageViewDelete1.isVisible =
                                    attachmentUri1.toString().isNotEmpty()
                            }

                            REQUEST_CODE_ATTACHMENT_2 -> {
                                attachmentUri2 = selectedUri
                                Glide.with(mActivity.applicationContext).load(path)
                                    .into(binding.imageViewAttachment2)
                                binding.imageViewThumb2.isVisible =
                                    attachmentUri2.toString().isEmpty()
                                binding.imageViewDelete2.isVisible =
                                    attachmentUri2.toString().isNotEmpty()
                            }

                            REQUEST_CODE_ATTACHMENT_3 -> {
                                attachmentUri3 = selectedUri
                                Glide.with(mActivity.applicationContext).load(path)
                                    .into(binding.imageViewAttachment3)
                                binding.imageViewThumb3.isVisible =
                                    attachmentUri3.toString().isEmpty()
                                binding.imageViewDelete3.isVisible =
                                    attachmentUri3.toString().isNotEmpty()
                            }
                        }

                        updateFileSizeUI()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private var mRequestCode: Int = -1
    private fun selectImageFromGallery(requestCode: Int) {
        try {
            mRequestCode = requestCode
            registerActivityResult.launch(
                getMediaPickIntent(
                    builder.mediaBuilder.allowPhotosOnly,
                    builder.mediaBuilder.allowVideosOnly,
                    builder.mediaBuilder.allowBoth
                )
            )
        } catch (e: Exception) {
            try {
                registerActivityResult.launch(
                    getMediaPickIntent(
                        builder.mediaBuilder.allowPhotosOnly,
                        builder.mediaBuilder.allowVideosOnly,
                        builder.mediaBuilder.allowBoth
                    )
                )
            } catch (e2: Exception) {
                showSnackBar(
                    mActivity, binding.cardViewSubmit, getLocaleString(R.string.no_gallery_app)
                )
            }
        }
    }

    private fun checkIfPermissionAllowed(requestCode: Int) {
        mRequestCode = requestCode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                selectImageFromGallery(mRequestCode)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    mActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                selectImageFromGallery(mRequestCode)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                if (mRequestCode != -1) {
                    selectImageFromGallery(mRequestCode)
                }
            } else {
                runOnUiThread {
                    showCustomAlertDialog(message = getLocaleString(R.string.allow_permission),
                        negativeButtonText = getLocaleString(R.string.action_cancel)
                            .uppercase(
                                Locale.getDefault()
                            ),
                        positionButtonText = getLocaleString(R.string.action_grant)
                            .uppercase(Locale.getDefault()),
                        negativeCallback = {
                            alertDialog?.cancel()
                        },
                        positiveCallback = {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            alertDialog?.cancel()
                        },
                        onDismissListener = {

                        },
                        onCancelListener = {

                        })
                }
            }
        }

    private fun setScreenUI() {
        //TODO: Screen
        if (builder.screenBuilder.windowBackgroundColor != -1)
            binding.layoutContactUsParent.setBackgroundColor(getColorCode(builder.screenBuilder.windowBackgroundColor))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (builder.screenBuilder.isFullScreen) {
            hideSystemUI()
            setStatusBarColor(getColorCode(builder.screenBuilder.statusBarColor))
            navigationUI(false, getColorCode(builder.screenBuilder.statusBarColor))
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (builder.screenBuilder.isFullScreen) {
            if (hasFocus) {
                hideSystemUI()
                setStatusBarColor(getColorCode(builder.screenBuilder.statusBarColor))
                navigationUI(false, getColorCode(builder.screenBuilder.statusBarColor))
            }
        }
    }

    private fun setToolbarUI() {
        //TODO: Toolbar
        setSupportActionBar(binding.toolBar)
        supportActionBar!!.title = ""
        supportActionBar!!.subtitle = ""

        if (builder.toolBarBuilder.toolBarColor != -1)
            binding.toolBar.setBackgroundColor(getColorCode(builder.toolBarBuilder.toolBarColor))
        if (builder.toolBarBuilder.backPressIcon != -1)
            binding.toolBar.setNavigationIcon(builder.toolBarBuilder.backPressIcon)
        binding.toolBar.navigationContentDescription = builder.toolBarBuilder.backPressIconDescription

        if (builder.toolBarBuilder.toolBarTitle.isNotEmpty())
            binding.textViewToolBarTitle.text = builder.toolBarBuilder.toolBarTitle
        if (builder.toolBarBuilder.toolBarTitleColor != -1)
            binding.textViewToolBarTitle.setTextColor(getColorCode(builder.toolBarBuilder.toolBarTitleColor))
        if (builder.toolBarBuilder.toolBarTitleFont != -1)
            binding.textViewToolBarTitle.typeface =
                ResourcesCompat.getFont(mActivity, builder.toolBarBuilder.toolBarTitleFont)
        if (builder.toolBarBuilder.toolBarTitleSize != -1F)
            binding.textViewToolBarTitle.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                builder.toolBarBuilder.toolBarTitleSize
            )
    }

    var selectedOptionId: Int = -1

    @SuppressLint("ResourceType", "ClickableViewAccessibility")
    private fun setRadioButtonUI() {
        //TODO: Radio Buttons
        binding.flexRadioButtons.flexDirection = FlexDirection.ROW
        binding.flexRadioButtons.flexWrap = FlexWrap.WRAP
        binding.flexRadioButtons.justifyContent = JustifyContent.FLEX_START
        binding.flexRadioButtons.alignItems = AlignItems.BASELINE
        binding.flexRadioButtons.alignContent = AlignContent.FLEX_START
        binding.flexRadioButtons.isVisible = builder.mediaBuilder.optionItemsList.isNotEmpty()

        builder.mediaBuilder.optionItemsList.forEachIndexed { index, optionItem ->
            val radioButton = RadioButton(mActivity)
            radioButton.id = 100 + index
            radioButton.isChecked = optionItem.isChecked
            radioButton.isClickable = false
            radioButton.text = optionItem.text
            if (optionItem.isChecked) {
                if (selectedOptionId != -1)
                    binding.root.findViewById<RadioButton>(selectedOptionId).isChecked = false
                selectedOptionId = radioButton.id
                if (builder.messageBuilder.messageHint.isEmpty())
                    binding.textViewFeedbackMessage.hint =
                        builder.messageBuilder.messageHint.ifEmpty { optionItem.message.ifEmpty { getLocaleString(R.string.label_type_here) } }
            }
            radioButton.applyTintColor(getColorCode(builder.radioButtonBinding.radioButtonTextColor))
            radioButton.applyTextStyle(
                getColorCode(builder.radioButtonBinding.radioButtonTextColor),
                builder.radioButtonBinding.radioButtonTextFont,
                builder.radioButtonBinding.radioButtonTextSize
            )

//            val colorStateList = ColorStateList(
//                arrayOf<IntArray>(
//                    intArrayOf(-android.R.attr.state_enabled),
//                    intArrayOf(android.R.attr.state_enabled)
//                ), intArrayOf(
//                    Color.BLACK,  //disabled
//                    Color.BLUE //enabled
//                )
//            )
//
//            radioButton.setButtonTintList(colorStateList)
            radioButton.setPadding(
                dpToPx(4F).roundToInt(),
                dpToPx(0F).roundToInt(),
                dpToPx(32F).roundToInt(),
                dpToPx(0F).roundToInt()
            )
            val flexLayoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            radioButton.layoutParams = flexLayoutParams

            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (selectedOptionId != -1) {
                    binding.root.findViewById<RadioButton>(selectedOptionId).isChecked = false
                }
                if (isChecked) {
                    binding.textViewFeedbackMessage.hint =
                        builder.messageBuilder.messageHint.ifEmpty { optionItem.message.ifEmpty { getLocaleString(R.string.label_type_here) } }
                    selectedOptionId = radioButton.id
                }
            }

            radioButton.setOnTouchListener { v, event ->
                if (event.actionMasked == MotionEvent.ACTION_UP)
                    if (!radioButton.isChecked) {
                        radioButton.isChecked = true
                    }
                return@setOnTouchListener true
            }

            binding.flexRadioButtons.addView(radioButton)
        }
    }

    private fun setMessageUI() {
        //TODO: Message UI
        (binding.textViewFeedbackMessage.layoutParams as LinearLayout.LayoutParams).apply {
            height = (getScreenWidth() * builder.mediaBuilder.messageBoxHeight).roundToInt()
        }

        if (builder.messageBuilder.messageCardBackgroundColor != -1) {
            binding.textViewFeedbackMessage.background = corneredDrawable(
                getColorCode(builder.messageBuilder.messageCardBackgroundColor),
                dpToPx(builder.messageBuilder.messageCardBackgroundRadius)
            )
        } else {
            binding.textViewFeedbackMessage.background = corneredDrawable(
                getColorCode(R.color.extra_extra_light_gray),
                dpToPx(builder.messageBuilder.messageCardBackgroundRadius)
            )
        }

        if (builder.messageBuilder.messageHint.isNotEmpty())
            binding.textViewFeedbackMessage.hint = builder.messageBuilder.messageHint
        if (builder.messageBuilder.messageHintColor != -1)
            binding.textViewFeedbackMessage.setHintTextColor(getColorCode(builder.messageBuilder.messageHintColor))
        binding.textViewFeedbackMessage.setText(builder.messageBuilder.message)
        if (builder.messageBuilder.messageColor != -1)
            binding.textViewFeedbackMessage.setTextColor(getColorCode(builder.messageBuilder.messageColor))
        if (builder.messageBuilder.messageFont != -1)
            binding.textViewFeedbackMessage.typeface =
                ResourcesCompat.getFont(mActivity, builder.messageBuilder.messageFont)
        if (builder.messageBuilder.messageSize != -1F)
            binding.textViewFeedbackMessage.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                builder.messageBuilder.messageSize
            )

        binding.textViewFeedbackMessage.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                updateSubmitButtonUI()
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setAttachmentUI() {
        //TODO: Attachment UI

        (binding.layoutAttachments.layoutParams as LinearLayout.LayoutParams).apply {
            height = (getScreenWidth() * builder.mediaBuilder.attachmentBoxHeight).roundToInt()
        }

        if (builder.attachmentBuilder.attachmentCardBackgroundColor != -1) {
            binding.layoutAttachments.background = corneredDrawable(
                getColorCode(builder.attachmentBuilder.attachmentCardBackgroundColor),
                dpToPx(builder.attachmentBuilder.attachmentCardBackgroundRadius)
            )
        } else {
            binding.layoutAttachments.background = corneredDrawable(
                getColorCode(R.color.extra_extra_light_gray),
                dpToPx(builder.messageBuilder.messageCardBackgroundRadius)
            )
        }

        if (builder.attachmentBuilder.attachmentTitle.isNotEmpty())
            binding.textViewAttachmentTitle.text = builder.attachmentBuilder.attachmentTitle
        if (builder.attachmentBuilder.attachmentTitleColor != -1)
            binding.textViewAttachmentTitle.setTextColor(getColorCode(builder.attachmentBuilder.attachmentTitleColor))
        if (builder.attachmentBuilder.attachmentTitleFont != -1)
            binding.textViewAttachmentTitle.typeface =
                ResourcesCompat.getFont(mActivity, builder.attachmentBuilder.attachmentTitleFont)
        if (builder.attachmentBuilder.attachmentTitleSize != -1F)
            binding.textViewAttachmentTitle.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                builder.attachmentBuilder.attachmentTitleSize
            )
        updateFileSizeUI()

        if (builder.attachmentBuilder.attachmentBackgroundColor != -1) {
            binding.cardViewAttachments11.setCardBackgroundColor(getColorCode(builder.attachmentBuilder.attachmentBackgroundColor))
            binding.cardViewAttachments11.radius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                builder.attachmentBuilder.attachmentBackgroundRadius,
                mActivity.resources.displayMetrics
            )
            binding.cardViewAttachments22.setCardBackgroundColor(getColorCode(builder.attachmentBuilder.attachmentBackgroundColor))
            binding.cardViewAttachments22.radius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                builder.attachmentBuilder.attachmentBackgroundRadius,
                mActivity.resources.displayMetrics
            )
            binding.cardViewAttachments33.setCardBackgroundColor(getColorCode(builder.attachmentBuilder.attachmentBackgroundColor))
            binding.cardViewAttachments33.radius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                builder.attachmentBuilder.attachmentBackgroundRadius,
                mActivity.resources.displayMetrics
            )
        }

        if (builder.attachmentBuilder.attachmentIcon != -1) {
            binding.imageViewThumb1.setImageResource(builder.attachmentBuilder.attachmentIcon)
            binding.imageViewThumb2.setImageResource(builder.attachmentBuilder.attachmentIcon)
            binding.imageViewThumb3.setImageResource(builder.attachmentBuilder.attachmentIcon)
        }

        if (builder.attachmentBuilder.attachmentDeleteIcon != -1) {
            binding.imageViewDelete1.setImageResource(builder.attachmentBuilder.attachmentDeleteIcon)
            binding.imageViewDelete2.setImageResource(builder.attachmentBuilder.attachmentDeleteIcon)
            binding.imageViewDelete3.setImageResource(builder.attachmentBuilder.attachmentDeleteIcon)
        }

        binding.cardViewAttachments1.setOnClickListener {
            if (checkClickTime()) {
                checkIfPermissionAllowed(REQUEST_CODE_ATTACHMENT_1)
            }
        }

        binding.imageViewDelete1.setOnClickListener {
            if (checkClickTime()) {
                attachmentUri1 = null
                binding.imageViewAttachment1.setImageBitmap(null)
                binding.imageViewThumb1.isVisible = true
                binding.imageViewDelete1.isVisible = false

                updateFileSizeUI()
            }
        }

        binding.cardViewAttachments2.setOnClickListener {
            if (checkClickTime()) {
                checkIfPermissionAllowed(REQUEST_CODE_ATTACHMENT_2)
            }
        }

        binding.imageViewDelete2.setOnClickListener {
            if (checkClickTime()) {
                attachmentUri2 = null
                binding.imageViewAttachment2.setImageBitmap(null)
                binding.imageViewThumb2.isVisible = true
                binding.imageViewDelete2.isVisible = false

                updateFileSizeUI()
            }
        }

        binding.cardViewAttachments3.setOnClickListener {
            if (checkClickTime()) {
                checkIfPermissionAllowed(REQUEST_CODE_ATTACHMENT_3)
            }
        }

        binding.imageViewDelete3.setOnClickListener {
            if (checkClickTime()) {
                attachmentUri3 = null
                binding.imageViewAttachment3.setImageBitmap(null)
                binding.imageViewThumb3.isVisible = true
                binding.imageViewDelete3.isVisible = false

                updateFileSizeUI()
            }
        }

        if (builder.emailBuilder.showKeyboard) {
            binding.textViewFeedbackMessage.requestFocus()
            openKeyboard(mActivity)
        }
    }

    private fun setActionButtonUI() {
        //TODO: Action button
        if (builder.actionButtonBuilder.buttonBackgroundInactiveColor != -1)
            binding.cardViewSubmit.setCardBackgroundColor(getColorCode(builder.actionButtonBuilder.buttonBackgroundInactiveColor))
        binding.cardViewSubmit.radius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            builder.actionButtonBuilder.buttonRadius,
            mActivity.resources.displayMetrics
        )

        if (builder.actionButtonBuilder.buttonText.isNotEmpty())
            binding.textViewSubmit.text = builder.actionButtonBuilder.buttonText
        binding.textViewSubmit.applyTextStyle(
            getColorCode(builder.actionButtonBuilder.buttonTextColor),
            builder.actionButtonBuilder.buttonTextFont,
            builder.actionButtonBuilder.buttonTextSize
        )

        binding.cardViewSubmit.setOnClickListener {
            if (checkClickTime()) {
                //TODO submit email here
                if (binding.textViewFeedbackMessage.text.toString().trim().isNotEmpty()) {
                    val fileUris = ArrayList<Uri>()
                    if (attachmentUri1 != null) {
                        fileUris.add(attachmentUri1!!)
                    }
                    if (attachmentUri2 != null) {
                        fileUris.add(
                            attachmentUri2!!
                        )
                    }
                    if (attachmentUri3 != null) {
                        fileUris.add(
                            attachmentUri3!!
                        )
                    }

                    //TODO: Email details
                    sendContactEmail(
                        context = mActivity,
                        selectionType = if (selectedOptionId != -1) {
                            (binding.root.findViewById<RadioButton>(selectedOptionId)).text.toString()
                        } else "",
                        message = binding.textViewFeedbackMessage.text.toString().trim(),
                        fileUris = fileUris,
                        builder = builder
                    )
                }
            }
        }
    }

    private fun updateFileSizeUI() {
        var localFileSize = 0L
        if (attachmentUri1 != null) {
            localFileSize += attachmentUri1!!.length(contentResolver)
        }
        if (attachmentUri2 != null) {
            localFileSize += attachmentUri2!!.length(contentResolver)
        }
        if (attachmentUri3 != null) {
            localFileSize += attachmentUri3!!.length(contentResolver)
        }
        val decimalFormat = DecimalFormat("0.00")
        val sizeInMB = localFileSize / (1024 * 1024).toDouble()
        attachmentFileSize = sizeInMB
        binding.textViewAttachmentSize.text = String.format(
            "%s MB/%s MB", decimalFormat.format(attachmentFileSize), builder.mediaBuilder.maxFileSize.toString()
        )

        binding.textViewAttachmentSize.setTextColor(
            getColorCode(
                if (attachmentFileSize > builder.mediaBuilder.maxFileSize) {
                    R.color.alert
                } else {
                    if (builder.attachmentBuilder.attachmentTitleColor != -1) {
                        builder.attachmentBuilder.attachmentTitleColor
                    } else {
                        R.color.light_gray
                    }
                }
            )
        )
        updateSubmitButtonUI()
    }

    private fun updateSubmitButtonUI() {
        if (attachmentFileSize > 0L) {
            if (attachmentFileSize > builder.mediaBuilder.maxFileSize) {
                binding.cardViewSubmit.isEnabled = false
                binding.cardViewSubmit.setCardBackgroundColor(getColorCode(builder.actionButtonBuilder.buttonBackgroundInactiveColor))
            } else {
                if (binding.textViewFeedbackMessage.text.toString().trim().isNotEmpty()) {
                    binding.cardViewSubmit.isEnabled = true
                    binding.cardViewSubmit.setCardBackgroundColor(getColorCode(builder.actionButtonBuilder.buttonBackgroundColor))
                } else {
                    binding.cardViewSubmit.isEnabled = false
                    binding.cardViewSubmit.setCardBackgroundColor(getColorCode(builder.actionButtonBuilder.buttonBackgroundInactiveColor))
                }
            }
        } else {
            if (binding.textViewFeedbackMessage.text.toString().trim().isNotEmpty()) {
                binding.cardViewSubmit.isEnabled = true
                binding.cardViewSubmit.setCardBackgroundColor(getColorCode(builder.actionButtonBuilder.buttonBackgroundColor))
            } else {
                binding.cardViewSubmit.isEnabled = false
                binding.cardViewSubmit.setCardBackgroundColor(getColorCode(builder.actionButtonBuilder.buttonBackgroundInactiveColor))
            }
        }
    }
}