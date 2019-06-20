package zebrostudio.wallr100.android.ui.detail.images

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy.ALL
import com.bumptech.glide.load.engine.DiskCacheStrategy.NONE
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.github.zagum.expandicon.ExpandIconView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.yalantis.ucrop.UCrop
import dagger.android.AndroidInjection
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.activity_detail.*
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.ui.ImageLoader
import zebrostudio.wallr100.android.ui.LoaderListener
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.expandimage.FullScreenImageActivity
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.BITMAP_CACHE
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.CRYSTALLIZED_BITMAP_CACHE
import zebrostudio.wallr100.android.utils.*
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.SEARCH
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.WALLPAPERS
import zebrostudio.wallr100.presentation.detail.images.ActionType
import zebrostudio.wallr100.presentation.detail.images.DetailContract.DetailPresenter
import zebrostudio.wallr100.presentation.detail.images.DetailContract.DetailView
import zebrostudio.wallr100.presentation.detail.images.ILLEGAL_STATE_EXCEPTION_MESSAGE
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity
import javax.inject.Inject

const val SLIDING_PANEL_PARALLEL_OFFSET = 40
const val INITIAL_LOADER_PROGRESS_VALUE = 0
const val INITIAL_LOADER_PROGRESS_PERCENTAGE = "0%"
const val BLUR_RADIUS: Float = 8F
const val INITIAL_SELECTED_DOWNLOAD_OPTION = 0

class DetailActivity : BaseActivity(), DetailView {

  @Inject
  internal lateinit var presenter: DetailPresenter
  @Inject
  internal lateinit var imageLoader: ImageLoader
  private var activityResultIntent: Intent? = null
  private var materialProgressLoader: MaterialDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_detail)
    presenter.attachView(this)
    intent.let {
      if (it.hasExtra(IMAGE_TYPE_TAG)) {
        presenter.setImageType(it.getIntExtra(IMAGE_TYPE_TAG, WALLPAPERS.ordinal))
      } else {
        throw IllegalStateException(ILLEGAL_STATE_EXCEPTION_MESSAGE)
      }
    }
    setUpExpandPanel()
    attachClickListeners()
    setUpBlurView()
  }

  override fun onRequestPermissionsResult(requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray) {
    presenter.handlePermissionRequestResult(requestCode, permissions, grantResults)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (data != null) {
      activityResultIntent = intent
    }
    presenter.handleViewResult(requestCode, resultCode)
  }

  override fun onBackPressed() {
    presenter.handleBackButtonClick()
  }

  override fun getWallpaperImageDetails(): ImagePresenterEntity {
    return intent.let {
      if (it.hasExtra(IMAGE_DETAILS_TAG)) {
        it.getSerializableExtra(IMAGE_DETAILS_TAG) as ImagePresenterEntity
      } else {
        throw IllegalStateException(ILLEGAL_STATE_EXCEPTION_MESSAGE)
      }
    }
  }

  override fun getSearchImageDetails(): SearchPicturesPresenterEntity {
    return intent.let {
      if (it.hasExtra(IMAGE_DETAILS_TAG)) {
        it.getSerializableExtra(IMAGE_DETAILS_TAG) as SearchPicturesPresenterEntity
      } else {
        throw IllegalStateException(ILLEGAL_STATE_EXCEPTION_MESSAGE)
      }
    }
  }

  override fun showAuthorDetails(name: String, profileImageLink: String) {
    authorName.text = name
    imageLoader.loadWithPlaceHolder(this, profileImageLink, authorImage, R.drawable.ic_user_white)
  }

  override fun showImage(lowQualityLink: String, highQualityLink: String) {
    imageLoader.loadWithThumbnail(this, highQualityLink, lowQualityLink, imageView, ALL,
      object : LoaderListener {
        override fun onResourceReady(resource: Drawable?,
          model: Any?,
          target: Target<Drawable>?,
          dataSource: DataSource?,
          isFirstResource: Boolean): Boolean {
          return false
        }

        override fun onLoadFailed(e: GlideException?,
          model: Any?,
          target: Target<Drawable>?,
          isFirstResource: Boolean): Boolean {
          presenter.handleHighQualityImageLoadFailed()
          return false
        }
      })
  }

  override fun showImage(bitmap: Bitmap) {
    imageLoader.loadWithCenterCropping(this, bitmap, imageView, NONE)
  }

  override fun showImageLoadError() {
    errorToast(stringRes(R.string.unable_to_load_hd_image_error))
  }

  override fun showNoInternetError() {
    errorToast(stringRes(R.string.no_internet_message))
  }

  override fun requestStoragePermission(actionType: ActionType) {
    requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE), actionType.ordinal)
  }

  override fun showPermissionRequiredMessage() {
    errorToast(stringRes(R.string.storage_permission_denied_error))
  }

  override fun showNoInternetToShareError() {
    errorToast(stringRes(R.string.detail_activity_share_error))
  }

  override fun showUnsuccessfulPurchaseError() {
    errorToast(stringRes(R.string.unsuccessful_purchase_error))
  }

  override fun shareLink(intentExtra: String, intentType: String) {
    Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, intentExtra)
      type = intentType
    }.let {
      startActivity(Intent.createChooser(it, stringRes(R.string.share_link_using)))
    }
  }

  override fun showWaitLoader(message: String) {
    materialProgressLoader = MaterialDialog.Builder(this).widgetColor(colorRes(R.color.accent))
        .contentColor(colorRes(R.color.white)).content(message)
        .backgroundColor(colorRes(R.color.primary)).progress(true, INITIAL_LOADER_PROGRESS_VALUE)
        .progressIndeterminateStyle(false).build()
    materialProgressLoader?.show()
  }

  override fun hideWaitLoader() {
    materialProgressLoader?.dismiss()
  }

  override fun redirectToBuyPro(requestCode: Int) {
    startActivityForResult(Intent(this, BuyProActivity::class.java), requestCode)
  }

  override fun showGenericErrorMessage() {
    errorToast(stringRes(R.string.generic_error_message))
  }

  override fun blurScreen() {
    blurView.visible()
    wallpaperDownloadProgressPercentage.gone()
    loadingHintBelowProgressPercentage.gone()
    wallpaperActionProgressSpinkit.gone()
    loadingHintBelowProgressSpinkit.gone()
  }

  override fun blurScreenAndInitializeProgressPercentage() {
    blurView.visible()
    wallpaperDownloadProgressPercentage.text = INITIAL_LOADER_PROGRESS_PERCENTAGE
    wallpaperDownloadProgressPercentage.visible()
    loadingHintBelowProgressPercentage.text =
        stringRes(R.string.detail_activity_grabbing_best_quality_wallpaper_message)
    loadingHintBelowProgressPercentage.visible()
  }

  override fun hideScreenBlur() {
    blurView.gone()
  }

  override fun showIndefiniteLoader(message: String) {
    wallpaperActionProgressSpinkit.visible()
    loadingHintBelowProgressSpinkit.text = message
    loadingHintBelowProgressSpinkit.visible()
  }

  override fun showIndefiniteLoaderWithAnimation(message: String) {
    val exitAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_out)
    exitAnimation.fillAfter = true
    val entryAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade_in)
    entryAnimation.fillAfter = true
    exitAnimation.setAnimationListener(object : Animation.AnimationListener {
      override fun onAnimationRepeat(animation: Animation?) {
        // Do Nothing
      }

      override fun onAnimationEnd(animation: Animation?) {
        loadingHintBelowProgressSpinkit.text = message
        wallpaperActionProgressSpinkit.startAnimation(entryAnimation)
        loadingHintBelowProgressSpinkit.startAnimation(entryAnimation)
        wallpaperActionProgressSpinkit.visible()
        loadingHintBelowProgressSpinkit.visible()
      }

      override fun onAnimationStart(animation: Animation?) {
        // Do Nothing
      }

    })
    wallpaperDownloadProgressPercentage.startAnimation(exitAnimation)
    loadingHintBelowProgressPercentage.startAnimation(exitAnimation)
  }

  override fun hideIndefiniteLoader() {
    wallpaperActionProgressSpinkit.gone()
    loadingHintBelowProgressSpinkit.text = ""
    loadingHintBelowProgressSpinkit.gone()
  }

  override fun getUriFromResultIntent(): Uri? {
    if (activityResultIntent != null) {
      return UCrop.getOutput(activityResultIntent!!)
    }
    return null
  }

  override fun showUnableToDownloadErrorMessage() {
    errorToast(stringRes(R.string.detail_activity_fetch_wallpaper_error_message))
  }

  override fun showWallpaperSetErrorMessage() {
    errorToast(stringRes(R.string.set_wallpaper_error_message))
  }

  override fun showWallpaperSetSuccessMessage() {
    successToast(stringRes(R.string.set_wallpaper_success_message))
  }

  override fun updateProgressPercentage(progress: String) {
    wallpaperDownloadProgressPercentage.text = progress
  }

  override fun showWallpaperOperationInProgressWaitMessage() {
    infoToast(stringRes(R.string.finalizing_stuff_wait_message), Toast.LENGTH_SHORT)
  }

  override fun showDownloadWallpaperCancelledMessage() {
    infoToast(stringRes(R.string.detail_activity_wallpaper_download_cancelled_message))
  }

  override fun startCroppingActivity(source: Uri,
    destination: Uri,
    minimumWidth: Int,
    minimumHeight: Int) {
    UCrop.of(source, destination).withMaxResultSize(minimumWidth, minimumHeight)
        .useSourceImageAspectRatio().start(this)
  }

  override fun showSearchTypeDownloadDialog(showCrystallizedOption: Boolean) {
    val optionsArray: Int = if (showCrystallizedOption) {
      R.array.imageDownloadQualitiesWithCrystallized
    } else {
      R.array.imageDownloadQualities
    }
    MaterialDialog.Builder(this).backgroundColor(colorRes(R.color.primary))
        .title(R.string.detail_activity_choose_download_quality_message).items(optionsArray)
        .contentColor(colorRes(R.color.white)).widgetColor(colorRes(R.color.accent))
        .positiveColor(colorRes(R.color.accent)).negativeColor(colorRes(R.color.accent))
        .itemsCallbackSingleChoice(INITIAL_SELECTED_DOWNLOAD_OPTION) { _, _, which, _ ->
          presenter.handleDownloadQualitySelectionEvent(SEARCH, which)
          true
        }.positiveText(R.string.detail_activity_options_download).negativeText(R.string.cancel_text)
        .show()
  }

  override fun showWallpaperTypeDownloadDialog(showCrystallizedOption: Boolean) {
    val optionsArray: Int = if (showCrystallizedOption) {
      R.array.imageDownloadQualitiesWithCrystallized
    } else {
      R.array.imageDownloadQualities
    }
    MaterialDialog.Builder(this).backgroundColor(colorRes(R.color.primary))
        .title(R.string.detail_activity_choose_download_quality_message).items(optionsArray)
        .contentColor(colorRes(R.color.white)).widgetColor(colorRes(R.color.accent))
        .positiveColor(colorRes(R.color.accent)).negativeColor(colorRes(R.color.accent))
        .itemsCallbackSingleChoice(INITIAL_SELECTED_DOWNLOAD_OPTION) { _, _, which, _ ->
          presenter.handleDownloadQualitySelectionEvent(WALLPAPERS, which)
          true
        }.positiveText(R.string.detail_activity_options_download).negativeText(R.string.cancel_text)
        .show()
  }

  override fun showDownloadStartedMessage() {
    infoToast(stringRes(R.string.detail_activity_download_started_message))
  }

  override fun showDownloadAlreadyInProgressMessage() {
    errorToast(stringRes(R.string.detail_activity_download_already_in_progress_message))
  }

  override fun showDownloadCompletedSuccessMessage() {
    successToast(stringRes(R.string.download_finished_success_message))
  }

  override fun showCrystallizedDownloadCompletedSuccessMessage() {
    successToast(stringRes(R.string.detail_activity_crystallized_download_finished_message))
  }

  override fun showCrystallizeDescriptionDialog() {
    MaterialDialog.Builder(this).backgroundColor(colorRes(R.color.primary))
        .customView(R.layout.dialog_crystallize_example, false)
        .contentColor(colorRes(R.color.white))
        .widgetColor(colorRes(R.color.accent)).positiveColor(colorRes(R.color.accent))
        .negativeColor(colorRes(R.color.accent))
        .positiveText(stringRes(R.string.detail_activity_crystallize_dialog_positive_text))
        .negativeText(stringRes(R.string.detail_activity_crystallize_dialog_negative_text))
        .onPositive { _, _ ->
          presenter.handleCrystallizeDialogPositiveClick()
        }.show()
  }

  override fun showCrystallizeSuccessMessage() {
    successToast(stringRes(R.string.crystallizing_wallpaper_successful_message))
  }

  override fun showImageHasAlreadyBeenCrystallizedMessage() {
    infoToast(stringRes(R.string.detail_activity_image_already_crystallized_message))
  }

  override fun showAddToCollectionSuccessMessage() {
    successToast(stringRes(R.string.add_to_collection_success_message))
  }

  override fun showAlreadyPresentInCollectionErrorMessage() {
    errorToast(stringRes(R.string.already_present_in_collection_error_message))
  }

  override fun showExpandedImage(lowQualityLink: String, highQualityLink: String) {
    startActivity(FullScreenImageActivity.getCallingIntent(this, lowQualityLink, highQualityLink))
  }

  override fun showCrystallizedExpandedImage() {
    startActivity(FullScreenImageActivity.getCallingIntent(this, CRYSTALLIZED_BITMAP_CACHE))
  }

  override fun showEditedExpandedImage() {
    startActivity(FullScreenImageActivity.getCallingIntent(this, BITMAP_CACHE))
  }

  override fun collapseSlidingPanel() {
    slidingPanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
  }

  override fun exitView() {
    overridePendingTransition(R.anim.no_change, R.anim.slide_to_right)
    this.finish()
  }

  private fun setUpExpandPanel() {
    expandIconView.setState(ExpandIconView.LESS, false)
    slidingPanel.setParallaxOffset(SLIDING_PANEL_PARALLEL_OFFSET)
    slidingPanel.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
      override fun onPanelSlide(panel: View, slideOffset: Float) {
        // Do nothing
      }

      override fun onPanelStateChanged(panel: View,
        previousState: SlidingUpPanelLayout.PanelState,
        newState: SlidingUpPanelLayout.PanelState) {
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

  private fun attachClickListeners() {
    setWallpaperImageLayout.setOnClickListener { presenter.handleQuickSetClick() }
    downloadImageLayout.setOnClickListener { presenter.handleDownloadClick() }
    crystallizeImageLayout.setOnClickListener { presenter.handleCrystallizeClick() }
    editAndSetImageLayout.setOnClickListener { presenter.handleEditSetClick() }
    addToCollectionImageLayout.setOnClickListener { presenter.handleAddToCollectionClick() }
    shareImageLayout.setOnClickListener { presenter.handleShareClick() }
    backIcon.setOnClickListener { presenter.handleBackButtonClick() }
    imageView.setOnClickListener { presenter.handleImageViewClicked() }
  }

  private fun setUpBlurView() {
    blurView.setupWith(parentFrameLayout).setBlurAlgorithm(RenderScriptBlur(this))
        .setBlurRadius(BLUR_RADIUS)
  }

  companion object {
    const val IMAGE_DETAILS_TAG = "ImageDetails"
    const val IMAGE_TYPE_TAG = "ImageType"

    fun getCallingIntent(context: Context,
      searchPicturesPresenterEntity: SearchPicturesPresenterEntity): Intent {
      return Intent(context, DetailActivity::class.java).apply {
        putExtras(Bundle().apply {
          putInt(IMAGE_TYPE_TAG, SEARCH.ordinal)
          putExtra(IMAGE_DETAILS_TAG, searchPicturesPresenterEntity)
        })
      }
    }

    fun getCallingIntent(context: Context, imagePresenterEntity: ImagePresenterEntity): Intent {
      return Intent(context, DetailActivity::class.java).apply {
        putExtras(Bundle().apply {
          putInt(IMAGE_TYPE_TAG, WALLPAPERS.ordinal)
          putExtra(IMAGE_DETAILS_TAG, imagePresenterEntity)
        })
      }
    }
  }

}