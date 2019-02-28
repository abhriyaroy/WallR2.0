package zebrostudio.wallr100.presentation.expandimage

import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.REMOTE
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImageView

class FullScreenImagePresenterImpl(
  private var imageOptionsUseCase: ImageOptionsUseCase,
  private var postExecutionThread: PostExecutionThread
) :
    FullScreenImageContract.FullScreenImagePresenter {

  private var fullScreenView: FullScreenImageView? = null
  private var lowQualityImageLink: String? = null
  private var highQualityImageLink: String? = null
  private var isInFullScreenMode: Boolean = false
  private var imageLoadingTypeOrdinal: Int = REMOTE.ordinal

  override fun attachView(view: FullScreenImageView) {
    fullScreenView = view
    imageLoadingTypeOrdinal = view.getImageLoadingType()
    processImageLoadingTypeOrdinal(imageLoadingTypeOrdinal)
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

  private fun processImageLoadingTypeOrdinal(imageLoadingType: Int) {
    when (imageLoadingType) {
      REMOTE.ordinal -> fullScreenView?.getImageLinksFromBundle()
      else -> {
        imageOptionsUseCase.getCrystallizedBitmapSingle()
            .observeOn(postExecutionThread.scheduler)
            .subscribe({
              fullScreenView?.showImage(it)
            }, {
              fullScreenView?.hideLoader()
              fullScreenView?.showGenericErrorMessage()
            })
      }
    }
  }

}
