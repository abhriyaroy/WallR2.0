package zebrostudio.wallr100.android.ui.expandimage

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_full_screen_image.backIcon
import kotlinx.android.synthetic.main.activity_full_screen_image.highQualityImagePhotoView
import kotlinx.android.synthetic.main.activity_full_screen_image.lowQualityImageView
import kotlinx.android.synthetic.main.fragment_image_list.spinkitView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImagePresenter
import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImageView
import javax.inject.Inject

class FullScreenImageActivity : BaseActivity(), FullScreenImageView {

  @Inject internal lateinit var presenter: FullScreenImagePresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    preventWindowFromTakingScreenshot()
    setContentView(R.layout.activity_full_screen_image)
    presenter.attachView(this)
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

  override fun getImageLinksFromBundle() {
    presenter.setLowQualityImageLink(intent.getStringExtra(lowQualityImageBundleTag))
    presenter.setHighQualityImageLink(intent.getStringExtra(highQualityImageBundleTag))
  }

  override fun showLowQualityImage(link: String) {
    Glide.with(this)
        .load(link)
        .into(lowQualityImageView)
  }

  override fun startLoadingHighQualityImage(link: String) {
    Glide.with(this)
        .load(link)
        .listener(object : RequestListener<Drawable> {
          override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
          ): Boolean {
            presenter.notifyHighQualityImageLoadingFailed()
            return false
          }

          override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
          ): Boolean {
            presenter.notifyHighQualityImageLoadingFinished()
            return false
          }

        })
        .into(highQualityImagePhotoView)
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

  override fun showStatusBarAndNavigationBar() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.navigationBarColor = Color.TRANSPARENT
      window.statusBarColor = Color.TRANSPARENT
    }
    presenter.notifyStatusBarAndNavBarShown()
  }

  override fun hideStatusBarAndNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          or View.SYSTEM_UI_FLAG_FULLSCREEN
          or View.SYSTEM_UI_FLAG_IMMERSIVE)
      presenter.notifyStatusBarAndNavBarHidden()
    }
  }

  private fun preventWindowFromTakingScreenshot() {
    try {
      window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
          WindowManager.LayoutParams.FLAG_SECURE)
    } catch (e: Exception) {

    }
  }

  private fun initStatusBarAndNavigationBarConfiguration() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
        if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
          showStatusBarAndNavigationBar()
        } else {
          hideStatusBarAndNavigationBar()
        }
      }
      window.navigationBarColor = Color.TRANSPARENT
      window.statusBarColor = Color.TRANSPARENT
    }
    hideStatusBarAndNavigationBar()
  }

  private fun configurePhotoView() {
    highQualityImagePhotoView.setOnPhotoTapListener { _, _, _ ->
      presenter.notifyPhotoViewTapped()
    }
    highQualityImagePhotoView.maximumScale = 5.0f
    highQualityImagePhotoView.mediumScale = 3.0f
  }

  companion object {
    const val lowQualityImageBundleTag = "LOW"
    const val highQualityImageBundleTag = "HIGH"

    fun getCallingIntent(context: Context): Intent {
      return Intent(context, FullScreenImageActivity::class.java)
    }
  }
}
