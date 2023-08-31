package com.hashone.commons.base

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.hashone.commons.R
import com.hashone.commons.databinding.DialogConfirmationBinding
import com.hashone.commons.languages.LocaleHelper
import com.hashone.commons.utils.DEFAULT_LANGUAGE
import com.hashone.commons.utils.dpToPx
import kotlin.math.roundToInt

open class BaseActivity : AppCompatActivity() {

    lateinit var mActivity: Activity

    val mActivityLauncher: BetterActivityResult<Intent, ActivityResult> =
        BetterActivityResult.registerActivityForResult(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = this

        LocaleHelper.setLocale(
            mActivity, CommonApplication.mInstance.mStoreUserData.getString(DEFAULT_LANGUAGE)
        )?.let {
            CommonApplication.mInstance.setLocaleContext(
                it
            )
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    var alertDialog: AlertDialog? = null
    fun showCustomAlertDialog(
        title: String = "",
        message: String = "",
        positionButtonText: String = "",
        negativeButtonText: String = "",
        neutralButtonText: String = "",
        isCancelable: Boolean = true,
        negativeCallback: View.OnClickListener? = null,
        positiveCallback: View.OnClickListener? = null,
        neutralCallback: View.OnClickListener? = null,
        keyEventCallback: DialogInterface.OnKeyListener? = null,
        onDismissListener: DialogInterface.OnDismissListener? = null,
        onCancelListener: DialogInterface.OnCancelListener? = null,
        isReplace: Boolean = false,
        isRemoveAds: Boolean = false
    ) {
        try {
            val builder = AlertDialog.Builder(mActivity, R.style.CustomAlertDialog)
            val dialogBinding =
                DialogConfirmationBinding.inflate(LayoutInflater.from(mActivity), null, false)
            dialogBinding.textViewTitle.text = title
            dialogBinding.textViewMessage.text = message
            dialogBinding.textViewYes.text = positionButtonText
            dialogBinding.textViewNo.text = negativeButtonText
            dialogBinding.textViewNeutral.text = neutralButtonText
            dialogBinding.textViewTitle.isVisible = title.isNotEmpty()

            if (title.isEmpty()) {
                val mLayoutParams =
                    dialogBinding.textViewMessage.layoutParams as ConstraintLayout.LayoutParams
                mLayoutParams.setMargins(0, dpToPx(24F).roundToInt(), 0, 0)
                dialogBinding.textViewMessage.layoutParams = mLayoutParams

                dialogBinding.textViewMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
                dialogBinding.textViewMessage.setTextColor(
                    ContextCompat.getColor(
                        mActivity,
                        R.color.black
                    )
                )
            }

            dialogBinding.textViewYes.isVisible = positionButtonText.isNotEmpty()
            dialogBinding.textViewNo.isVisible = negativeButtonText.isNotEmpty()

            dialogBinding.textViewYes.isAllCaps = true
            dialogBinding.textViewNo.isAllCaps = true

            dialogBinding.view4.isVisible = neutralButtonText.isNotEmpty()
            dialogBinding.textViewNeutral.isVisible = neutralButtonText.isNotEmpty()

            if (isReplace) {
                dialogBinding.textViewYes.typeface =
                    ResourcesCompat.getFont(mActivity, R.font.roboto_regular)
                dialogBinding.textViewYes.typeface =
                    ResourcesCompat.getFont(mActivity, R.font.roboto_bold)
            }
            if (isRemoveAds) {
                dialogBinding.textViewYes.typeface =
                    ResourcesCompat.getFont(mActivity, R.font.roboto_bold)
                dialogBinding.textViewNeutral.typeface =
                    ResourcesCompat.getFont(mActivity, R.font.roboto_regular)
            }

            builder.setView(dialogBinding.root)
            alertDialog = builder.create()
            if (!mActivity.isDestroyed) if (alertDialog != null && !alertDialog!!.isShowing) alertDialog!!.show()
            alertDialog!!.setCancelable(isCancelable)
            dialogBinding.textViewYes.setOnClickListener(positiveCallback)
            dialogBinding.textViewNo.setOnClickListener(negativeCallback)
            dialogBinding.textViewNeutral.setOnClickListener(neutralCallback)
            alertDialog!!.setOnDismissListener(onDismissListener)
            alertDialog!!.setOnCancelListener(onCancelListener)
            alertDialog!!.setOnKeyListener(keyEventCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}