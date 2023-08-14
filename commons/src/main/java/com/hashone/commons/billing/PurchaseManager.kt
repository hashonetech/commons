package com.hashone.commons.billing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryResult
import com.android.billingclient.api.PurchasesResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchaseHistory
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject


object PurchaseManager {
    const val CURRENT_PURCHASE: String = "CURRENT_PURCHASE"
    const val IS_PREMIUM_PURCHASED: String = "KEY_IS_PREMIUM_PURCHASED"
    private var billingClient: BillingClient? = null

    enum class TrialPeriod {
        DAY, WEEK, MONTH, KEY_OFFER_TYPE, KEY_OFFER_DURATION
    }

    @SuppressLint("StaticFieldLeak")
    var storePurchaseData: StorePurchaseData? = null

    private var purchaseListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                purchase?.let {
                    handlePurchase(purchase)
                }
            }
        } else {
            EventBus.getDefault().post(PurchaseEventModel(false, null, billingResult.responseCode, billingResult.debugMessage))
        }
    }

    /**
     * Initializes the BillingClient and StorePurchaseData for managing in-app purchases.
     *
     * This method creates a BillingClient instance with pending purchases enabled and attaches a listener for handling purchase events.
     * It also initializes a StorePurchaseData instance to manage purchase-related data storage.
     *
     * @param context The application context used for initializing the BillingClient and StorePurchaseData.
     *
     * Example Usage:
     * ```
     * val appContext = applicationContext
     * initializeBillingClient(appContext)
     * ```
     */
    fun initializeBillingClient(context: Context) {
        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(purchaseListener).build()
        storePurchaseData = StorePurchaseData(context)
    }

    /**
     * Checks whether a product is purchased for the given product ID and returns the result through a callback.
     *
     * This method verifies if the specified product is purchased by querying the billing client and
     * invoking the provided callback with the result.
     *
     * @param productId The ID of the product to check for purchase.
     * @param callback The callback function that will receive the result of the purchase check.
     *                 It will be invoked with a boolean value indicating whether the product is purchased or not.
     *
     * Example Usage:
     * ```
     * val productId = "lifetime"
     * isProductPurchased(productId) { isPurchased ->
     *     if (isPurchased) {
     *         // Handle the case where the product is purchased
     *     } else {
     *         // Handle the case where the product is not purchased
     *     }
     * }
     * ```
     */
    fun isProductPurchased(productId: String, callback: (isPurchased: Boolean) -> Unit) {
        isBillingInitialized(object : PurchaseListener() {
            override fun onBillingError(responseCode: Int, debugMessage: String) {
                callback(false)
            }

            override fun onBillingInitialized() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val params: QueryPurchasesParams.Builder = QueryPurchasesParams.newBuilder().setProductType(ProductType.INAPP)
                        val inAppResult = billingClient?.queryPurchasesAsync(params.build())
                        if (inAppResult?.purchasesList?.isNotEmpty() == true) {
                            callback(inAppResult.purchasesList.find { purchaseItem ->
                                JSONObject(purchaseItem.originalJson).optString("productId", "").equals(productId)
                            } != null)
                        } else {
                            callback(false)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback(false)
                    }
                }
            }
        })
    }

    /**
     * Checks whether a subscription is purchased for the given subscription ID and returns the result through a callback.
     *
     * This method verifies if the specified subscription is purchased by querying the billing client and
     * invoking the provided callback with the result.
     *
     * @param subscriptionId The ID of the subscription to check for purchase.
     * @param callback The callback function that will receive the result of the purchase check.
     *                 It will be invoked with a boolean value indicating whether the subscription is purchased or not.
     *
     * Example Usage:
     * ```
     * val subscriptionId = "sub.year"
     * isSubscriptionPurchased(subscriptionId) { isPurchased ->
     *     if (isPurchased) {
     *         // Handle the case where subscription is purchased
     *     } else {
     *         // Handle the case where subscription is not purchased
     *     }
     * }
     * ```
     */
    fun isSubscriptionPurchased(subscriptionId: String, callback: (isSubscribed: Boolean) -> Unit) {
        isBillingInitialized(object : PurchaseListener() {
            override fun onBillingError(responseCode: Int, debugMessage: String) {
                callback(false)
            }

            override fun onBillingInitialized() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val params: QueryPurchasesParams.Builder = QueryPurchasesParams.newBuilder().setProductType(ProductType.SUBS)
                        val inAppResult = billingClient?.queryPurchasesAsync(params.build())
                        if (inAppResult?.purchasesList?.isNotEmpty() == true) {
                            callback(inAppResult.purchasesList.find { purchaseItem ->
                                JSONObject(purchaseItem.originalJson).optString("productId", "").equals(subscriptionId)
                            } != null)
                        } else {
                            callback(false)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback(false)
                    }
                }
            }
        })
    }

    /**
     * Retrieves the product type for a given product ID and notifies a listener with the result.
     *
     * This method queries the product details for the specified product ID using the
     * queryAllProductDetails method and invokes appropriate callbacks on the provided
     * PurchaseListener based on the result.
     *
     * @param productId The ID of the product for which the product type needs to be retrieved.
     * @param purchaseListener The listener that will receive callbacks regarding the purchase process.
     *
     * Example Usage:
     * ```
     * val productId = "example_product_id"
     * val listener = object : PurchaseListener() {
     *     override fun onBillingError(responseCode: Int, debugMessage: String) {
     *         // Handle billing error
     *     }
     *
     *     override fun onProductType(productType: String) {
     *         // Handle retrieved product type
     *     }
     * }
     *
     * getProductType(productId, listener)
     * ```
     */
    fun getProductType(productId: String, purchaseListener: PurchaseListener) {
        queryAllProductDetails(listOf(productId), listOf(productId), object : PurchaseListener() {
            override fun onBillingError(responseCode: Int, debugMessage: String) {
                purchaseListener.onBillingError(responseCode, debugMessage)
            }

            override fun onProductDetail(productDetailsList: List<ProductDetails>) {
                if (productDetailsList.isNotEmpty()) {
                    val productType = productDetailsList[0].productType
                    purchaseListener.onProductType(productType)
                } else {
                    purchaseListener.onProductType("")
                }
            }
        })
    }

    /**
     * Retrieves a list of all purchased products and returns the result through a callback.
     *
     * This method queries the BillingClient to retrieve details of all purchased in-app products
     * and invokes the provided callback with a list of PurchaseData objects representing the purchased products.
     *
     * @param callback The callback function that will receive the list of purchased products.
     *                 It will be invoked with an ArrayList of PurchaseData objects.
     *
     * Example Usage:
     * ```
     * getAllPurchasedProduct { productsList ->
     *     for (product in productsList) {
     *         // Process each purchased product
     *         // Example: Log product details
     *         Log.d("PurchasedProduct", "Product ID: ${product.productId}, Purchase Time: ${product.purchaseTime}")
     *     }
     * }
     * ```
     */
    fun getAllPurchasedProduct(callback: (productsList: ArrayList<PurchaseData>) -> Unit) {
        val arrayList = arrayListOf<PurchaseData>()
        isBillingInitialized(object : PurchaseListener() {
            override fun onBillingError(responseCode: Int, debugMessage: String) {
                callback(arrayList)
            }

            override fun onBillingInitialized() {
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        queryPurchases(ProductType.INAPP)?.purchasesList?.forEach {
                            arrayList.add(
                                PurchaseData(
                                    purchaseToken = it.purchaseToken,
                                    purchaseTime = it.purchaseTime,
                                    isAutoRenewing = it.isAutoRenewing,
                                    isAcknowledged = it.isAcknowledged,
                                    orderId = it.orderId.orEmpty(),
                                    productId = JSONObject(it.originalJson).optString("productId", ""),
                                    purchaseState = it.purchaseState,
                                    productType = ProductType.INAPP
                                )
                            )
                        }
                        callback(arrayList)
                    }
                } catch (e: Exception) {
                    callback(arrayList)
                }

            }
        })
    }


    /**
     * Checks whether the user has purchased premium products based on a list of product IDs and returns the result through a callback.
     *
     * This method verifies if the user has purchased any of the specified premium products (in-app or subscription)
     * and invokes the provided callback with a boolean value indicating whether the user has premium access.
     *
     * @param proProductListList An ArrayList of product IDs representing the premium products.
     * @param callback The callback function that will receive the result of the premium check.
     *                 It will be invoked with a boolean value indicating whether the user has premium access.
     *
     * Example Usage:
     * ```
     * val premiumProducts = arrayListOf("product_id_1", "product_id_2", "subscription_id")
     * isPremium(premiumProducts) { isPremium ->
     *     if (isPremium) {
     *         // Grant premium access to the user
     *     } else {
     *         // User does not have premium access
     *     }
     * }
     * ```
     */
    fun isPremium(proProductListList: ArrayList<String>, callback: (isPremium: Boolean) -> Unit) {
        isBillingInitialized(object : PurchaseListener() {
            override fun onBillingError(responseCode: Int, debugMessage: String) {
                callback(false)
            }

            override fun onBillingInitialized() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        storePurchaseData?.clearPurchaseData()

                        val subscriptionResult = queryPurchases(ProductType.SUBS)
                        val inAppResult = queryPurchases(ProductType.INAPP)

                        if (subscriptionResult?.purchasesList?.isNotEmpty() == true || inAppResult?.purchasesList?.isNotEmpty() == true) {
                            subscriptionResult?.purchasesList?.forEach {
                                val productId = JSONObject(it.originalJson).optString("productId", "")

                                getSubscriptionDetails(listOf(productId), object : PurchaseListener() {
                                    override fun onProductDetail(productDetailsList: List<ProductDetails>) {
                                        productDetailsList.forEach { productDetails ->
                                            val purchaseData = PurchaseData(
                                                purchaseToken = it.purchaseToken,
                                                purchaseTime = it.purchaseTime,
                                                isAutoRenewing = it.isAutoRenewing,
                                                isAcknowledged = it.isAcknowledged,
                                                orderId = it.orderId.orEmpty(),
                                                productId = productId,
                                                purchaseState = it.purchaseState,
                                                productType = ProductType.SUBS,
                                                hasFreeTrial = getFreeTrial(productDetails).isNotEmpty(),
                                                trialPeriod = getFreeTrial(productDetails)
                                            )
                                            if (it.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                                if (!purchaseData.isAcknowledged) {
                                                    GlobalScope.launch(Dispatchers.IO) {
                                                        acknowledgePurchase(purchaseData.purchaseToken, object : PurchaseListener() {
                                                            override fun isPurchaseAcknowledge(isReady: Boolean) {
                                                                if (isReady) {
                                                                    purchaseData.isAcknowledged = true
                                                                }
                                                                if (proProductListList.contains(productId)) {
                                                                    storePurchaseData?.savePurchaseData(CURRENT_PURCHASE, purchaseData)
                                                                    storePurchaseData?.setPremiumPurchase(true)
                                                                } else {
                                                                    storePurchaseData?.savePurchaseData(productId, purchaseData)
                                                                }
                                                            }
                                                        })
                                                    }
                                                } else {
                                                    if (proProductListList.contains(productId)) {
                                                        storePurchaseData?.savePurchaseData(CURRENT_PURCHASE, purchaseData)
                                                        storePurchaseData?.setPremiumPurchase(true)
                                                    } else {
                                                        storePurchaseData?.savePurchaseData(productId, purchaseData)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                })
                            }

                            inAppResult?.purchasesList?.forEach {
                                val productId = JSONObject(it.originalJson).optString("productId", "")
                                val purchaseData = PurchaseData(
                                    purchaseToken = it.purchaseToken,
                                    purchaseTime = it.purchaseTime,
                                    isAutoRenewing = it.isAutoRenewing,
                                    isAcknowledged = it.isAcknowledged,
                                    orderId = it.orderId.orEmpty(),
                                    productId = productId,
                                    purchaseState = it.purchaseState,
                                    productType = ProductType.INAPP
                                )

                                if (it.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                    if (!purchaseData.isAcknowledged) {
                                        GlobalScope.launch(Dispatchers.IO) {
                                            acknowledgePurchase(purchaseData.purchaseToken, object : PurchaseListener() {
                                                override fun isPurchaseAcknowledge(isReady: Boolean) {
                                                    if (isReady) {
                                                        purchaseData.isAcknowledged = true
                                                    }
                                                    if (proProductListList.contains(productId)) {
                                                        storePurchaseData?.savePurchaseData(CURRENT_PURCHASE, purchaseData)
                                                        storePurchaseData?.setPremiumPurchase(true)
                                                    } else {
                                                        storePurchaseData?.savePurchaseData(productId, purchaseData)
                                                    }
                                                }
                                            })
                                        }
                                    } else {
                                        if (proProductListList.contains(productId)) {
                                            storePurchaseData?.savePurchaseData(CURRENT_PURCHASE, purchaseData)
                                            storePurchaseData?.setPremiumPurchase(true)
                                        } else {
                                            storePurchaseData?.savePurchaseData(productId, purchaseData)
                                        }
                                    }
                                }
                            }
                            callback((storePurchaseData?.isPremiumPurchased() == true))
                        } else {
                            callback(false)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback(false)
                    }
                }
            }
        })
    }


    private fun getProductDetailParam(
        listOf: List<String>, productType: String
    ): ArrayList<QueryProductDetailsParams.Product> {
        val productList: ArrayList<QueryProductDetailsParams.Product> = arrayListOf()
        listOf.forEach {
            productList.add(
                QueryProductDetailsParams.Product.newBuilder().setProductId(it).setProductType(productType).build()
            )
        }
        return productList
    }

    /**
     * Retrieves details of subscription products using their IDs and notifies a listener with the result.
     *
     * This method queries the BillingClient to retrieve details of subscription products based on the provided list of subscription IDs.
     * It then invokes the appropriate callback on the provided PurchaseListener with the retrieved subscription details.
     *
     * @param listOfSubscription A list of subscription IDs for which product details need to be retrieved.
     * @param purchaseListener The listener that will receive callbacks regarding the subscription details retrieval.
     *
     * Example Usage:
     * ```
     * val subscriptionIds = listOf("sub_id_1", "sub_id_2")
     * val listener = object : PurchaseListener() {
     *     override fun onBillingError(responseCode: Int, debugMessage: String) {
     *         // Handle billing error
     *     }
     *
     *     override fun onProductDetail(productDetailsList: List<ProductDetails>) {
     *         // Handle retrieved subscription details
     *     }
     * }
     *
     * getSubscriptionDetails(subscriptionIds, listener)
     * ```
     */
    suspend fun getSubscriptionDetails(listOfSubscription: List<String>, purchaseListener: PurchaseListener) {
        isBillingInitialized(object : PurchaseListener() {
            override fun onBillingInitialized() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val subscriptionList = getProductDetailParam(listOfSubscription, ProductType.SUBS)

                        val subscriptionDetailsDeferred = async(Dispatchers.IO) {
                            billingClient?.queryProductDetails(QueryProductDetailsParams.newBuilder().setProductList(subscriptionList).build())
                        }

                        val subscription = subscriptionDetailsDeferred.await()

                        val subscriptionDetailsList = subscription?.productDetailsList.orEmpty()

                        val skuDetails: List<ProductDetails> = subscriptionDetailsList

                        purchaseListener.onProductDetail(skuDetails)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        purchaseListener.onBillingError(-1, e.message.orEmpty())
                    }

                }
            }

            override fun onBillingError(responseCode: Int, debugMessage: String) {
                purchaseListener.onBillingError(responseCode, debugMessage)
            }
        })
    }

    /**
     * Retrieves details of in-app products using their IDs and notifies a listener with the result.
     *
     * This method queries the BillingClient to retrieve details of in-app products based on the provided list of product IDs.
     * It then invokes the appropriate callback on the provided PurchaseListener with the retrieved in-app product details.
     *
     * @param listOfProduct A list of product IDs for which details need to be retrieved.
     * @param purchaseListener The listener that will receive callbacks regarding the in-app product details retrieval.
     *
     * Example Usage:
     * ```
     * val productIds = listOf("product_id_1", "product_id_2")
     * val listener = object : PurchaseListener() {
     *     override fun onBillingError(responseCode: Int, debugMessage: String) {
     *         // Handle billing error
     *     }
     *
     *     override fun onProductDetail(productDetailsList: List<ProductDetails>) {
     *         // Handle retrieved in-app product details
     *     }
     * }
     *
     * getProductDetails(productIds, listener)
     * ```
     */
    suspend fun getProductDetails(listOfProduct: List<String>, purchaseListener: PurchaseListener) {
        isBillingInitialized(object : PurchaseListener() {
            override fun onBillingError(responseCode: Int, debugMessage: String) {
                purchaseListener.onBillingError(responseCode, debugMessage)
            }

            override fun onBillingInitialized() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val inAppList = getProductDetailParam(listOfProduct, ProductType.INAPP)

                        val inAppDetailsDeferred = async(Dispatchers.IO) {
                            billingClient?.queryProductDetails(QueryProductDetailsParams.newBuilder().setProductList(inAppList).build())
                        }

                        val inApp = inAppDetailsDeferred.await()
                        val inAppDetailsList = inApp?.productDetailsList.orEmpty()
                        val skuDetails: List<ProductDetails> = inAppDetailsList

                        purchaseListener.onProductDetail(skuDetails)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        purchaseListener.onBillingError(-1, e.message.orEmpty())
                    }
                }
            }
        })
    }

    /**
     * Queries and retrieves details of both in-app and subscription products using their IDs,
     * and notifies a listener with the combined result.
     *
     * This method queries the BillingClient to retrieve details of both in-app and subscription products
     * based on the provided lists of product and subscription IDs. It then invokes the appropriate callback
     * on the provided PurchaseListener with the retrieved product details.
     *
     * @param products A list of in-app product IDs for which details need to be retrieved.
     * @param subscriptions A list of subscription IDs for which details need to be retrieved.
     * @param purchaseListener The listener that will receive callbacks regarding the product details retrieval.
     *
     * Example Usage:
     * ```
     * val inAppProducts = listOf("product_id_1", "product_id_2")
     * val subscriptionProducts = listOf("sub_id_1", "sub_id_2")
     * val listener = object : PurchaseListener() {
     *     override fun onBillingError(responseCode: Int, debugMessage: String) {
     *         // Handle billing error
     *     }
     *
     *     override fun onProductDetail(productDetailsList: List<ProductDetails>) {
     *         // Handle retrieved product details (in-app and subscription)
     *     }
     * }
     *
     * queryAllProductDetails(inAppProducts, subscriptionProducts, listener)
     * ```
     */
    fun queryAllProductDetails(
        products: List<String>, subscriptions: List<String>, purchaseListener: PurchaseListener
    ) {
        isBillingInitialized(object : PurchaseListener() {
            override fun onBillingError(responseCode: Int, debugMessage: String) {
                purchaseListener.onBillingError(responseCode, debugMessage)
            }

            override fun onBillingInitialized() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val inAppList = getProductDetailParam(
                            products, ProductType.INAPP
                        )

                        val subscriptionList = getProductDetailParam(
                            subscriptions, ProductType.SUBS
                        )

                        val inAppDetailsDeferred = async(Dispatchers.IO) {
                            billingClient?.queryProductDetails(
                                QueryProductDetailsParams.newBuilder().setProductList(inAppList).build()
                            )
                        }
                        val subscriptionDetailsDeferred = async(Dispatchers.IO) {
                            billingClient?.queryProductDetails(
                                QueryProductDetailsParams.newBuilder().setProductList(subscriptionList).build()
                            )
                        }

                        val inApp = inAppDetailsDeferred.await()
                        val subscription = subscriptionDetailsDeferred.await()

                        val inAppDetailsList = inApp?.productDetailsList.orEmpty()
                        val subscriptionDetailsList = subscription?.productDetailsList.orEmpty()

                        val skuDetails: List<ProductDetails> = inAppDetailsList + subscriptionDetailsList
                        purchaseListener.onProductDetail(skuDetails)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        purchaseListener.onBillingError(-1, e.message.orEmpty())
                    }
                }
            }
        })
    }

    /**
     * Extracts and returns the duration of the free trial period from the provided ProductDetails object.
     *
     * This method processes the subscription offer details of a ProductDetails object to identify and extract
     * the duration of the free trial period, if available. The trial period duration is returned in a HashMap
     * containing the offer type and duration.
     *
     * @param productDetails The ProductDetails object from which to extract the free trial duration.
     * @return A HashMap containing the trial offer type (DAY, WEEK, MONTH) and duration, or an empty HashMap if no free trial is found.
     *
     * Example Usage:
     * ```
     * val sampleProductDetails: ProductDetails = // Obtain product details from your logic
     * val trialDuration = getFreeTrial(sampleProductDetails)
     * if (trialDuration.isNotEmpty()) {
     *     val offerType = trialDuration[TrialPeriod.KEY_OFFER_TYPE.name]
     *     val offerDuration = trialDuration[TrialPeriod.KEY_OFFER_DURATION.name]
     *     // Process the trial duration and type
     *     Log.d("TrialOffer", "Offer Type: $offerType, Duration: $offerDuration")
     * } else {
     *     // No free trial available
     * }
     * ```
     */
    fun getFreeTrial(productDetails: ProductDetails): HashMap<String, String> {
        val duration = hashMapOf<String, String>()
        productDetails.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.let {
            for (offer in it) {
                if (offer.formattedPrice.equals("Free", ignoreCase = true)) {
                    val billingPeriod = offer.billingPeriod

                    // P1w: 1 year subscription.
                    // P6M: 6 months subscription.
                    // P14D: 14 days subscription.
                    // PT1H: 1 hour subscription.

                    val dayOrMonth = billingPeriod.toCharArray().last().toString()
                    val trialDuration = billingPeriod.substring(1, billingPeriod.toCharArray().lastIndex).toInt()
                    when {
                        dayOrMonth.equals("d", ignoreCase = true) -> {
                            duration[TrialPeriod.KEY_OFFER_TYPE.name] = TrialPeriod.DAY.name
                            duration[TrialPeriod.KEY_OFFER_DURATION.name] = trialDuration.toString()
                            return duration
                        }

                        dayOrMonth.equals("w", ignoreCase = true) -> {
                            duration[TrialPeriod.KEY_OFFER_TYPE.name] = TrialPeriod.WEEK.name
                            duration[TrialPeriod.KEY_OFFER_DURATION.name] = "${trialDuration * 7}"
                            return duration
                        }

                        dayOrMonth.equals("m", ignoreCase = true) -> {
                            duration[TrialPeriod.KEY_OFFER_TYPE.name] = TrialPeriod.MONTH.name
                            duration[TrialPeriod.KEY_OFFER_DURATION.name] = trialDuration.toString()
                            return duration
                        }

                        else -> {
                            return hashMapOf()
                        }
                    }
                }
            }
        }
        return duration
    }
    /**
     * Retrieves and returns the formatted price of a product from the provided ProductDetails object.
     *
     * This method determines the product type (subscription or in-app) of the given ProductDetails and retrieves
     * the formatted price based on the billing period. For subscriptions, it extracts the formatted price from the
     * last pricing phase of the subscription offer details. For in-app products, it retrieves the formatted price
     * from the one-time purchase offer details.
     *
     * @param productDetails The ProductDetails object from which to extract the formatted price.
     * @return A String representing the formatted price of the product (₹1200), or an empty string if no price is found .
     *
     * Example Usage:
     * ```
     * val sampleProductDetails: ProductDetails = // Obtain product details from your logic
     * val formattedPrice = getFormattedPrice(sampleProductDetails)
     * if (formattedPrice.isNotEmpty()) {
     *     // Display the formatted price to the user
     *     Log.d("ProductPrice", "Formatted Price: $formattedPrice")
     * } else {
     *     // No price information available
     * }
     * ```
     */
    fun getFormattedPrice(productDetails: ProductDetails): String {
        if (productDetails.productType == ProductType.SUBS) {
            for (offer in productDetails.subscriptionOfferDetails?.last()?.pricingPhases?.pricingPhaseList!!) {
                return when (offer.billingPeriod) {
                    "P1Y" -> offer.formattedPrice.replace(".00", "")
                    "P1M" -> offer.formattedPrice.replace(".00", "")
                    "P1D" -> offer.formattedPrice.replace(".00", "")
                    else -> offer.formattedPrice.replace(".00", "")
                    // Add more cases for other billing periods if needed
                }
            }
        } else {
            return productDetails.oneTimePurchaseOfferDetails?.formattedPrice?.replace(".00", "").orEmpty()
        }
        return "" // Return an empty string if no matching billing period is found
    }
    /**
     * Retrieves and returns the price of a product from the provided ProductDetails object.
     *
     * This method determines the product type (subscription or in-app) of the given ProductDetails and retrieves
     * the price based on the billing period. For subscriptions, it extracts the price from the last pricing phase
     * of the subscription offer details. For in-app products, it retrieves the price from the one-time purchase offer details.
     *
     * @param productDetails The ProductDetails object from which to extract the price.
     * @return A Float representing the price of the product, or 0.0f if no price is found.
     *
     * Example Usage:
     * ```
     * val sampleProductDetails: ProductDetails = // Obtain product details from your logic
     * val productPrice = getPriceValue(sampleProductDetails)
     * if (productPrice > 0.0f) {
     *     // Display the product price to the user
     *     Log.d("ProductPrice", "Price: $productPrice")
     * } else {
     *     // No price information available
     * }
     * ```
     */
    fun getPriceValue(productDetails: ProductDetails): Float {
        if (productDetails.productType == ProductType.SUBS) {
            for (offer in productDetails.subscriptionOfferDetails?.last()?.pricingPhases?.pricingPhaseList!!) {
                return when (offer.billingPeriod) {
                    "P1Y" -> (offer.priceAmountMicros / 1_000_000f)
                    "P1M" -> (offer.priceAmountMicros / 1_000_000f)
                    "P1D" -> (offer.priceAmountMicros / 1_000_000f)
                    else -> (offer.priceAmountMicros / 1_000_000f)
                    // Add more cases for other billing periods if needed
                }
            }
        } else {
            return (productDetails.oneTimePurchaseOfferDetails?.priceAmountMicros!! / 1_000_000f)
        }
        return 0f // Return an empty string if no matching billing period is found
    }

    fun Activity.purchaseProduct(productDetails: ProductDetails) {
        makePurchase(productDetails)
    }

    fun Activity.purchaseSubscription(productDetails: ProductDetails) {
        makePurchase(productDetails)
    }

    /**
     * Consumes a purchased product by its ID and notifies the listener about the consumption result.
     *
     * This method initializes the BillingClient and attempts to consume a purchased product using its product ID.
     * If the consumption is successful, the listener's `onPurchaseConsume` callback is triggered.
     * If there is an error during the consumption process, the listener's `onBillingError` callback is called with
     * the corresponding response code and debug message.
     *
     * @param productID The ID of the product to be consumed.
     * @param purchaseListener The listener to handle the consumption result and errors.
     *
     * Example Usage:
     * ```
     * val sampleProductID: String = "sample_product_id"
     * consumeProductById(sampleProductID, object : PurchaseListener() {
     *     override fun onPurchaseConsume() {
     *         // Handle successful consumption
     *         Log.d("PurchaseConsume", "Product $sampleProductID consumed successfully.")
     *     }
     *
     *     override fun onBillingError(responseCode: Int, debugMessage: String) {
     *         // Handle consumption error
     *         Log.e("PurchaseConsumeError", "Error consuming product: $debugMessage (Code: $responseCode)")
     *     }
     * })
     * ```
     */
    fun consumeProductById(productID: String, purchaseListener: PurchaseListener) {
        isBillingInitialized(object : PurchaseListener() {
            override fun onBillingError(responseCode: Int, debugMessage: String) {
                purchaseListener.onBillingError(responseCode, debugMessage)
            }

            override fun onBillingInitialized() {
                var purchaseData = storePurchaseData?.getPurchaseData(productID)

                if (purchaseData == null) purchaseData = storePurchaseData?.getPurchaseData(CURRENT_PURCHASE)

                if (purchaseData != null) {
                    val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseData.purchaseToken).build()
                    billingClient?.consumeAsync(consumeParams) { billingResult, purchaseToken ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            purchaseListener.onPurchaseConsume()
                        } else {
                            purchaseListener.onBillingError(billingResult.responseCode, billingResult.debugMessage)
                        }
                    }
                } else {
                    purchaseListener.onBillingError(BillingClient.BillingResponseCode.DEVELOPER_ERROR, "Current purchase not found")
                }
            }
        })
    }

    /**
     * Retrieves the purchase history of both subscription and in-app products and notifies the listener with the history.
     *
     * This method initializes the BillingClient and queries the purchase history for both subscription and in-app products.
     * It then combines the purchase history from active subscriptions and individual in-app purchases, and returns the
     * combined history to the listener. If no purchase history is available, an empty list is returned.
     *
     * @param purchaseListener The listener to handle the purchase history or errors.
     *
     * Example Usage:
     * ```
     * getPurchaseHistory(object : PurchaseListener() {
     *     override fun onPurchaseHistoryReceived(historyList: List<HistoryPurchaseData>) {
     *         if (historyList.isNotEmpty()) {
     *             // Display purchase history to the user
     *             for (history in historyList) {
     *                 val productType = if (history.productType == ProductType.SUBS) "Subscription" else "In-App"
     *                 val autoRenewingStatus = if (history.isAutoRenewing) "Auto-Renewing" else "Not Auto-Renewing"
     *                 Log.d("PurchaseHistory", "Product: ${history.productId}, Type: $productType, Order ID: ${history.orderId}, Time: ${history.purchaseTime}, $autoRenewingStatus")
     *             }
     *         } else {
     *             // No purchase history available
     *             Log.d("PurchaseHistory", "No purchase history available.")
     *         }
     *     }
     * })
     * ```
     */
    fun getPurchaseHistory(purchaseListener: PurchaseListener) {
        isBillingInitialized(object : PurchaseListener() {
            override fun onBillingInitialized() {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val historyList = arrayListOf<HistoryPurchaseData>()

                        val purchasesResultSubHistory = queryPurchasesHistory(ProductType.SUBS)
                        val purchasesResultINAPPHistory = queryPurchasesHistory(ProductType.INAPP)

                        val subscriptionResult = queryPurchases(ProductType.SUBS)
                        val inAppResult = queryPurchases(ProductType.INAPP)

                        if (subscriptionResult?.purchasesList?.isNotEmpty() == true || inAppResult?.purchasesList?.isNotEmpty() == true) {
                            subscriptionResult?.purchasesList?.forEach {
                                val productId = JSONObject(it.originalJson).optString("productId", "")
                                historyList.add(
                                    HistoryPurchaseData(
                                        productId, it.orderId.orEmpty(), it.purchaseTime, it.isAutoRenewing, ProductType.SUBS
                                    )
                                )
                            }
                            inAppResult?.purchasesList?.forEach {
                                val productId = JSONObject(it.originalJson).optString("productId", "")
                                historyList.add(
                                    HistoryPurchaseData(
                                        productId, it.orderId.orEmpty(), it.purchaseTime, it.isAutoRenewing, ProductType.INAPP
                                    )
                                )
                            }
                        }

                        if (!purchasesResultSubHistory?.purchaseHistoryRecordList.isNullOrEmpty() || !purchasesResultINAPPHistory?.purchaseHistoryRecordList.isNullOrEmpty()) {
                            purchasesResultSubHistory?.purchaseHistoryRecordList?.forEach {
                                if (historyList.none { historyPurchaseData -> historyPurchaseData.productId == JSONObject(it.originalJson).optString("productId", "") }) historyList.add(
                                    HistoryPurchaseData(
                                        JSONObject(it.originalJson).optString("productId", ""), "", it.purchaseTime, true, ProductType.SUBS
                                    )
                                )
                            }

                            purchasesResultINAPPHistory?.purchaseHistoryRecordList?.forEach {
                                if (historyList.none { historyPurchaseData -> historyPurchaseData.productId == JSONObject(it.originalJson).optString("productId", "") }) historyList.add(
                                    HistoryPurchaseData(
                                        JSONObject(it.originalJson).optString("productId", ""), "", it.purchaseTime, false, ProductType.INAPP
                                    )
                                )
                            }
                            purchaseListener.onPurchaseHistoryReceived(historyList)
                        } else {
                            purchaseListener.onPurchaseHistoryReceived(historyList)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        purchaseListener.onPurchaseHistoryReceived(arrayListOf())
                    }
                }
            }

            override fun onBillingError(responseCode: Int, debugMessage: String) {
                purchaseListener.onPurchaseHistoryReceived(arrayListOf())
            }
        })
    }

    private fun Activity.makePurchase(productDetails: ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                .setProductDetails(productDetails)
                // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                // for a list of offers that are available to the user
                .setOfferToken(if (productDetails.productType == ProductType.SUBS) productDetails.subscriptionOfferDetails?.get(0)!!.offerToken else "").build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList).build()
        // Launch the billing flow
        billingClient?.launchBillingFlow(this@makePurchase, billingFlowParams)
    }

    private fun handlePurchase(purchases: Purchase) {
        if (purchases.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val productId = JSONObject(purchases.originalJson).optString("productId", "")

            queryAllProductDetails(listOf(productId), listOf(productId), object : PurchaseListener() {
                override fun onProductDetail(productDetailsList: List<ProductDetails>) {
                    productDetailsList.forEach { productDetailsListItem ->

                        val purchaseData = PurchaseData(
                            purchaseToken = purchases.purchaseToken,
                            purchaseTime = purchases.purchaseTime,
                            isAutoRenewing = purchases.isAutoRenewing,
                            isAcknowledged = purchases.isAcknowledged,
                            orderId = purchases.orderId.orEmpty(),
                            productId = productId,
                            purchaseState = purchases.purchaseState,
                            productType = productDetailsListItem.productType,
                            hasFreeTrial = getFreeTrial(productDetailsListItem).isNotEmpty(),
                            trialPeriod = getFreeTrial(productDetailsListItem)
                        )

                        if (!purchases.isAcknowledged) {
                            GlobalScope.launch(Dispatchers.IO) {
                                try {
                                    acknowledgePurchase(purchases.purchaseToken, object : PurchaseListener() {
                                        override fun isPurchaseAcknowledge(isReady: Boolean) {
                                            if (isReady) {
                                                purchaseData.isAcknowledged = true
                                            }
                                            EventBus.getDefault().post(PurchaseEventModel(true, purchaseData, -1, ""))
                                        }
                                    })
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    EventBus.getDefault().post(PurchaseEventModel(true, purchaseData, -1, ""))
                                }
                            }
                        } else {
                            EventBus.getDefault().post(PurchaseEventModel(true, purchaseData, -1, ""))
                        }
                    }
                }
            })
        } else {
            EventBus.getDefault().post(PurchaseEventModel(false, null, purchases.purchaseState, ""))
        }
    }

    private suspend fun acknowledgePurchase(purchaseToken: String, listener: PurchaseListener) {
        try {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchaseToken)
            val result = billingClient?.acknowledgePurchase(acknowledgePurchaseParams.build())
            listener.isPurchaseAcknowledge(result?.responseCode == BillingClient.BillingResponseCode.OK)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            listener.isPurchaseAcknowledge(false)
        }
    }

    private suspend fun queryPurchases(productType: String): PurchasesResult? {
        val params = QueryPurchasesParams.newBuilder().setProductType(productType)
        return billingClient?.queryPurchasesAsync(params.build())
    }

    private suspend fun queryPurchasesHistory(productType: String): PurchaseHistoryResult? {
        val params = QueryPurchaseHistoryParams.newBuilder().setProductType(productType)
        return billingClient?.queryPurchaseHistory(params.build())
    }

    private fun isBillingInitialized(purchaseListener: PurchaseListener) {
        if (billingClient == null) {
            purchaseListener.onBillingError(BillingClient.BillingResponseCode.DEVELOPER_ERROR, "Billing client null")
            return
        }
        if (billingClient?.isReady == true && billingClient?.connectionState == BillingClient.ConnectionState.CONNECTED) {
            purchaseListener.onBillingInitialized()
        } else {
            billingClient?.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    purchaseListener.onBillingError(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED, "")
                }

                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        purchaseListener.onBillingInitialized()
                    } else {
                        purchaseListener.onBillingError(billingResult.responseCode, billingResult.debugMessage)
                    }
                }
            })
        }
    }
}