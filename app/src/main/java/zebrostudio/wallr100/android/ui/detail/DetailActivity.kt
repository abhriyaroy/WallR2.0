package zebrostudio.wallr100.android.ui.detail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.zagum.expandicon.ExpandIconView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.yalantis.ucrop.UCrop
import dagger.android.AndroidInjection
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.activity_detail.addToCollectionImageLayout
import kotlinx.android.synthetic.main.activity_detail.authorImage
import kotlinx.android.synthetic.main.activity_detail.authorName
import kotlinx.android.synthetic.main.activity_detail.backIcon
import kotlinx.android.synthetic.main.activity_detail.blurView
import kotlinx.android.synthetic.main.activity_detail.crystallizeImageLayout
import kotlinx.android.synthetic.main.activity_detail.downloadImageLayout
import kotlinx.android.synthetic.main.activity_detail.editAndSetImageLayout
import kotlinx.android.synthetic.main.activity_detail.expandIconView
import kotlinx.android.synthetic.main.activity_detail.imageView
import kotlinx.android.synthetic.main.activity_detail.loadingHintBelowProgressPercentage
import kotlinx.android.synthetic.main.activity_detail.loadingHintBelowProgressSpinkit
import kotlinx.android.synthetic.main.activity_detail.parentFrameLayout
import kotlinx.android.synthetic.main.activity_detail.setWallpaperImageLayout
import kotlinx.android.synthetic.main.activity_detail.shareImageLayout
import kotlinx.android.synthetic.main.activity_detail.slidingPanel
import kotlinx.android.synthetic.main.activity_detail.wallpaperActionProgressSpinkit
import kotlinx.android.synthetic.main.activity_detail.wallpaperDownloadProgressPercentage
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.expandimage.FullScreenImageActivity
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.CRYSTALLIZED_BITMAP_CACHE
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.EDITED_BITMAP_CACHE
import zebrostudio.wallr100.android.utils.colorRes
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.infoToast
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.successToast
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.SEARCH
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.WALLPAPERS
import zebrostudio.wallr100.presentation.detail.ActionType
import zebrostudio.wallr100.presentation.detail.DetailContract.DetailPresenter
import zebrostudio.wallr100.presentation.detail.DetailContract.DetailView
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity
import javax.inject.Inject

const val SLIDING_PANEL_PARALLEL_OFFSET = 40
const val INITIAL_LOADER_PROGRESS_VALUE = 0
const val INITIAL_LOADER_PROGRESS_PERCENTAGE = "0%"
const val BLUR_RADIUS: Float = 8F
const val INITIAL_SELECTED_DOWNLOAD_OPTION = 0
const val ILLEGAL_STATE_EXCEPTION_MESSAGE = "Activity is not invoked using getCallingIntent method"

class DetailActivity : BaseActivity(), DetailView {

  @Inject lateinit var presenter: DetailPresenter

  private var materialProgressLoader: MaterialDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_detail)
    presenter.attachView(this)
    presenter.setCalledIntent(intent)
    setUpExpandPanel()
    attachClickListeners()
    setUpBlurView()
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

  override fun onBackPressed() {
    presenter.handleBackButtonClick()
  }

  override fun throwIllegalStateException() {
    throw IllegalStateException(ILLEGAL_STATE_EXCEPTION_MESSAGE)
  }

  override fun getWallpaperImageDetails(): ImagePresenterEntity {
    return intent.extras!!.getSerializable(IMAGE_DETAILS_TAG) as ImagePresenterEntity
  }

  override fun getSearchImageDetails(): SearchPicturesPresenterEntity {
    return intent.extras!!.getSerializable(IMAGE_DETAILS_TAG) as SearchPicturesPresenterEntity
  }

  override fun showAuthorDetails(name: String, profileImageLink: String) {
    authorName.text = name
    val options = RequestOptions()
        .placeholder(R.drawable.ic_user_white)
        .dontAnimate()
    Glide.with(this)
        .load(profileImageLink)
        .apply(options)
        .into(authorImage)
  }

  override fun showImage(lowQualityLink: String, highQualityLink: String) {
    val placeHolderOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
    val mainImageOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
    Glide.with(this)
        .load(highQualityLink)
        .thumbnail(Glide.with(this)
            .load(lowQualityLink)
            .apply(placeHolderOptions))
        .apply(mainImageOptions)
        .listener(object : RequestListener<Drawable> {
          override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
          ): Boolean {
            presenter.handleHighQualityImageLoadFailed()
            return false
          }

          override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
          ): Boolean {
            return false
          }
        })
        .into(imageView)
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

  override fun showImageLoadError() {
    errorToast(stringRes(R.string.unable_to_load_hd_image_error))
  }

  override fun showNoInternetError() {
    errorToast(stringRes(R.string.no_internet_message))
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

  override fun requestStoragePermission(actionType: ActionType) {
    requestPermissions(this,
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE), actionType.ordinal)
  }

  override fun showPermissionRequiredMessage() {
    errorToast(getString(R.string.detail_activity_storage_permission_denied_error))
  }

  override fun showNoInternetToShareError() {
    errorToast(getString(R.string.detail_activity_share_error))
  }

  override fun showUnsuccessfulPurchaseError() {
    errorToast(getString(R.string.detail_activity_unsuccessful_purchase_error))
  }

  override fun shareLink(shortLink: String) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_intent_message) +
        "\n\n" + shortLink)
    sendIntent.type = "text/plain"
    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_link_using)))
  }

  override fun showWaitLoader(message: String) {
    materialProgressLoader = MaterialDialog.Builder(this)
        .widgetColor(colorRes(R.color.accent))
        .contentColor(colorRes(R.color.white))
        .content(message)
        .backgroundColor(colorRes(R.color.primary))
        .progress(true, INITIAL_LOADER_PROGRESS_VALUE)
        .progressIndeterminateStyle(false)
        .build()
    materialProgressLoader?.show()
  }

  override fun hideWaitLoader() {
    materialProgressLoader?.dismiss()
  }

  override fun redirectToBuyPro(requestCode: Int) {
    startActivityForResult(Intent(this, BuyProActivity::class.java), requestCode)
  }

  override fun showGenericErrorMessage() {
    errorToast(getString(R.string.generic_error_message))
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
        getString(R.string.detail_activity_grabbing_best_quality_wallpaper_message)
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

  override fun getUriFromIntent(data: Intent): Uri? {
    return UCrop.getOutput(data)
  }

  override fun showUnableToDownloadErrorMessage() {
    errorToast(getString(R.string.detail_activity_fetch_wallpaper_error_message))
  }

  override fun showWallpaperSetErrorMessage() {
    errorToast(getString(R.string.detail_activity_set_wallpaper_error_message))
  }

  override fun showWallpaperSetSuccessMessage() {
    successToast(getString(R.string.detail_activity_set_wallpaper_success_message))
  }

  override fun updateProgressPercentage(progress: String) {
    wallpaperDownloadProgressPercentage.text = progress
  }

  override fun showWallpaperOperationInProgressWaitMessage() {
    infoToast(getString(R.string.detail_activity_finalizing_stuff_wait_message), Toast.LENGTH_SHORT)
  }

  override fun showDownloadWallpaperCancelledMessage() {
    infoToast(getString(R.string.detail_activity_wallpaper_download_cancelled_message))
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

  override fun showSearchTypeDownloadDialog(showCrystallizedOption: Boolean) {
    val optionsArray: Int = if (showCrystallizedOption) {
      R.array.imageDownloadQualitiesWithCrystallized
    } else {
      R.array.imageDownloadQualities
    }
    MaterialDialog.Builder(this)
        .backgroundColor(colorRes(R.color.primary))
        .title(R.string.detail_activity_choose_download_quality_message)
        .items(optionsArray)
        .contentColor(colorRes(R.color.white))
        .widgetColor(colorRes(R.color.accent))
        .positiveColor(colorRes(R.color.accent))
        .negativeColor(colorRes(R.color.accent))
        .itemsCallbackSingleChoice(INITIAL_SELECTED_DOWNLOAD_OPTION
        ) { _, _, which, _ ->
          presenter.handleDownloadQualitySelectionEvent(SEARCH, which)
          true
        }
        .positiveText(R.string.detail_activity_options_download)
        .negativeText(R.string.cancel_text)
        .show()
  }

  override fun showWallpaperTypeDownloadDialog(showCrystallizedOption: Boolean) {
    val optionsArray: Int = if (showCrystallizedOption) {
      R.array.imageDownloadQualitiesWithCrystallized
    } else {
      R.array.imageDownloadQualities
    }
    MaterialDialog.Builder(this)
        .backgroundColor(colorRes(R.color.primary))
        .title(R.string.detail_activity_choose_download_quality_message)
        .items(optionsArray)
        .contentColor(colorRes(R.color.white))
        .widgetColor(colorRes(R.color.accent))
        .positiveColor(colorRes(R.color.accent))
        .negativeColor(colorRes(R.color.accent))
        .itemsCallbackSingleChoice(INITIAL_SELECTED_DOWNLOAD_OPTION
        ) { _, _, which, _ ->
          presenter.handleDownloadQualitySelectionEvent(WALLPAPERS, which)
          true
        }
        .positiveText(R.string.detail_activity_options_download)
        .negativeText(R.string.cancel_text)
        .show()
  }

  override fun showDownloadStartedMessage() {
    infoToast(getString(R.string.detail_activity_download_started_message))
  }

  override fun showDownloadAlreadyInProgressMessage() {
    errorToast(getString(R.string.detail_activity_download_already_in_progress_message))
  }

  override fun showDownloadCompletedSuccessMessage() {
    successToast(getString(R.string.detail_activity_download_finished_message))
  }

  override fun showCrystallizedDownloadCompletedSuccessMessage() {
    successToast(getString(R.string.detail_activity_crystallized_download_finished_message))
  }

  override fun showCrystallizeDescriptionDialog() {
    MaterialDialog.Builder(this)
        .backgroundColor(colorRes(R.color.primary))
        .customView(R.layout.crystallize_example_dialog_layout, false)
        .contentColor(colorRes(R.color.white))
        .widgetColor(colorRes(R.color.accent))
        .positiveColor(colorRes(R.color.accent))
        .negativeColor(colorRes(R.color.accent))
        .positiveText(getString(R.string.detail_activity_crystallize_dialog_positive_text))
        .negativeText(getString(R.string.detail_activity_crystallize_dialog_negative_text))
        .onPositive { _, _ ->
          presenter.handleCrystallizeDialogPositiveClick()
        }
        .show()
  }

  override fun showCrystallizeSuccessMessage() {
    successToast(getString(R.string.detail_activity_crystallizing_wallpaper_successful_message))
  }

  override fun showImageHasAlreadyBeenCrystallizedMessage() {
    infoToast(getString(R.string.detail_activity_image_already_crystallized_message))
  }

  override fun showAddToCollectionSuccessMessage() {
    successToast(getString(R.string.detail_activity_image_add_to_collection_success_message))
  }

  override fun showExpandedImage(lowQualityLink: String, highQualityLink: String) {
    startActivity(FullScreenImageActivity.getCallingIntent(this, lowQualityLink, highQualityLink))
  }

  override fun showCrystallizedExpandedImage() {
    startActivity(FullScreenImageActivity.getCallingIntent(this, CRYSTALLIZED_BITMAP_CACHE))
  }

  override fun showEditedExpandedImage() {
    startActivity(FullScreenImageActivity.getCallingIntent(this, EDITED_BITMAP_CACHE))
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

    fun getCallingIntent(
      context: Context,
      searchPicturesPresenterEntity: SearchPicturesPresenterEntity
    ): Intent {
      return Intent(context, DetailActivity::class.java).apply {
        putExtras(Bundle().apply {
          putInt(IMAGE_TYPE_TAG, SEARCH.ordinal)
          putExtra(IMAGE_DETAILS_TAG, searchPicturesPresenterEntity)
        })
      }
    }

    fun getCallingIntent(
      context: Context,
      imagePresenterEntity: ImagePresenterEntity
    ): Intent {
      return Intent(context, DetailActivity::class.java).apply {
        putExtras(Bundle().apply {
          putInt(IMAGE_TYPE_TAG, WALLPAPERS.ordinal)
          putExtra(IMAGE_DETAILS_TAG, imagePresenterEntity)
        })
      }
    }
  }

}