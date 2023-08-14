package com.hashone.commons.billing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryPurchaseData(
    var productId: String,
    var orderId: String,
    var purchaseTime: Long,
    var autoRenew: Boolean,
    var productType: String,
) : Parcelable
