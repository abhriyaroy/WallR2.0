package zebrostudio.wallr100.presentation.wallpaper

import com.uber.autodispose.autoDisposable
import io.reactivex.Single
import zebrostudio.wallr100.android.ui.wallpaper.WallpaperFragment.Companion.EXPLORE_FRAGMENT_TAG
import zebrostudio.wallr100.android.ui.wallpaper.WallpaperFragment.Companion.TOP_PICKS_FRAGMENT_TAG
import zebrostudio.wallr100.android.ui.wallpaper.WallpaperFragment.Companion.CATEGORIES_FRAGMENT_TAG
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.presentation.wallpaper.ImageListContract.ImageListView
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper

class ImageListPresenterImpl(
  private val wallpaperImagesUseCase: WallpaperImagesUseCase,
  private val imagePresenterEntityMapper: ImagePresenterEntityMapper
) : ImageListContract.ImageListPresenter {

  private var imageListView: ImageListView? = null
  internal lateinit var imageListType: String
  private val imageListTypes = listOf(
      "EXPLORE",
      "RECENT",
      "POPULAR",
      "STANDOUTS",
      "BUILDINGS",
      "FOOD",
      "NATURE",
      "OBJECTS",
      "PEOPLE",
      "TECHNOLOGY"
  )

  override fun attachView(view: ImageListView) {
    imageListView = view
  }

  override fun detachView() {
    imageListView = null
  }

  override fun setImageListType(fragmentTag: String, position: Int) {
    when (fragmentTag) {
      EXPLORE_FRAGMENT_TAG -> {
        imageListType = imageListTypes[position]
      }
      TOP_PICKS_FRAGMENT_TAG -> {
        imageListType = imageListTypes[position + 1]
      }
      CATEGORIES_FRAGMENT_TAG -> {
        imageListType = imageListTypes[position + 4]
      }
    }
  }

  override fun fetchImages(refresh: Boolean) {
    imageListView?.hideAllLoadersAndMessageViews()
    if (!refresh) {
      imageListView?.showLoader()
    }
    getImageList()
        .map {
          imagePresenterEntityMapper.mapToPresenterEntity(it)
        }
        .autoDisposable(imageListView?.getScope()!!)
        .subscribe({
          if (refresh) {
            imageListView?.hideRefreshing()
          }
          imageListView?.hideLoader()
          imageListView?.showImageList(it)
        }, {
          imageListView?.hideLoader()
          imageListView?.showNoInternetMessageView()
          if (refresh) {
            imageListView?.hideRefreshing()
          }
        })
  }

  private fun getImageList(): Single<List<ImageModel>> {
    return when (imageListType) {
      imageListTypes[0] -> wallpaperImagesUseCase.getExploreImages()
      imageListTypes[1] -> wallpaperImagesUseCase.getRecentImages()
      imageListTypes[2] -> wallpaperImagesUseCase.getPopularImages()
      imageListTypes[3] -> wallpaperImagesUseCase.getStandoutImages()
      imageListTypes[4] -> wallpaperImagesUseCase.getBuildingsImages()
      imageListTypes[5] -> wallpaperImagesUseCase.getFoodImages()
      imageListTypes[6] -> wallpaperImagesUseCase.getNatureImages()
      imageListTypes[7] -> wallpaperImagesUseCase.getObjectsImages()
      imageListTypes[8] -> wallpaperImagesUseCase.getPeopleImages()
      else -> wallpaperImagesUseCase.getTechnologyImages()
    }
  }

}