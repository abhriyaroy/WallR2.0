package zebrostudio.wallr100.presentation.wallpaper.explore

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.android.ui.wallpaper.WallpaperFragment.Companion.EXPLORE_FRAGMENT_TAG
import zebrostudio.wallr100.android.ui.wallpaper.WallpaperFragment.Companion.TOP_PICKS_FRAGMENT_TAG
import zebrostudio.wallr100.android.ui.wallpaper.WallpaperFragment.Companion.CATEGORIES_FRAGMENT_TAG
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.presentation.wallpaper.explore.ImageListContract.ImageListView
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper

class ImageListPresenterImpl(
  private val wallpaperImagesUseCase: WallpaperImagesUseCase,
  private val imagePresenterEntityMapper: ImagePresenterEntityMapper
) : ImageListContract.ImageListPresenter {

  private var imageListView: ImageListView? = null
  private var imageListType: ImageListType? = null

  override fun attachView(view: ImageListView) {
    imageListView = view
    fetchImages()
  }

  override fun detachView() {
    imageListView = null
  }

  override fun setImageListType(fragmentTag: String, position: Int) {
    when (fragmentTag) {
      EXPLORE_FRAGMENT_TAG -> {
        imageListType = ImageListType.EXPLORE
      }
      TOP_PICKS_FRAGMENT_TAG -> {

      }
      CATEGORIES_FRAGMENT_TAG -> {

      }
    }
  }

  private fun fetchImages() {
    imageListView?.hideAllLoadersAndMessageViews()
    imageListView?.showLoader()
    wallpaperImagesUseCase.getExploreImages()
        .map {
          imagePresenterEntityMapper.mapToPresenterEntity(it)
        }
        .autoDisposable(imageListView?.getScope()!!)
        .subscribe({
          imageListView?.hideLoader()
          imageListView?.showImageList(it)
        }, {
          imageListView?.hideLoader()
          imageListView?.showNoInternetMessageView()
        })
  }

}

enum class ImageListType {
  EXPLORE,
  RECENT,
  POPULAR,
  STANDOUTS,
  BUILDINGS,
  FOOD,
  NATURE,
  OBJECTS,
  PEOPLE,
  TECHNOLOGY
}