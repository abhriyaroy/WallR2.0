package zebrostudio.wallr100.presentation.wallpaper

import com.uber.autodispose.autoDisposable
import io.reactivex.Single
import zebrostudio.wallr100.android.ui.wallpaper.ImageListType.imageType
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

  override fun attachView(view: ImageListView) {
    imageListView = view
  }

  override fun detachView() {
    imageListView = null
  }

  override fun setImageListType(fragmentTag: String, position: Int) {
    when (fragmentTag) {
      EXPLORE_FRAGMENT_TAG -> {
        imageListType = imageType[position]
      }
      TOP_PICKS_FRAGMENT_TAG -> {
        imageListType = imageType[position + 1]
      }
      CATEGORIES_FRAGMENT_TAG -> {
        imageListType = imageType[position + 4]
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
          System.out.println(it.message)
          imageListView?.hideLoader()
          imageListView?.showNoInternetMessageView()
          if (refresh) {
            imageListView?.hideRefreshing()
          }
        })
  }

  private fun getImageList(): Single<List<ImageModel>> {
    return when (imageListType) {
      imageType[0] -> wallpaperImagesUseCase.exploreImagesSingle()
      imageType[1] -> wallpaperImagesUseCase.recentImagesSingle()
      imageType[2] -> wallpaperImagesUseCase.popularImagesSingle()
      imageType[3] -> wallpaperImagesUseCase.standoutImagesSingle()
      imageType[4] -> wallpaperImagesUseCase.buildingsImagesSingle()
      imageType[5] -> wallpaperImagesUseCase.foodImagesSingle()
      imageType[6] -> wallpaperImagesUseCase.natureImagesSingle()
      imageType[7] -> wallpaperImagesUseCase.objectsImagesSingle()
      imageType[8] -> wallpaperImagesUseCase.peopleImagesSingle()
      else -> wallpaperImagesUseCase.technologyImagesSingle()
    }
  }

}