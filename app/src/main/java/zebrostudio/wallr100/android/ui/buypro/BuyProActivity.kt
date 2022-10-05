package zebrostudio.wallr100.android.ui.buypro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.android.billingclient.api.*
import com.google.firebase.crashlytics.internal.model.ImmutableList
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_buy_pro.*
import kotlinx.android.synthetic.main.item_buy_pro_features.view.*
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.ui.ImageLoader
import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType.PURCHASE
import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType.RESTORE
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig.Companion.ITEM_SKU
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig.Companion.ITEM_SKU_TEST
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig.Companion.NA
import zebrostudio.wallr100.android.utils.*
import zebrostudio.wallr100.presentation.buypro.BuyProContract
import zebrostudio.wallr100.presentation.buypro.BuyProContract.BuyProView
import javax.inject.Inject

class BuyProActivity : BaseActivity(), BuyProView {
  @Inject
  internal lateinit var buyProPresenter: BuyProContract.BuyProPresenter
  @Inject
  internal lateinit var imageLoader: ImageLoader

  private lateinit var materialDialog: MaterialDialog

  private var billingClient: BillingClient? = null
  private var billingFlowParams: BillingFlowParams? = null

//  private val purchaseFinishedListener = OnIabPurchaseFinishedListener { result, purchase ->
//    if (result.isSuccess) {
//      buyProPresenter.verifyTransaction(
//        purchase.packageName,
//        purchase.sku,
//        purchase.token,
//        PURCHASE
//      )
//    } else {
//      showTryRestoringInfo()
//      dismissWaitLoader()
//    }
//  }
//  private val queryInventoryFinishedListener = QueryInventoryFinishedListener { result, inventory ->
//    if (result.isSuccess) {
//      buyProPresenter.verifyTransaction(
//        inventory.getPurchase(ITEM_SKU_TEST).packageName,
//        inventory.getPurchase(ITEM_SKU_TEST).sku,
//        inventory.getPurchase(ITEM_SKU_TEST).token,
//        RESTORE
//      )
//    } else {
//      showGenericVerificationError()
//      dismissWaitLoader()
//    }
//  }

  private val purchasesUpdatedListener =
    PurchasesUpdatedListener { billingResult, purchases ->
//      buyProPresenter.handlePurchaseUpdationEvent(billingResult, purchases)
      billingResult
      purchases
      if((purchases?.size ?: 0) > 0){
        purchases!![0].apply {
          buyProPresenter.verifyTransaction(
            billingResult.responseCode,
            packageName,
            products[0],
            purchaseToken,
            PURCHASE
          )
        }
      } else {
        buyProPresenter.verifyTransaction(
          billingResult.responseCode,
          NA,
          NA,
          NA,
          PURCHASE
        )
      }

    }

  val queryProductDetailsParams =
    QueryProductDetailsParams.newBuilder()
      .setProductList(
        ImmutableList.from(
          QueryProductDetailsParams.Product.newBuilder()
            .setProductId(ITEM_SKU)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()))
      .build()

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    buyProPresenter.attachView(this)
    setContentView(R.layout.activity_buy_pro)
    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

    loadWallrLogo()
    showProFeatures(buildProFeaturesList())
    attachClickListeners()
    initBiller()
  }

  override fun onDestroy() {
    buyProPresenter.detachView()
    super.onDestroy()
  }

  override fun onBackPressed() {
    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    super.onBackPressed()
  }

  override fun showInvalidPurchaseError() {
    errorToast(stringRes(R.string.buy_pro_invalid_purchase_message))
  }

  override fun showUnableToVerifyPurchaseError() {
    errorToast(stringRes(R.string.buy_pro_unable_to_verify_purchase_message))
  }

  override fun showNoInternetErrorMessage(premiumOperationType: PremiumTransactionType) {
    when (premiumOperationType) {
      PURCHASE -> errorToast(
        stringRes(R.string.buy_pro_purchase_ensure_working_internet_connection_message)
      )
      RESTORE -> errorToast(
        stringRes(R.string.buy_pro_restore_ensure_working_internet_connection_message)
      )
    }
  }

  override fun showGenericVerificationError() {
    errorToast(stringRes(R.string.generic_error_message))
  }

  override fun showSuccessfulTransactionMessage(proTransactionType: PremiumTransactionType) {
    when (proTransactionType) {
      PURCHASE -> successToast(
        stringRes(R.string.buy_pro_purchase_successful_message)
      )
      RESTORE -> successToast(
        stringRes(R.string.buy_pro_restore_successful_message)
      )
    }
  }

  override fun showWaitLoader(proTransactionType: PremiumTransactionType) {
    val contentStringId = when (proTransactionType) {
      PURCHASE -> stringRes(R.string.buy_pro_verifying_purchase_message)
      RESTORE -> stringRes(R.string.buy_pro_verifying_restore_message)
    }
    materialDialog = MaterialDialog.Builder(this)
        .widgetColor(colorRes(R.color.accent))
        .contentColor(colorRes(R.color.white))
        .content(contentStringId)
        .backgroundColor(colorRes(R.color.primary_dark_material_dark))
        .progress(true, 0)
        .cancelable(false)
        .progressIndeterminateStyle(false)
        .build()
    materialDialog.show()
  }

  override fun dismissWaitLoader() {
    materialDialog.dismiss()
  }

  override fun finishWithResult() {
    val intent = Intent()
    intent.putExtra(
      PurchaseTransactionConfig.PURCHASE_TAG,
      PurchaseTransactionConfig.PURCHASE_REQUEST_CODE
    )
    setResult(PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE, intent)
    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    finish()
  }

  override fun isBillerReady(): Boolean = billingClient?.isReady ?: false

  override fun initBiller(isForced: Boolean){
    billingClient =  BillingClient.newBuilder(this)
      .setListener(purchasesUpdatedListener)
      .enablePendingPurchases()
      .build()
    establishBillingConnection(isForced)
  }

  override fun launchPurchase() {
    billingClient?.launchBillingFlow(this@BuyProActivity, billingFlowParams!!)
  }

  override fun launchRestore() {
    billingClient?.launchBillingFlow(this@BuyProActivity, billingFlowParams!!)
  }

  private fun loadWallrLogo() {
    imageLoader.load(this, R.drawable.ic_wallr_logo_large, proLogo)
  }

  private fun showProFeatures(buildBuyProFeaturesList: List<Triple<Int, Int, Int>>) {
    val itemIterator = buildBuyProFeaturesList.iterator()
    val layoutInflater = LayoutInflater.from(this)
    itemIterator.forEach {
      val proFeatureView = layoutInflater.inflate(R.layout.item_buy_pro_features, null)
      with(proFeatureView) {
        imageView.setImageResource(it.first)
        headerTextView.setText(it.second)
        descriptionTextView.setText(it.third)
      }
      buyProFeatures.addView(proFeatureView)
    }
  }

  private fun buildProFeaturesList(): List<Triple<Int, Int, Int>> {
    return mutableListOf<Triple<Int, Int, Int>>().apply {
      add(
        Triple(
          R.drawable.ic_remove_ads_white, R.string.buy_pro_features_ads_header,
          R.string.buy_pro_features_ads_sub_header
        )
      )
      add(
        Triple(
          R.drawable.ic_high_definition, R.string.buy_pro_features_downloads_header,
          R.string.buy_pro_features_downloads_sub_header
        )
      )
      add(
        Triple(
          R.drawable.ic_automatic_wallpaper_changer,
          R.string.buy_pro_features_automatic_wallpaper_changer_header,
          R.string.buy_pro_features_automatic_wallpaper_changer_sub_header
        )
      )
      add(
        Triple(
          R.drawable.ic_crystallize_white, R.string.buy_pro_features_crystallize_header,
          R.string.buy_pro_features_crystallize_sub_header
        )
      )
      add(
        Triple(
          R.drawable.ic_share_white, R.string.buy_pro_features_share_header,
          R.string.buy_pro_features_share_sub_header
        )
      )
    }
  }

  private fun attachClickListeners() {
    purchaseButton.setOnClickListener {
      buyProPresenter.handlePurchaseClicked()
    }

    restoreButton.setOnClickListener {
      buyProPresenter.handleRestoreClicked()
    }

    backButtonPro.setOnClickListener {
      onBackPressed()
    }
  }

  private fun showTryRestoringInfo() {
    infoToast(stringRes(R.string.buy_pro_try_restoring_message))
  }

  private fun establishBillingConnection(isForced : Boolean = false){
    billingClient?.startConnection(object : BillingClientStateListener {
      override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
          billingClient?.queryProductDetailsAsync(queryProductDetailsParams) {
              billingResult,
              productDetailsList ->
            Log.d(this.javaClass.name, "the queryProductDetailsAsync response is $billingResult, $productDetailsList")


            val productDetailsParamsList = listOf(
              BillingFlowParams.ProductDetailsParams.newBuilder()
                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                .setProductDetails(productDetailsList.first())
                .build()
            )

            billingFlowParams = BillingFlowParams.newBuilder()
              .setProductDetailsParamsList(productDetailsParamsList)
              .build()

            if(isForced){
              buyProPresenter.handlePurchaseClicked()
            }
          }
        }
      }
      override fun onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.

      }
    })
  }

}

enum class PremiumTransactionType {
  PURCHASE,
  RESTORE
}
