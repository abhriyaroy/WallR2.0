package zebrostudio.wallr100.android.ui.detail.colors

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.github.zagum.expandicon.ExpandIconView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED
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
import zebrostudio.wallr100.android.ui.ImageLoader
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode.MULTIPLE
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode.SINGLE
import zebrostudio.wallr100.android.ui.detail.images.BLUR_RADIUS
import zebrostudio.wallr100.android.ui.detail.images.SLIDING_PANEL_PARALLEL_OFFSET
import zebrostudio.wallr100.android.ui.expandimage.FullScreenImageActivity
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.BITMAP_CACHE
import zebrostudio.wallr100.android.utils.*
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailPresenter
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailView
import zebrostudio.wallr100.presentation.detail.images.ILLEGAL_STATE_EXCEPTION_MESSAGE
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.*
import java.util.*
import javax.inject.Inject

const val COLORS_HEX_VALUE_LIST_INTENT_EXTRA_TAG = "colors_hex_list"
const val COLORS_DETAIL_MODE_INTENT_EXTRA_TAG = "colors_mode"
const val COLORS_DETAIL_MULTIPLE_TYPE_INTENT_EXTRA_TAG = "colors_type"
const val INTENT_IMAGE_TYPE = "image/*"
const val WALLR_DOWNLOAD_LINK = "http://bit.ly/download_wallr"
private const val ALPHA_PARTIALLY_VISIBLE = 0.3f
private const val ALPHA_COMPLETELY_VISIBLE = 1.0f

class ColorsDetailActivity : BaseActivity(), ColorsDetailView {

  @Inject
  internal lateinit var presenter: ColorsDetailPresenter
  @Inject
  internal lateinit var imageLoader: ImageLoader

  private var activityResultIntent: Intent? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_colors_detail)
    AndroidInjection.inject(this)
    presenter.attachView(this)
    attachClickListeners()
    setUpBlurView()
    setUpExpandPanel()
    processIntent()
    presenter.handleViewReadyState()
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
    data.let {
      activityResultIntent = it
    }
    presenter.handleViewResult(requestCode, resultCode)
  }

  override fun getMultiColorImageType(): MultiColorImageType {
    return intent.let {
      if (it.hasExtra(COLORS_DETAIL_MULTIPLE_TYPE_INTENT_EXTRA_TAG)) {
        when (it.getIntExtra(COLORS_DETAIL_MULTIPLE_TYPE_INTENT_EXTRA_TAG,
          MATERIAL.ordinal)) {
          MATERIAL.ordinal -> MATERIAL
          GRADIENT.ordinal -> GRADIENT
          else -> PLASMA
        }
      } else {
        throw IllegalStateException(ILLEGAL_STATE_EXCEPTION_MESSAGE)
      }
    }
  }

  override fun showImageTypeText(text: String) {
    colorStyleNameTextView.text = text
  }

  override fun requestStoragePermission(colorsActionType: ColorsActionType) {
    requestPermissions(this,
      arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), colorsActionType.ordinal)
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
    imageLoader.loadWithCenterCropping(this, bitmap, imageView)
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

  override fun showIndefiniteLoader(message: String) {
    blurView.visible()
    colorActionProgressSpinkit.visible()
    colorActionHintTextView.visible()
    colorActionHintTextView.text = message
  }

  override fun hideIndefiniteLoader() {
    blurView.gone()
    colorActionProgressSpinkit.gone()
    colorActionHintTextView.gone()
  }

  override fun showWallpaperSetErrorMessage() {
    errorToast(stringRes(R.string.set_wallpaper_error_message))
  }

  override fun showWallpaperSetSuccessMessage() {
    successToast(stringRes(R.string.set_wallpaper_success_message))
  }

  override fun showAddToCollectionSuccessMessage() {
    successToast(stringRes(R.string.add_to_collection_success_message))
  }

  override fun collapsePanel() {
    slidingPanel.panelState = COLLAPSED
  }

  override fun disableColorOperations() {
    disableOperations(setColorWallpaperLayout,
      downloadColorLayout,
      editAndSetColorLayout,
      addColorToCollectionLayout,
      shareColorLayout)
  }

  override fun enableColorOperations() {
    enableOperations(setColorWallpaperLayout,
      downloadColorLayout,
      editAndSetColorLayout,
      addColorToCollectionLayout,
      shareColorLayout)
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

  override fun getUriFromResultIntent(): Uri? {
    if (activityResultIntent != null) {
      return UCrop.getOutput(activityResultIntent!!)
    }
    return null
  }

  override fun showGenericErrorMessage() {
    errorToast(stringRes(R.string.generic_error_message))
  }

  override fun showOperationInProgressWaitMessage() {
    infoToast(stringRes(R.string.finalizing_stuff_wait_message), Toast.LENGTH_SHORT)
  }

  override fun showFullScreenImage() {
    startActivity(FullScreenImageActivity.getCallingIntent(this, BITMAP_CACHE))
  }

  override fun showDownloadCompletedSuccessMessage() {
    successToast(stringRes(R.string.download_finished_success_message))
  }

  override fun showAlreadyPresentInCollectionErrorMessage() {
    errorToast(stringRes(R.string.already_present_in_collection_error_message))
  }

  override fun showShareIntent(uri: Uri) {
    val sendIntent = Intent()
    sendIntent.action = ACTION_SEND
    sendIntent.putExtra(EXTRA_STREAM, uri)
    sendIntent.type = INTENT_IMAGE_TYPE
    sendIntent.putExtra(EXTRA_TEXT,
      "${stringRes(R.string.share_intent_message)} $WALLR_DOWNLOAD_LINK \n\n")
    sendIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
    startActivity(createChooser(sendIntent, stringRes(R.string.share_link_using)))
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
    slidingPanel.setParallaxOffset(SLIDING_PANEL_PARALLEL_OFFSET)
    slidingPanel.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
      override fun onPanelSlide(panel: View, slideOffset: Float) {
        // Do nothing
      }

      override fun onPanelStateChanged(
        panel: View,
        previousState: SlidingUpPanelLayout.PanelState,
        newState: SlidingUpPanelLayout.PanelState
      ) {
        if (newState == EXPANDED) {
          expandIconView.setState(ExpandIconView.MORE, true)
          presenter.notifyPanelExpanded()
        } else if (newState == COLLAPSED) {
          expandIconView.setState(ExpandIconView.LESS, true)
          presenter.notifyPanelCollapsed()
        }
      }
    })
  }

  private fun processIntent() {
    intent.let {
      if (it.hasExtra(COLORS_DETAIL_MODE_INTENT_EXTRA_TAG)
          && it.hasExtra(COLORS_HEX_VALUE_LIST_INTENT_EXTRA_TAG)) {
        presenter.setColorsDetailMode(
          if (it.getIntExtra(COLORS_DETAIL_MODE_INTENT_EXTRA_TAG, SINGLE.ordinal)
              == SINGLE.ordinal) {
            SINGLE
          } else {
            MULTIPLE
          })
        presenter.setColorList(it.getStringArrayListExtra(COLORS_HEX_VALUE_LIST_INTENT_EXTRA_TAG))
      } else {
        throw IllegalStateException(ILLEGAL_STATE_EXCEPTION_MESSAGE)
      }
    }
  }

  private fun disableOperations(vararg views: RelativeLayout) {
    for (view in views) {
      view.findViewById<ImageView>(R.id.operationImageView)?.apply {
        alpha = ALPHA_PARTIALLY_VISIBLE
      }
      view.findViewById<TextView>(R.id.operationTextView)?.apply {
        setTextColor(context.colorRes(R.color.dove_gray))
      }
    }
  }

  private fun enableOperations(vararg views: RelativeLayout) {
    for (view in views) {
      view.findViewById<ImageView>(R.id.operationImageView)?.apply {
        alpha = ALPHA_COMPLETELY_VISIBLE
      }
      view.findViewById<TextView>(R.id.operationTextView)?.apply {
        setTextColor(context.colorRes(R.color.white))
      }
    }
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
