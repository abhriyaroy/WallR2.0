package zebrostudio.wallr100.android.ui.expandimage

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_full_screen_image.imageFullSize
import kotlinx.android.synthetic.main.activity_full_screen_image.imageThumbSize
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
    setContentView(R.layout.activity_full_screen_image)
    presenter.attachView(this)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  override fun getImageLinksFromBundle() {
    presenter.setLowQualityImageLink(intent.getBundleExtra(lowQualityImageBundleTag).toString())
    presenter.setHighQualityImageLink(intent.getBundleExtra(highQualityImageBundleTag).toString())
  }

  override fun showLowQualityImage(link: String) {
    Glide.with(this)
        .load(link)
        .into(imageThumbSize)
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
        .into(imageFullSize)
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
    imageThumbSize.gone()
  }

  companion object {

    const val lowQualityImageBundleTag = "LOW"
    const val highQualityImageBundleTag = "HIGH"

    fun getCallingIntent(context: Context): Intent {
      return Intent(context, FullScreenImageActivity::class.java)
    }
  }
}
