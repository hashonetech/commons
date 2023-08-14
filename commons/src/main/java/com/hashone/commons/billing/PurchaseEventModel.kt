package com.hashone.commons.billing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PurchaseEventModel(
    var isPurchaseSuccess: Boolean, var purchaseData: PurchaseData?, var errorCode: Int = -1, var errorMessage: String = ""
) : Parcelable
