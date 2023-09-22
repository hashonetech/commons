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
import android.text.TextUtils
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
import com.hashone.commons.extensions.getMediaPickIntent
import com.hashone.commons.extensions.getScreenWidth
import com.hashone.commons.extensions.hideSoftKeyboard
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
import java.text.DecimalFormatSymbols
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
                    mActivity, binding.cardViewSubmit, getString(R.string.no_gallery_app)
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
                    showCustomAlertDialog(
                        message = getString(R.string.allow_permission),
                        negativeButtonText = getString(R.string.action_cancel),
                        positionButtonText = getString(R.string.action_grant),
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

        if (builder.toolBarBuilder.barColor != -1)
            binding.toolBar.setBackgroundColor(getColorCode(builder.toolBarBuilder.barColor))
        if (builder.toolBarBuilder.backIcon != -1)
            binding.toolBar.setNavigationIcon(builder.toolBarBuilder.backIcon)
        binding.toolBar.navigationContentDescription = builder.toolBarBuilder.backIconDescription

        if (builder.toolBarBuilder.title.isNotEmpty())
            binding.textViewToolBarTitle.text = builder.toolBarBuilder.title
        if (builder.toolBarBuilder.titleColor != -1)
            binding.textViewToolBarTitle.setTextColor(getColorCode(builder.toolBarBuilder.titleColor))
        if (builder.toolBarBuilder.titleFont != -1)
            binding.textViewToolBarTitle.typeface =
                ResourcesCompat.getFont(mActivity, builder.toolBarBuilder.titleFont)
        if (builder.toolBarBuilder.titleSize != -1F)
            binding.textViewToolBarTitle.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                builder.toolBarBuilder.titleSize
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
                if (builder.messageBuilder.hint.isEmpty())
                    binding.textViewFeedbackMessage.hint =
                        builder.messageBuilder.hint.ifEmpty {
                            optionItem.message.ifEmpty {
                                getString(
                                    R.string.label_type_here
                                )
                            }
                        }
            }

            radioButton.applyTintColor(getColorCode(if (optionItem.isChecked) builder.radioButtonBinding.selectedColor else builder.radioButtonBinding.defaultColor))
            radioButton.applyTextStyle(
                getColorCode(builder.radioButtonBinding.selectedColor),
                builder.radioButtonBinding.textFont,
                builder.radioButtonBinding.textSize
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
//            radioButton.setPadding(
//                dpToPx(4F).roundToInt(),
//                dpToPx(0F).roundToInt(),
//                dpToPx(32F).roundToInt(),
//                dpToPx(0F).roundToInt()
//            )

            val isLeftToRight =
                TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_LTR
            radioButton.setPadding(
                dpToPx(if (isLeftToRight) 2F else 32F).roundToInt(),
                dpToPx(0F).roundToInt(),
                dpToPx(if (isLeftToRight) 32F else 2F).roundToInt(),
                dpToPx(0F).roundToInt()
            )
            val flexLayoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            radioButton.layoutParams = flexLayoutParams

            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                hideSoftKeyboard()
                binding.textViewFeedbackMessage.clearFocus()
                if (selectedOptionId != -1) {
                    binding.root.findViewById<RadioButton>(selectedOptionId).isChecked = false
                }
                if (isChecked) {
                    binding.textViewFeedbackMessage.hint =
                        builder.messageBuilder.hint.ifEmpty {
                            optionItem.message.ifEmpty {
                                getString(
                                    R.string.label_type_here
                                )
                            }
                        }
                    selectedOptionId = radioButton.id

                }
                buttonView.setTextColor(getColorCode(if (isChecked) builder.radioButtonBinding.selectedColor else builder.radioButtonBinding.defaultColor))

            }

            radioButton.setOnTouchListener { v, event ->
                hideSoftKeyboard()
                binding.textViewFeedbackMessage.clearFocus()
                if (event.actionMasked == MotionEvent.ACTION_UP) {
                    if (!radioButton.isChecked) {
                        radioButton.isChecked = true
                    }
                }
                return@setOnTouchListener true
            }

            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ), intArrayOf(
                    getColorCode(builder.radioButtonBinding.defaultColor),  //disabled
                    getColorCode(builder.radioButtonBinding.selectedColor) //enabled
                )
            )

            radioButton.buttonTintList = colorStateList
            radioButton.isClickable = true
            radioButton.setTextColor(getColorCode(if (optionItem.isChecked) builder.radioButtonBinding.selectedColor else builder.radioButtonBinding.defaultColor))

            binding.flexRadioButtons.addView(radioButton)
//            binding.flexRadioButtons.setSpaceBetweenItem(dpToPx(32F))
        }
    }

    private fun setMessageUI() {
        //TODO: Message UI
        (binding.textViewFeedbackMessage.layoutParams as LinearLayout.LayoutParams).apply {
            height = (getScreenWidth() * builder.mediaBuilder.messageBoxHeight).roundToInt()
        }

        if (builder.messageBuilder.backgroundColor != -1) {
            binding.textViewFeedbackMessage.background = corneredDrawable(
                getColorCode(builder.messageBuilder.backgroundColor),
                dpToPx(builder.messageBuilder.backgroundRadius)
            )
        } else {
            binding.textViewFeedbackMessage.background = corneredDrawable(
                getColorCode(R.color.extra_extra_light_gray),
                dpToPx(builder.messageBuilder.backgroundRadius)
            )
        }

        if (builder.messageBuilder.hint.isNotEmpty())
            binding.textViewFeedbackMessage.hint = builder.messageBuilder.hint
        if (builder.messageBuilder.hintColor != -1)
            binding.textViewFeedbackMessage.setHintTextColor(getColorCode(builder.messageBuilder.hintColor))
        binding.textViewFeedbackMessage.setText(builder.messageBuilder.message)
        if (builder.messageBuilder.color != -1)
            binding.textViewFeedbackMessage.setTextColor(getColorCode(builder.messageBuilder.color))
        if (builder.messageBuilder.font != -1)
            binding.textViewFeedbackMessage.typeface =
                ResourcesCompat.getFont(mActivity, builder.messageBuilder.font)
        if (builder.messageBuilder.size != -1F)
            binding.textViewFeedbackMessage.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                builder.messageBuilder.size
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

        if (builder.attachmentBuilder.cardBackgroundColor != -1) {
            binding.layoutAttachments.background = corneredDrawable(
                getColorCode(builder.attachmentBuilder.cardBackgroundColor),
                dpToPx(builder.attachmentBuilder.cardBackgroundRadius)
            )
        } else {
            binding.layoutAttachments.background = corneredDrawable(
                getColorCode(R.color.extra_extra_light_gray),
                dpToPx(builder.messageBuilder.backgroundRadius)
            )
        }

        if (builder.attachmentBuilder.title.isNotEmpty())
            binding.textViewAttachmentTitle.text = builder.attachmentBuilder.title
        if (builder.attachmentBuilder.titleColor != -1)
            binding.textViewAttachmentTitle.setTextColor(getColorCode(builder.attachmentBuilder.titleColor))
        if (builder.attachmentBuilder.titleFont != -1)
            binding.textViewAttachmentTitle.typeface =
                ResourcesCompat.getFont(mActivity, builder.attachmentBuilder.titleFont)
        if (builder.attachmentBuilder.titleSize != -1F)
            binding.textViewAttachmentTitle.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                builder.attachmentBuilder.titleSize
            )
        updateFileSizeUI()

        if (builder.attachmentBuilder.backgroundColor != -1) {
            binding.cardViewAttachments11.setCardBackgroundColor(getColorCode(builder.attachmentBuilder.backgroundColor))
            binding.cardViewAttachments11.radius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                builder.attachmentBuilder.backgroundRadius,
                mActivity.resources.displayMetrics
            )
            binding.cardViewAttachments22.setCardBackgroundColor(getColorCode(builder.attachmentBuilder.backgroundColor))
            binding.cardViewAttachments22.radius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                builder.attachmentBuilder.backgroundRadius,
                mActivity.resources.displayMetrics
            )
            binding.cardViewAttachments33.setCardBackgroundColor(getColorCode(builder.attachmentBuilder.backgroundColor))
            binding.cardViewAttachments33.radius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                builder.attachmentBuilder.backgroundRadius,
                mActivity.resources.displayMetrics
            )
        }

        if (builder.attachmentBuilder.addIcon != -1) {
            binding.imageViewThumb1.setImageResource(builder.attachmentBuilder.addIcon)
            binding.imageViewThumb2.setImageResource(builder.attachmentBuilder.addIcon)
            binding.imageViewThumb3.setImageResource(builder.attachmentBuilder.addIcon)
        }

        if (builder.attachmentBuilder.deleteIcon != -1) {
            binding.imageViewDelete1.setImageResource(builder.attachmentBuilder.deleteIcon)
            binding.imageViewDelete2.setImageResource(builder.attachmentBuilder.deleteIcon)
            binding.imageViewDelete3.setImageResource(builder.attachmentBuilder.deleteIcon)
        }

        binding.cardViewAttachments1.setOnClickListener {
            if (checkClickTime()) {
                hideSoftKeyboard()
                binding.textViewFeedbackMessage.clearFocus()
                checkIfPermissionAllowed(REQUEST_CODE_ATTACHMENT_1)
            }
        }

        binding.imageViewDelete1.setOnClickListener {
            if (checkClickTime()) {
                hideSoftKeyboard()
                binding.textViewFeedbackMessage.clearFocus()
                attachmentUri1 = null
                binding.imageViewAttachment1.setImageBitmap(null)
                binding.imageViewThumb1.isVisible = true
                binding.imageViewDelete1.isVisible = false

                updateFileSizeUI()
            }
        }

        binding.cardViewAttachments2.setOnClickListener {
            if (checkClickTime()) {
                hideSoftKeyboard()
                binding.textViewFeedbackMessage.clearFocus()
                checkIfPermissionAllowed(REQUEST_CODE_ATTACHMENT_2)
            }
        }

        binding.imageViewDelete2.setOnClickListener {
            if (checkClickTime()) {
                hideSoftKeyboard()
                binding.textViewFeedbackMessage.clearFocus()
                attachmentUri2 = null
                binding.imageViewAttachment2.setImageBitmap(null)
                binding.imageViewThumb2.isVisible = true
                binding.imageViewDelete2.isVisible = false

                updateFileSizeUI()
            }
        }

        binding.cardViewAttachments3.setOnClickListener {
            if (checkClickTime()) {
                hideSoftKeyboard()
                binding.textViewFeedbackMessage.clearFocus()
                checkIfPermissionAllowed(REQUEST_CODE_ATTACHMENT_3)
            }
        }

        binding.imageViewDelete3.setOnClickListener {
            if (checkClickTime()) {
                hideSoftKeyboard()
                binding.textViewFeedbackMessage.clearFocus()
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
        } else {
            hideSoftKeyboard()
            binding.textViewFeedbackMessage.clearFocus()
        }

        binding.layoutMainScrollView.setOnClickListener {
            hideSoftKeyboard()
            binding.textViewFeedbackMessage.clearFocus()
        }
    }

    private fun setActionButtonUI() {
        //TODO: Action button
        if (builder.actionButtonBuilder.backgroundInactiveColor != -1)
            binding.cardViewSubmit.setCardBackgroundColor(getColorCode(builder.actionButtonBuilder.backgroundInactiveColor))
        binding.cardViewSubmit.radius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            builder.actionButtonBuilder.radius,
            mActivity.resources.displayMetrics
        )

        if (builder.actionButtonBuilder.text.isNotEmpty())
            binding.textViewSubmit.text = builder.actionButtonBuilder.text
        binding.textViewSubmit.applyTextStyle(
            getColorCode(builder.actionButtonBuilder.textColor),
            builder.actionButtonBuilder.textFont,
            builder.actionButtonBuilder.textSize
        )

        binding.cardViewSubmit.setOnClickListener {
            if (checkClickTime()) {
                hideSoftKeyboard()
                binding.textViewFeedbackMessage.clearFocus()
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
        decimalFormat.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
        val sizeInMB = localFileSize / (1000 * 1000).toDouble()
        attachmentFileSize = sizeInMB


        binding.textViewAttachmentSize.text = String.format(
            "%s MB/%s MB",
            decimalFormat.format(attachmentFileSize),
            builder.mediaBuilder.maxFileSize.toString()
        )

        binding.textViewAttachmentSize.setTextColor(
            getColorCode(
                if (attachmentFileSize > builder.mediaBuilder.maxFileSize) {
                    R.color.alert
                } else {
                    if (builder.attachmentBuilder.titleColor != -1) {
                        builder.attachmentBuilder.titleColor
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
                binding.cardViewSubmit.setCardBackgroundColor(getColorCode(builder.actionButtonBuilder.backgroundInactiveColor))
            } else {
                if (binding.textViewFeedbackMessage.text.toString().trim().isNotEmpty()) {
                    binding.cardViewSubmit.isEnabled = true
                    binding.cardViewSubmit.setCardBackgroundColor(getColorCode(builder.actionButtonBuilder.backgroundColor))
                } else {
                    binding.cardViewSubmit.isEnabled = false
                    binding.cardViewSubmit.setCardBackgroundColor(getColorCode(builder.actionButtonBuilder.backgroundInactiveColor))
                }
            }
        } else {
            if (binding.textViewFeedbackMessage.text.toString().trim().isNotEmpty()) {
                binding.cardViewSubmit.isEnabled = true
                binding.cardViewSubmit.setCardBackgroundColor(getColorCode(builder.actionButtonBuilder.backgroundColor))
            } else {
                binding.cardViewSubmit.isEnabled = false
                binding.cardViewSubmit.setCardBackgroundColor(getColorCode(builder.actionButtonBuilder.backgroundInactiveColor))
            }
        }
    }
}