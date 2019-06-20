package zebrostudio.wallr100.android.ui.expandimage

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_full_screen_image.backIcon
import kotlinx.android.synthetic.main.activity_full_screen_image.highQualityImagePhotoView
import kotlinx.android.synthetic.main.activity_full_screen_image.lowQualityImageView
import kotlinx.android.synthetic.main.fragment_image_list.spinkitView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.ui.ImageLoader
import zebrostudio.wallr100.android.ui.LoaderListener
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.REMOTE
import zebrostudio.wallr100.android.utils.*
import zebrostudio.wallr100.presentation.detail.images.ILLEGAL_STATE_EXCEPTION_MESSAGE
import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImagePresenter
import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImageView
import javax.inject.Inject

class FullScreenImageActivity : BaseActivity(), FullScreenImageView {

  @Inject
  internal lateinit var presenter: FullScreenImagePresenter
  @Inject
  internal lateinit var imageLoader: ImageLoader

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    preventWindowFromTakingScreenshot()
    setContentView(R.layout.activity_full_screen_image)
    presenter.attachView(this)
    intent.let {
      if (it.hasExtra(IMAGE_LOADING_TYPE_TAG)) {
        presenter.setImageLoadingType(it.getIntExtra(IMAGE_LOADING_TYPE_TAG, REMOTE.ordinal))
      } else {
        throw IllegalStateException(ILLEGAL_STATE_EXCEPTION_MESSAGE)
      }
    }
    initStatusBarAndNavigationBarConfiguration()
    configurePhotoView()
    backIcon.setOnClickListener {
      onBackPressed()
    }
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  override fun getImageLinks() {
    intent.let {
      if (it.hasExtra(LOW_QUALITY_IMAGE_TAG) && it.hasExtra(HIGH_QUALITY_IMAGE_TAG)) {
        presenter.setLowQualityImageLink(it.getStringExtra(LOW_QUALITY_IMAGE_TAG))
        presenter.setHighQualityImageLink(it.getStringExtra(HIGH_QUALITY_IMAGE_TAG))
      } else {
        throw IllegalStateException(ILLEGAL_STATE_EXCEPTION_MESSAGE)
      }
    }
  }

  override fun showLowQualityImage(link: String) {
    imageLoader.load(this, link, lowQualityImageView)
  }

  override fun startLoadingHighQualityImage(link: String) {
    imageLoader.loadWithListener(this, link, highQualityImagePhotoView, object : LoaderListener {
      override fun onResourceReady(resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean): Boolean {
        presenter.handleHighQualityImageLoadingFinished()
        return false
      }

      override fun onLoadFailed(e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean): Boolean {
        presenter.handleHighQualityImageLoadingFailed()
        return false
      }
    })
  }

  override fun showImage(bitmap: Bitmap) {
    imageLoader.loadWithListener(this, bitmap, highQualityImagePhotoView, object : LoaderListener {
      override fun onResourceReady(resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean): Boolean {
        presenter.handleHighQualityImageLoadingFinished()
        return false
      }

      override fun onLoadFailed(e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean): Boolean {
        presenter.handleHighQualityImageLoadingFailed()
        return false
      }
    })
  }

  override fun showLoader() {
    spinkitView.visible()
  }

  override fun hideLoader() {
    spinkitView.gone()
  }

  override fun showHighQualityImageLoadingError() {
    errorToast(getString(R.string.unable_to_load_hd_image_error))
  }

  override fun hideLowQualityImage() {
    lowQualityImageView.gone()
  }

  override fun showStatusAndNavBar() {
    showStatusBarAndNavigationBar()
    presenter.notifyStatusBarAndNavBarShown()
  }

  override fun hideStatusAndNavBar() {
    hideStatusBarAndNavigationBar()
    presenter.notifyStatusBarAndNavBarHidden()

  }

  override fun showGenericErrorMessage() {
    errorToast(getString(R.string.generic_error_message))
  }

  private fun preventWindowFromTakingScreenshot() {
    disableScreenshots()
  }

  private fun initStatusBarAndNavigationBarConfiguration() {
    makeNavigationBarAndStatusBarTransparent()
    hideStatusAndNavBar()
  }

  private fun configurePhotoView() {
    highQualityImagePhotoView.setOnPhotoTapListener { _, _, _ ->
      presenter.handleZoomImageViewTapped()
    }
    highQualityImagePhotoView.maximumScale = 5.0f
    highQualityImagePhotoView.mediumScale = 3.0f
  }

  companion object {
    const val IMAGE_LOADING_TYPE_TAG = "LOADING_TYPE"
    const val LOW_QUALITY_IMAGE_TAG = "LOW"
    const val HIGH_QUALITY_IMAGE_TAG = "HIGH"

    fun getCallingIntent(context: Context, imageLoadingType: ImageLoadingType): Intent {
      return Intent(context, FullScreenImageActivity::class.java)
          .apply {
            putExtras(Bundle().apply {
              putInt(IMAGE_LOADING_TYPE_TAG, imageLoadingType.ordinal)
            })
          }
    }

    fun getCallingIntent(
      context: Context,
      lowQualityLink: String,
      highQualityLink: String
    ): Intent {
      return Intent(context, FullScreenImageActivity::class.java).apply {
        putExtras(Bundle().apply {
          putInt(IMAGE_LOADING_TYPE_TAG, REMOTE.ordinal)
          putString(LOW_QUALITY_IMAGE_TAG, lowQualityLink)
          putString(HIGH_QUALITY_IMAGE_TAG, highQualityLink)
        })
      }
    }
  }
}

enum class ImageLoadingType {
  REMOTE,
  BITMAP_CACHE,
  CRYSTALLIZED_BITMAP_CACHE
}
