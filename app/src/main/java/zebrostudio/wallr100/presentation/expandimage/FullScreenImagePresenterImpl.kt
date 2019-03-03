package zebrostudio.wallr100.presentation.expandimage

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.CRYSTALLIZED_BITMAP_CACHE
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.EDITED_BITMAP_CACHE
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.REMOTE
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImageView

class FullScreenImagePresenterImpl(
  private var imageOptionsUseCase: ImageOptionsUseCase,
  private var postExecutionThread: PostExecutionThread
) :
    FullScreenImageContract.FullScreenImagePresenter {

  internal var isInFullScreenMode: Boolean = false
  private var fullScreenView: FullScreenImageView? = null
  private var lowQualityImageLink: String? = null
  private var highQualityImageLink: String? = null
  private var imageLoadingTypeOrdinal: Int = REMOTE.ordinal

  override fun attachView(view: FullScreenImageView) {
    fullScreenView = view
    imageLoadingTypeOrdinal = fullScreenView?.getImageLoadingType()!!
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

  private fun processImageLoadingTypeOrdinal(imageLoadingType: Int) {
    when (imageLoadingType) {
      REMOTE.ordinal -> fullScreenView?.getImageLinksFromBundle()
      CRYSTALLIZED_BITMAP_CACHE.ordinal -> fetchCrystallizedImageBitmap()
      EDITED_BITMAP_CACHE.ordinal -> fetchEditedImageBitmap()
    }
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
