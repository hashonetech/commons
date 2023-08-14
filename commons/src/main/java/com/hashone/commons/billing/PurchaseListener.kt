package com.hashone.commons.billing

import com.android.billingclient.api.ProductDetails

abstract class PurchaseListener {
    open fun onPurchaseConsume() {}
    open fun onProductType(productType: String) {}
    open fun onBillingError(responseCode: Int, debugMessage: String) {}
    open fun onBillingInitialized() {}
    open fun onProductDetail(productDetailsList: List<ProductDetails>) {}
    open fun onPurchaseHistoryReceived(array: ArrayList<HistoryPurchaseData>) {}
    open fun isPurchaseAcknowledge(isReady: Boolean = false) {}
}