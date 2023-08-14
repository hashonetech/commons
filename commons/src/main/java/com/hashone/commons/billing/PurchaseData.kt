package com.hashone.commons.billing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PurchaseData(
    var purchaseToken: String,
    var purchaseTime: Long,
    var isAutoRenewing: Boolean,
    var isAcknowledged: Boolean,
    var orderId: String,
    var productId: String,
    var purchaseState: Int,
    var productType: String,
    var hasFreeTrial: Boolean = false,
    var trialPeriod: HashMap<String,String> = hashMapOf(),
) : Parcelable
