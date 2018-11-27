package zebrostudio.wallr100.presentation.wallpaper

import com.uber.autodispose.autoDisposable
import io.reactivex.Single
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentTag
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.presentation.wallpaper.ImageListContract.ImageListView
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper

class ImageListPresenterImpl(
  private val wallpaperImagesUseCase: WallpaperImagesUseCase,
  private val imagePresenterEntityMapper: ImagePresenterEntityMapper,
  private val fragmentNameTag: FragmentTag
) : ImageListContract.ImageListPresenter {

  private var imageListView: ImageListView? = null
  internal var imageListType = 0

  override fun attachView(view: ImageListView) {
    imageListView = view
  }

  override fun detachView() {
    imageListView = null
  }

  override fun setImageListType(fragmentTag: String, position: Int) {
    when (fragmentTag) {
      fragmentNameTag.getTag(R.string.guillotine_explore_title) -> {
        imageListType = position
      }
      fragmentNameTag.getTag(R.string.guillotine_top_picks_title) -> {
        imageListType = position + 1
      }
      fragmentNameTag.getTag(R.string.guillotine_categories_title) -> {
        imageListType = position + 4
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
      0 -> wallpaperImagesUseCase.exploreImagesSingle()
      1 -> wallpaperImagesUseCase.recentImagesSingle()
      2 -> wallpaperImagesUseCase.popularImagesSingle()
      3 -> wallpaperImagesUseCase.standoutImagesSingle()
      4 -> wallpaperImagesUseCase.buildingsImagesSingle()
      5 -> wallpaperImagesUseCase.foodImagesSingle()
      6 -> wallpaperImagesUseCase.natureImagesSingle()
      7 -> wallpaperImagesUseCase.objectsImagesSingle()
      8 -> wallpaperImagesUseCase.peopleImagesSingle()
      else -> wallpaperImagesUseCase.technologyImagesSingle()
    }
  }

}