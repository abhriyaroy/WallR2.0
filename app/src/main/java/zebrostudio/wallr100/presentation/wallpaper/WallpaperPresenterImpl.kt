package zebrostudio.wallr100.presentation.wallpaper

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper

class WallpaperPresenterImpl(
  private val wallpaperImagesUseCase: WallpaperImagesUseCase,
  private val imagePresenterEntityMapper: ImagePresenterEntityMapper
) : WallpaperContract.WallpaperPresenter {

  private var wallpaperView: WallpaperContract.WallpaperView? = null

  override fun attachView(view: WallpaperContract.WallpaperView) {
    wallpaperView = view
    getImages()
  }

  override fun detachView() {
    wallpaperView = null
  }

  private fun getImages() {
    wallpaperImagesUseCase.getExploreImages()
        .map {
          imagePresenterEntityMapper.mapToPresenterEntity(it)
        }
        .autoDisposable(wallpaperView?.getScope()!!)
        .subscribe({
          wallpaperView?.showImages(it)
        }, {
          wallpaperView?.showGenericErrorMessageView()
        })
  }
}