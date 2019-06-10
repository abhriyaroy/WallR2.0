package zebrostudio.wallr100.presentation.expandimage

import android.graphics.Bitmap
import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView

interface FullScreenImageContract {

  interface FullScreenImageView : BaseView {
    fun getImageLinks()
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
    fun setImageLoadingType(type: Int)
    fun setLowQualityImageLink(link: String)
    fun setHighQualityImageLink(link: String)
    fun handleHighQualityImageLoadingFinished()
    fun handleHighQualityImageLoadingFailed()
    fun handleZoomImageViewTapped()
    fun notifyStatusBarAndNavBarShown()
    fun notifyStatusBarAndNavBarHidden()
  }
}