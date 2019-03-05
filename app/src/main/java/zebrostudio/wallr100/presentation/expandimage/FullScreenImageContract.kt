package zebrostudio.wallr100.presentation.expandimage

import android.content.Intent
import android.graphics.Bitmap
import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView

interface FullScreenImageContract {

  interface FullScreenImageView : BaseView {
    fun throwIllegalStateException()
    fun getImageLinksFromBundle()
    fun showLowQualityImage(link: String)
    fun startLoadingHighQualityImage(link: String)
    fun showImage(bitmap: Bitmap)
    fun showLoader()
    fun hideLoader()
    fun showHighQualityImageLoadingError()
    fun hideLowQualityImage()
    fun showStatusAndNavBar()
    fun hideStatusAndNavBar()
    fun showGenericErrorMessage()
  }

  interface FullScreenImagePresenter : BasePresenter<FullScreenImageView> {
    fun setCalledIntent(intent: Intent)
    fun setLowQualityImageLink(link: String)
    fun setHighQualityImageLink(link: String)
    fun notifyHighQualityImageLoadingFinished()
    fun notifyHighQualityImageLoadingFailed()
    fun notifyPhotoViewTapped()
    fun notifyStatusBarAndNavBarShown()
    fun notifyStatusBarAndNavBarHidden()
  }
}