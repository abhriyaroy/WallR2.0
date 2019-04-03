package zebrostudio.wallr100.android.ui.detail.colors

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_colors_detail.spinkitView
import kotlinx.android.synthetic.main.activity_detail.imageView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.ui.detail.images.ILLEGAL_STATE_EXCEPTION_MESSAGE
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailPresenter
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailView
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType
import java.util.ArrayList
import javax.inject.Inject

const val COLORS_HEX_VALUE_LIST_INTENT_EXTRA_TAG = "colors_hex_list"
const val COLORS_DETAIL_MODE_INTENT_EXTRA_TAG = "colors_mode"
const val COLORS_DETAIL_MULTIPLE_TYPE_INTENT_EXTRA_TAG = "colors_type"

class ColorsDetailActivity : BaseActivity(), ColorsDetailView {

  @Inject lateinit var presenter: ColorsDetailPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_colors_detail)
    AndroidInjection.inject(this)
    presenter.attachView(this)
    presenter.setCalledIntent(intent)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  override fun onBackPressed() {
    presenter.handleBackButtonClick()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>, grantResults: IntArray
  ) {
    presenter.handlePermissionRequestResult(requestCode, permissions, grantResults)
  }

  override fun throwIllegalStateException() {
    throw IllegalStateException(
        ILLEGAL_STATE_EXCEPTION_MESSAGE)
  }

  override fun exitView() {
    overridePendingTransition(R.anim.no_change, R.anim.slide_to_right)
    finish()
  }

  override fun hasStoragePermission(): Boolean {
    val readPermission = ContextCompat.checkSelfPermission(this,
        Manifest.permission.READ_EXTERNAL_STORAGE)
    val writePermission = ContextCompat.checkSelfPermission(this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    if (readPermission != PackageManager.PERMISSION_GRANTED
        || writePermission != PackageManager.PERMISSION_GRANTED) {
      return false
    }
    return true
  }

  override fun requestStoragePermission(colorsActionType: ColorsActionType) {
    requestPermissions(this,
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE), colorsActionType.ordinal)
  }

  override fun showPermissionRequiredMessage() {
    errorToast(getString(R.string.storage_permission_denied_error))
  }

  override fun showImageLoadError() {
    errorToast(stringRes(R.string.colors_detail_activity_image_load_error_message))
  }

  override fun showNoInternetToShareError() {

  }

  override fun showUnsuccessfulPurchaseError() {
    errorToast(getString(R.string.unsuccessful_purchase_error))
  }

  override fun showMainImageWaitLoader() {
    spinkitView.visible()
  }

  override fun showImage(bitmap: Bitmap) {
    val imageOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .centerCrop()
    Glide.with(this)
        .load(bitmap)
        .apply(imageOptions)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(imageView)
  }

  override fun hideMainImageWaitLoader() {
    spinkitView.gone()
  }

  companion object {

    fun getCallingIntent(
      context: Context,
      hexValueList: List<String>,
      colorsDetailMode: ColorsDetailMode,
      multiColorImageType: MultiColorImageType? = null
    ): Intent {
      return Intent(context, ColorsDetailActivity::class.java).apply {
        putStringArrayListExtra(COLORS_HEX_VALUE_LIST_INTENT_EXTRA_TAG,
            ArrayList(hexValueList))
        putExtra(COLORS_DETAIL_MODE_INTENT_EXTRA_TAG, colorsDetailMode.ordinal)
        multiColorImageType?.let {
          putExtra(COLORS_DETAIL_MULTIPLE_TYPE_INTENT_EXTRA_TAG, it.ordinal)
        }
      }
    }
  }
}

enum class ColorsDetailMode {
  SINGLE,
  MULTIPLE
}
