package zebrostudio.wallr100.presentation.expandimage

import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView

interface FullScreenImageContract {

  interface FullScreenImageView : BaseView {
    fun getImageLinksFromBundle()
    fun showLowQualityImage(link: String)
    fun startLoadingHighQualityImage(link: String)
    fun showLoader()
    fun hideLoader()
    fun showHighQualityImageLoadingError()
    fun hideLowQualityImage()
    fun showStatusBarAndNavigationBar()
    fun hideStatusBarAndNavigationBar()
  }

  interface FullScreenImagePresenter : BasePresenter<FullScreenImageView> {
    fun setLowQualityImageLink(link: String)
    fun setHighQualityImageLink(link: String)
    fun notifyHighQualityImageLoadingFinished()
    fun notifyHighQualityImageLoadingFailed()
    fun notifyPhotoViewTapped()
    fun notifyStatusBarAndNavBarShown()
    fun notifyStatusBarAndNavBarHidden()
  }
}