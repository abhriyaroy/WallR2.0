package zebrostudio.wallr100.presentation.wallpaper.explore

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper

class ExplorePresenterImpl(
  private val wallpaperImagesUseCase: WallpaperImagesUseCase,
  private val imagePresenterEntityMapper: ImagePresenterEntityMapper
) : ExploreContract.ExplorePresenter {

  private var exploreView: ExploreContract.ExploreView? = null

  override fun attachView(view: ExploreContract.ExploreView) {
    exploreView = view
    fetchImages()
  }

  override fun detachView() {
    exploreView = null
  }

  private fun fetchImages() {
    wallpaperImagesUseCase.getExploreImages()
        .map {
          imagePresenterEntityMapper.mapToPresenterEntity(it)
        }
        .autoDisposable(exploreView?.getScope()!!)
        .subscribe({
          exploreView?.showImageList(it)
        }, {

        })
  }

}