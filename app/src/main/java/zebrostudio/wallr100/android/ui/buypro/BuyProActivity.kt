package zebrostudio.wallr100.android.ui.buypro

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.zebrostudio.librarypurchaseflow.IabHelper
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_buy_pro.buyProFeatures
import kotlinx.android.synthetic.main.activity_buy_pro.proLogo
import kotlinx.android.synthetic.main.activity_buy_pro.purchaseButton
import kotlinx.android.synthetic.main.activity_buy_pro.restoreButton
import kotlinx.android.synthetic.main.item_buy_pro_features.view.descriptionTextView
import kotlinx.android.synthetic.main.item_buy_pro_features.view.headerTextView
import kotlinx.android.synthetic.main.item_buy_pro_features.view.imageView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.colorRes
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.infoToast
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.successToast
import zebrostudio.wallr100.presentation.buypro.BuyProContract
import javax.inject.Inject

class BuyProActivity : AppCompatActivity(), BuyProContract.BuyProView {
  @Inject
  internal lateinit var buyProPresenter: BuyProContract.BuyProPresenter

  private lateinit var materialDialog: MaterialDialog
  private var iabHelper: IabHelper? = null

  private val purchaseFinishedListener =
      IabHelper.OnIabPurchaseFinishedListener { result, purchase ->
        if (result.isFailure) {
          showTryRestoringInfo()
          dismissWaitLoader()
        } else {
          buyProPresenter.verifyPurchaseIfSuccessful(purchase.packageName,
              purchase.sku,
              purchase.token,
              ProTransactionType.PURCHASE)
        }
      }

  private val queryInventoryFinishedListener =
      IabHelper.QueryInventoryFinishedListener { result, inv ->
        if (result.isFailure) {
          showGenericVerificationError()
          dismissWaitLoader()
        } else {
          buyProPresenter.verifyPurchaseIfSuccessful(
              inv.getPurchase(PurchaseTransactionDetails.ITEM_SKU).packageName,
              inv.getPurchase(PurchaseTransactionDetails.ITEM_SKU).sku,
              inv.getPurchase(PurchaseTransactionDetails.ITEM_SKU).token,
              ProTransactionType.RESTORE)
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
    setClickListeners()
  }

  override fun onResume() {
    super.onResume()
    if (iabHelper == null || iabHelper?.isSetupDone == false) {
      iabHelper = IabHelper(this, PurchaseTransactionDetails.BASE64_ENCODED_PUBLIC_KEY)
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
    data: Intent
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

  override fun showGenericVerificationError() {
    errorToast(stringRes(R.string.buy_pro_generic_error_message))
  }

  override fun showSuccessfulTransactionMessage(proTransactionType: ProTransactionType) {
    when (proTransactionType) {
      ProTransactionType.PURCHASE -> successToast(
          stringRes(R.string.buy_pro_purchase_successful_message))
      ProTransactionType.RESTORE -> successToast(
          stringRes(R.string.buy_pro_restore_successful_message))
    }
  }

  override fun dismissWaitLoader() {
    materialDialog.dismiss()
  }

  override fun finishWithResult() {
    val intent = Intent()
    intent.putExtra(PurchaseTransactionDetails.PURCHASE_TAG,
        PurchaseTransactionDetails.PURCHASE_REQUEST_CODE)
    setResult(PurchaseTransactionDetails.PURCHASE_SUCCESSFUL_RESULT_CODE, intent)
    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    finish()
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

  private fun setClickListeners() {
    purchaseButton?.setOnClickListener {
      if (iabHelper?.isSetupDone == true && iabHelper?.isAsyncInProgress != true) {
        if (isInternetAvailable()) {
          showWaitLoader(ProTransactionType.PURCHASE)
          iabHelper?.launchPurchaseFlow(this, PurchaseTransactionDetails.ITEM_SKU,
              PurchaseTransactionDetails.VERIFICATION_REQUEST_CODE,
              purchaseFinishedListener)
        } else {
          showNoInternetErrorMessage(ProTransactionType.PURCHASE)
        }
      } else {
        showGenericVerificationError()
      }
    }

    restoreButton?.setOnClickListener {
      if (iabHelper?.isSetupDone == true && iabHelper?.isAsyncInProgress != true) {
        if (isInternetAvailable()) {
          showWaitLoader(ProTransactionType.RESTORE)
          iabHelper?.queryInventoryAsync(queryInventoryFinishedListener)
        } else {
          showNoInternetErrorMessage(ProTransactionType.RESTORE)
        }
      } else {
        showGenericVerificationError()
      }
    }
  }

  private fun isInternetAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager?.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
  }

  private fun showNoInternetErrorMessage(premiumOperationType: ProTransactionType) {
    when (premiumOperationType) {
      ProTransactionType.PURCHASE -> errorToast(
          stringRes(R.string.buy_pro_purchase_ensure_working_internet_connection_message))
      ProTransactionType.RESTORE -> errorToast(
          stringRes(R.string.buy_pro_restore_ensure_working_internet_connection_message))
    }
  }

  private fun showWaitLoader(proTransactionType: ProTransactionType) {
    val contentStringId = when (proTransactionType) {
      ProTransactionType.PURCHASE -> stringRes(R.string.buy_pro_verifying_purchase_message)
      ProTransactionType.RESTORE -> stringRes(R.string.buy_pro_verifying_restore_message)
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

  private fun showTryRestoringInfo() {
    infoToast(stringRes(R.string.buy_pro_try_restoring_message))
  }

  companion object {
    enum class ProTransactionType {
      PURCHASE,
      RESTORE
    }
  }

}
