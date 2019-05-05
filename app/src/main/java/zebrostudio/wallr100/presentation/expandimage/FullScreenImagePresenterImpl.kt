package zebrostudio.wallr100.presentation.expandimage

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.BITMAP_CACHE
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.CRYSTALLIZED_BITMAP_CACHE
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.REMOTE
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImagePresenter
import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImageView

class FullScreenImagePresenterImpl(
  private var imageOptionsUseCase: ImageOptionsUseCase,
  private var postExecutionThread: PostExecutionThread
) : FullScreenImagePresenter {

  internal var isInFullScreenMode: Boolean = false
  private var fullScreenView: FullScreenImageView? = null
  private var lowQualityImageLink: String? = null
  private var highQualityImageLink: String? = null

  override fun attachView(view: FullScreenImageView) {
    fullScreenView = view
  }

  override fun detachView() {
    fullScreenView = null
  }

  override fun setImageLoadingType(type: Int) {
    when (type) {
      REMOTE.ordinal -> fullScreenView?.getImageLinks()
      CRYSTALLIZED_BITMAP_CACHE.ordinal -> fetchCrystallizedImageBitmap()
      BITMAP_CACHE.ordinal -> fetchEditedImageBitmap()
    }
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

  override fun handleHighQualityImageLoadingFinished() {
    fullScreenView?.hideLoader()
    fullScreenView?.hideLowQualityImage()
  }

  override fun handleHighQualityImageLoadingFailed() {
    fullScreenView?.hideLoader()
    fullScreenView?.showHighQualityImageLoadingError()
  }

  override fun handleZoomImageViewTapped() {
    if (isInFullScreenMode) {
      fullScreenView?.showStatusAndNavBar()
    } else {
      fullScreenView?.hideStatusAndNavBar()
    }
  }

  override fun notifyStatusBarAndNavBarShown() {
    isInFullScreenMode = false
  }

  override fun notifyStatusBarAndNavBarHidden() {
    isInFullScreenMode = true
  }

  private fun fetchCrystallizedImageBitmap() {
    imageOptionsUseCase.getCrystallizedImageSingle()
        .observeOn(postExecutionThread.scheduler)
        .doOnSubscribe {
          fullScreenView?.showLoader()
        }
        .autoDisposable(fullScreenView?.getScope()!!)
        .subscribe({
          fullScreenView?.hideLoader()
          fullScreenView?.showImage(it)
        }, {
          fullScreenView?.hideLoader()
          fullScreenView?.showGenericErrorMessage()
        })
  }

  private fun fetchEditedImageBitmap() {
    imageOptionsUseCase.getEditedImageSingle()
        .observeOn(postExecutionThread.scheduler)
        .doOnSubscribe {
          fullScreenView?.showLoader()
        }
        .autoDisposable(fullScreenView?.getScope()!!)
        .subscribe({
          fullScreenView?.hideLoader()
          fullScreenView?.showImage(it)
        }, {
          fullScreenView?.hideLoader()
          fullScreenView?.showGenericErrorMessage()
        })
  }

}
