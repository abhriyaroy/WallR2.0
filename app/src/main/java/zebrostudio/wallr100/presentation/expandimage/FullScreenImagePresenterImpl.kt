package zebrostudio.wallr100.presentation.expandimage

import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImageView

class FullScreenImagePresenterImpl : FullScreenImageContract.FullScreenImagePresenter {
  private var fullScreenView: FullScreenImageView? = null

  private var lowQualityImageLink: String? = null

  private var highQualityImageLink: String? = null
  private var isInFullScreenMode: Boolean = false
  override fun attachView(view: FullScreenImageView) {
    fullScreenView = view
    fullScreenView?.getImageLinksFromBundle()
  }

  override fun detachView() {
    fullScreenView = null
  }

  override fun setLowQualityImageLink(link: String) {
    lowQualityImageLink = link
    fullScreenView?.showLowQualityImage(lowQualityImageLink!!)
    fullScreenView?.showLoader()
  }

  override fun setHighQualityImageLink(link: String) {
    highQualityImageLink = link
    fullScreenView?.startLoadingHighQualityImage(highQualityImageLink!!)
  }

  override fun notifyHighQualityImageLoadingFinished() {
    fullScreenView?.hideLoader()
    fullScreenView?.hideLowQualityImage()
  }

  override fun notifyHighQualityImageLoadingFailed() {
    fullScreenView?.hideLoader()
    fullScreenView?.showHighQualityImageLoadingError()
  }

  override fun notifyPhotoViewTapped() {
    if (isInFullScreenMode) {
      fullScreenView?.showStatusBarAndNavigationBar()
    } else {
      fullScreenView?.hideStatusBarAndNavigationBar()
    }
  }

  override fun notifyStatusBarAndNavBarShown() {
    isInFullScreenMode = false
  }

  override fun notifyStatusBarAndNavBarHidden() {
    isInFullScreenMode = true
  }

}
