package zebrostudio.wallr100.android.ui.buypro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.zebrostudio.librarypurchaseflow.IabHelper
import com.zebrostudio.librarypurchaseflow.IabHelper.OnIabPurchaseFinishedListener
import com.zebrostudio.librarypurchaseflow.IabHelper.QueryInventoryFinishedListener
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_buy_pro.backButtonPro
import kotlinx.android.synthetic.main.activity_buy_pro.buyProFeatures
import kotlinx.android.synthetic.main.activity_buy_pro.proLogo
import kotlinx.android.synthetic.main.activity_buy_pro.purchaseButton
import kotlinx.android.synthetic.main.activity_buy_pro.restoreButton
import kotlinx.android.synthetic.main.item_buy_pro_features.view.descriptionTextView
import kotlinx.android.synthetic.main.item_buy_pro_features.view.headerTextView
import kotlinx.android.synthetic.main.item_buy_pro_features.view.imageView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType.PURCHASE
import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType.RESTORE
import zebrostudio.wallr100.android.utils.colorRes
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.infoToast
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.successToast
import zebrostudio.wallr100.presentation.buypro.BuyProContract
import javax.inject.Inject

class BuyProActivity : BaseActivity(), BuyProContract.BuyProView {
  @Inject
  internal lateinit var buyProPresenter: BuyProContract.BuyProPresenter

  private lateinit var materialDialog: MaterialDialog
  private var iabHelper: IabHelper? = null

  private val purchaseFinishedListener = OnIabPurchaseFinishedListener { result, purchase ->
    if (result.isFailure) {
      showTryRestoringInfo()
      dismissWaitLoader()
    } else {
      buyProPresenter.verifyPurchase(purchase.packageName,
          purchase.sku,
          purchase.token,
          PURCHASE)
    }
  }
  private val queryInventoryFinishedListener = QueryInventoryFinishedListener { result, inv ->
    if (result.isFailure) {
      showGenericVerificationError()
      dismissWaitLoader()
    } else {
      buyProPresenter.verifyPurchase(
          inv.getPurchase(PurchaseTransactionConfig.ITEM_SKU).packageName,
          inv.getPurchase(PurchaseTransactionConfig.ITEM_SKU).sku,
          inv.getPurchase(PurchaseTransactionConfig.ITEM_SKU).token,
          RESTORE)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    buyProPresenter.attachView(this)
    setContentView(R.layout.activity_buy_pro)
    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

    loadWallrLogo()
    showProFeatures(buildProFeaturesList())
    attachClickListeners()
  }

  override fun onResume() {
    super.onResume()
    if (iabHelper == null || iabHelper?.isSetupDone == false) {
      iabHelper = IabHelper(this, PurchaseTransactionConfig.BASE64_ENCODED_PUBLIC_KEY)
      iabHelper?.startSetup {}
    }
  }

  override fun onDestroy() {
    buyProPresenter.detachView()
    super.onDestroy()
  }

  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    if (iabHelper?.handleActivityResult(requestCode,
            resultCode, data) == false) {
      super.onActivityResult(requestCode, resultCode, data)
    }
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
          stringRes(R.string.buy_pro_purchase_ensure_working_internet_connection_message))
      RESTORE -> errorToast(
          stringRes(R.string.buy_pro_restore_ensure_working_internet_connection_message))
    }
  }

  override fun showGenericVerificationError() {
    errorToast(stringRes(R.string.generic_error_message))
  }

  override fun showSuccessfulTransactionMessage(proTransactionType: PremiumTransactionType) {
    when (proTransactionType) {
      PURCHASE -> successToast(
          stringRes(R.string.buy_pro_purchase_successful_message))
      RESTORE -> successToast(
          stringRes(R.string.buy_pro_restore_successful_message))
    }
  }

  override fun showWaitLoader(proTransactionType: PremiumTransactionType) {
    val contentStringId = when (proTransactionType) {
      PURCHASE -> stringRes(R.string.buy_pro_verifying_purchase_message)
      RESTORE -> stringRes(R.string.buy_pro_verifying_restore_message)
    }
    materialDialog = MaterialDialog.Builder(this)
        .widgetColor(colorRes(R.color.color_accent))
        .contentColor(colorRes(R.color.color_white))
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
    intent.putExtra(PurchaseTransactionConfig.PURCHASE_TAG,
        PurchaseTransactionConfig.PURCHASE_REQUEST_CODE)
    setResult(PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE, intent)
    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    finish()
  }

  override fun isIabReady(): Boolean {
    if (iabHelper?.isSetupDone == true && iabHelper?.isAsyncInProgress != true) {
      return true
    }
    return false
  }

  override fun launchPurchase() {
    iabHelper?.launchPurchaseFlow(this, PurchaseTransactionConfig.ITEM_SKU,
        PurchaseTransactionConfig.VERIFICATION_REQUEST_CODE,
        purchaseFinishedListener)
  }

  override fun launchRestore() {
    iabHelper?.queryInventoryAsync(queryInventoryFinishedListener)
  }

  private fun loadWallrLogo() {
    Glide.with(this)
        .load(R.drawable.ic_wallr)
        .into(proLogo)
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
      add(Triple(R.drawable.ic_remove_ads_white, R.string.buy_pro_features_ads_header,
          R.string.buy_pro_features_ads_sub_header))
      add(Triple(R.drawable.ic_high_definition, R.string.buy_pro_features_downloads_header,
          R.string.buy_pro_features_downloads_sub_header))
      add(Triple(R.drawable.ic_automatic_wallpaper_changer,
          R.string.buy_pro_features_automatic_wallpaper_changer_header,
          R.string.buy_pro_features_automatic_wallpaper_changer_sub_header))
      add(Triple(R.drawable.ic_crystallize_white, R.string.buy_pro_features_crystallize_header,
          R.string.buy_pro_features_crystallize_sub_header))
      add(Triple(R.drawable.ic_share_white, R.string.buy_pro_features_share_header,
          R.string.buy_pro_features_share_sub_header))
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

}

enum class PremiumTransactionType {
  PURCHASE,
  RESTORE
}
