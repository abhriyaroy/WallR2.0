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
import kotlinx.android.synthetic.main.activity_detail.addToCollectionImageLayout
import kotlinx.android.synthetic.main.activity_detail.authorImage
import kotlinx.android.synthetic.main.activity_detail.authorName
import kotlinx.android.synthetic.main.activity_detail.crystallizeImageLayout
import kotlinx.android.synthetic.main.activity_detail.downloadImageLayout
import kotlinx.android.synthetic.main.activity_detail.editAndSetImageLayout
import kotlinx.android.synthetic.main.activity_detail.expandIconView
import kotlinx.android.synthetic.main.activity_detail.imageView
import kotlinx.android.synthetic.main.activity_detail.setWallpaperImageLayout
import kotlinx.android.synthetic.main.activity_detail.shareImageLayout
import kotlinx.android.synthetic.main.activity_detail.slidingPanel
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.utils.errorToast
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

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    presenter.attachView(this)
    setContentView(R.layout.activity_detail)
    presenter.setImageType(
        intent.getSerializableExtra(imageType) as ImageListType)
    setUpExpandPanel()
    attachClickListeners()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>, grantResults: IntArray
  ) {
    presenter.handlePermissionRequestResult(requestCode, permissions, grantResults)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    presenter.handleActivityResult(requestCode, resultCode, data)
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
  }

  companion object {
    var imageDetails = "ImageDetails"
    var imageType = "ImageType"

    fun getCallingIntent(context: Context): Intent {
      return Intent(context, DetailActivity::class.java)
    }
  }

}
