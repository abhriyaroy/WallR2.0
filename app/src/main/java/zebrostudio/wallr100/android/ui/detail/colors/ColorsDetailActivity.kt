package zebrostudio.wallr100.android.ui.detail.colors

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.github.zagum.expandicon.ExpandIconView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.yalantis.ucrop.UCrop
import dagger.android.AndroidInjection
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.activity_colors_detail.addColorToCollectionLayout
import kotlinx.android.synthetic.main.activity_colors_detail.backIcon
import kotlinx.android.synthetic.main.activity_colors_detail.blurView
import kotlinx.android.synthetic.main.activity_colors_detail.colorActionHintTextView
import kotlinx.android.synthetic.main.activity_colors_detail.colorActionProgressSpinkit
import kotlinx.android.synthetic.main.activity_colors_detail.colorStyleNameTextView
import kotlinx.android.synthetic.main.activity_colors_detail.downloadColorLayout
import kotlinx.android.synthetic.main.activity_colors_detail.editAndSetColorLayout
import kotlinx.android.synthetic.main.activity_colors_detail.expandIconView
import kotlinx.android.synthetic.main.activity_colors_detail.imageView
import kotlinx.android.synthetic.main.activity_colors_detail.setColorWallpaperLayout
import kotlinx.android.synthetic.main.activity_colors_detail.shareColorLayout
import kotlinx.android.synthetic.main.activity_colors_detail.slidingPanel
import kotlinx.android.synthetic.main.activity_colors_detail.spinkitView
import kotlinx.android.synthetic.main.activity_detail.parentFrameLayout
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.detail.images.BLUR_RADIUS
import zebrostudio.wallr100.android.ui.detail.images.SLIDING_PANEL_PARALLEL_OFFSET
import zebrostudio.wallr100.android.ui.expandimage.FullScreenImageActivity
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.EDITED_BITMAP_CACHE
import zebrostudio.wallr100.android.utils.disable
import zebrostudio.wallr100.android.utils.enable
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.infoToast
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.successToast
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
const val INTENT_IMAGE_TYPE = "image/*"

class ColorsDetailActivity : BaseActivity(), ColorsDetailView {

  @Inject lateinit var presenter: ColorsDetailPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_colors_detail)
    AndroidInjection.inject(this)
    presenter.attachView(this)
    attachClickListeners()
    setUpBlurView()
    setUpExpandPanel()
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

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    presenter.handleViewResult(requestCode, resultCode, data)
  }

  override fun showImageTypeText(text: String) {
    colorStyleNameTextView.text = text
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
    errorToast(stringRes(R.string.storage_permission_denied_error))
  }

  override fun redirectToBuyPro(requestCode: Int) {
    startActivityForResult(Intent(this, BuyProActivity::class.java), requestCode)
  }

  override fun showUnsuccessfulPurchaseError() {
    errorToast(stringRes(R.string.unsuccessful_purchase_error))
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

  override fun showMainImageWaitLoader() {
    spinkitView.visible()
  }

  override fun hideMainImageWaitLoader() {
    spinkitView.gone()
  }

  override fun showNotEnoughFreeSpaceErrorMessage() {
    errorToast(stringRes(R.string.not_enough_free_space_error_message))
  }

  override fun showImageLoadError() {
    errorToast(stringRes(R.string.colors_detail_activity_image_load_error_message))
  }

  override fun showNoInternetError() {
    errorToast(stringRes(R.string.no_internet_message))
  }

  override fun showIndefiniteWaitLoader(message: String) {
    blurView.visible()
    colorActionProgressSpinkit.visible()
    colorActionHintTextView.visible()
    colorActionHintTextView.text = message
  }

  override fun hideIndefiniteWaitLoader() {
    blurView.gone()
    colorActionProgressSpinkit.gone()
    colorActionHintTextView.gone()
  }

  override fun showWallpaperSetErrorMessage() {
    errorToast(stringRes(R.string.detail_activity_set_wallpaper_error_message))
  }

  override fun showWallpaperSetSuccessMessage() {
    successToast(stringRes(R.string.detail_activity_set_wallpaper_success_message))
  }

  override fun showAddToCollectionSuccessMessage() {
    successToast(stringRes(R.string.add_to_collection_success_message))
  }

  override fun collapsePanel() {
    slidingPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
  }

  override fun disableColorOperations() {
    setColorWallpaperLayout.disable(this)

    downloadColorLayout.disable(this)

    editAndSetColorLayout.disable(this)

    addColorToCollectionLayout.disable(this)

    shareColorLayout.disable(this)
  }

  override fun enableColorOperations() {
    setColorWallpaperLayout.enable(this)

    downloadColorLayout.enable(this)

    editAndSetColorLayout.enable(this)

    addColorToCollectionLayout.enable(this)

    shareColorLayout.enable(this)
  }

  override fun showColorOperationsDisabledMessage() {
    infoToast(stringRes(R.string.colors_detail_activity_color_operations_disabled_message))
  }

  override fun startCroppingActivity(
    source: Uri,
    destination: Uri,
    minimumWidth: Int,
    minimumHeight: Int
  ) {
    UCrop.of(source, destination)
        .withMaxResultSize(minimumWidth, minimumHeight)
        .useSourceImageAspectRatio()
        .start(this)
  }

  override fun getUriFromIntent(data: Intent): Uri? {
    return UCrop.getOutput(data)
  }

  override fun showGenericErrorMessage() {
    errorToast(stringRes(R.string.generic_error_message))
  }

  override fun showOperationInProgressWaitMessage() {
    infoToast(stringRes(R.string.finalizing_stuff_wait_message), Toast.LENGTH_SHORT)
  }

  override fun showFullScreenImage() {
    startActivity(FullScreenImageActivity.getCallingIntent(this, EDITED_BITMAP_CACHE))
  }

  override fun showDownloadCompletedSuccessMessage() {
    successToast(stringRes(R.string.download_finished_success_message))
  }

  override fun showAlreadyPresentInCollectionErrorMessage() {
    errorToast(stringRes(R.string.already_present_in_collection_error_message))
  }

  override fun showShareIntent(uri: Uri) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
    sendIntent.type = INTENT_IMAGE_TYPE
    sendIntent.putExtra(Intent.EXTRA_TEXT, stringRes(R.string.share_intent_message) + "\n\n")
    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    startActivity(Intent.createChooser(sendIntent, stringRes(R.string.share_link_using)))
  }

  override fun exitView() {
    overridePendingTransition(R.anim.no_change, R.anim.slide_to_right)
    finish()
  }

  private fun attachClickListeners() {
    setColorWallpaperLayout.setOnClickListener {
      presenter.handleQuickSetClick()
    }

    downloadColorLayout.setOnClickListener {
      presenter.handleDownloadClick()
    }

    editAndSetColorLayout.setOnClickListener {
      presenter.handleEditSetClick()
    }

    addColorToCollectionLayout.setOnClickListener {
      presenter.handleAddToCollectionClick()
    }

    shareColorLayout.setOnClickListener {
      presenter.handleShareClick()
    }

    imageView.setOnClickListener {
      presenter.handleImageViewClicked()
    }

    backIcon.setOnClickListener {
      presenter.handleBackButtonClick()
    }
  }

  private fun setUpBlurView() {
    blurView.setupWith(parentFrameLayout).setBlurAlgorithm(RenderScriptBlur(this))
        .setBlurRadius(BLUR_RADIUS)
  }

  private fun setUpExpandPanel() {
    expandIconView.setState(ExpandIconView.LESS, false)
    slidingPanel.setParallaxOffset(
        SLIDING_PANEL_PARALLEL_OFFSET)
    slidingPanel.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
      override fun onPanelSlide(panel: View, slideOffset: Float) {
        // Do nothing
      }

      override fun onPanelStateChanged(
        panel: View,
        previousState: SlidingUpPanelLayout.PanelState,
        newState: SlidingUpPanelLayout.PanelState
      ) {
        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
          expandIconView.setState(ExpandIconView.MORE, true)
          presenter.setPanelStateAsExpanded()
        } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
          expandIconView.setState(ExpandIconView.LESS, true)
          presenter.setPanelStateAsCollapsed()
        }
      }
    })
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
