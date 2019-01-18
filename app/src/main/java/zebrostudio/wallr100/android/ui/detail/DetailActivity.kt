package zebrostudio.wallr100.android.ui.detail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.ActivityCompat
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
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.zagum.expandicon.ExpandIconView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
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
import kotlinx.android.synthetic.main.activity_detail.wallpaperActionProgressPercentage
import kotlinx.android.synthetic.main.activity_detail.wallpaperActionProgressSpinkit
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.infoToast
import zebrostudio.wallr100.android.utils.successToast
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType
import zebrostudio.wallr100.presentation.detail.ActionType
import zebrostudio.wallr100.presentation.detail.DetailContract.DetailPresenter
import zebrostudio.wallr100.presentation.detail.DetailContract.DetailView
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity
import javax.inject.Inject

class DetailActivity : BaseActivity(), DetailView {

  @Inject lateinit var presenter: DetailPresenter

  private var materialProgressLoader: MaterialDialog? = null
  private val slidingPanelParallelOffset = 40
  private val initialLoaderProgress = 0
  private val initialLoaderProgressPercentage = "0%"
  private val blurRadius: Float = 8F

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    presenter.attachView(this)
    setContentView(R.layout.activity_detail)
    presenter.setImageType(
        intent.getSerializableExtra(imageType) as ImageListType)
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
    presenter.handleActivityResult(requestCode, resultCode, data)
  }

  override fun onBackPressed() {
    presenter.handleBackButtonClick()
  }

  override fun getWallpaperImageDetails(): ImagePresenterEntity {
    return intent.getSerializableExtra(imageDetails) as ImagePresenterEntity
  }

  override fun getSearchImageDetails(): SearchPicturesPresenterEntity {
    return intent.getSerializableExtra(imageDetails) as SearchPicturesPresenterEntity
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

  override fun showImageLoadError() {
    errorToast(getString(R.string.detail_activity_unable_to_load_hd_image_error))
  }

  override fun showNoInternetError() {
    errorToast(getString(R.string.no_internet_message))
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
    ActivityCompat.requestPermissions(this,
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE),
        actionType.ordinal)
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
        .widgetColor(resources.getColor(R.color.color_accent))
        .contentColor(resources.getColor(R.color.color_white))
        .content(message)
        .backgroundColor(resources.getColor(R.color.color_primary))
        .progress(true, initialLoaderProgress)
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

  override fun blurScreenAndInitializeProgressPercentage() {
    blurView.visible()
    wallpaperActionProgressPercentage.text = initialLoaderProgressPercentage
    wallpaperActionProgressPercentage.visible()
    loadingHintBelowProgressPercentage.text =
        getString(R.string.detail_activity_grabbing_best_quality_wallpaper_message)
    loadingHintBelowProgressPercentage.visible()
  }

  override fun hideScreenBlur() {
    blurView.gone()
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
    wallpaperActionProgressPercentage.startAnimation(exitAnimation)
    loadingHintBelowProgressPercentage.startAnimation(exitAnimation)
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
    wallpaperActionProgressPercentage.text = progress
  }

  override fun showWallpaperOperationInProgressWaitMessage() {
    infoToast(getString(R.string.detail_activity_finalizing_stuff_wait_message), Toast.LENGTH_SHORT)
  }

  override fun showDownloadWallpaperCancelledMessage() {
    infoToast(getString(R.string.detail_activity_wallpaper_download_cancelled_message))
  }

  override fun exitView() {
    this.finish()
    overridePendingTransition(R.anim.no_change, R.anim.slide_to_right)
  }

  private fun setUpExpandPanel() {
    expandIconView.setState(ExpandIconView.LESS, false)
    slidingPanel.setParallaxOffset(slidingPanelParallelOffset)
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
        } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
          expandIconView.setState(ExpandIconView.LESS, true)
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
  }

  private fun setUpBlurView() {
    blurView.setupWith(parentFrameLayout).setBlurAlgorithm(RenderScriptBlur(this))
        .setBlurRadius(blurRadius)
  }

  companion object {
    var imageDetails = "ImageDetails"
    var imageType = "ImageType"

    fun getCallingIntent(context: Context): Intent {
      return Intent(context, DetailActivity::class.java)
    }
  }

}
