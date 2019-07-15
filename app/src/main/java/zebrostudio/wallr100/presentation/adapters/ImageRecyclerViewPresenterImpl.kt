package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract.ImageRecyclerItemView
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract.ImageRecyclerViewPresenter
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.SEARCH
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.WALLPAPERS
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

class ImageRecyclerViewPresenterImpl : ImageRecyclerViewPresenter {

  internal lateinit var imageType: ImageListType
  internal val searchResultList = mutableListOf<SearchPicturesPresenterEntity>()
  internal val wallpaperImageList = mutableListOf<ImagePresenterEntity>()

  override fun setListType(imageListType: ImageListType) {
    imageType = imageListType
  }

  override fun setSearchResultList(list: List<SearchPicturesPresenterEntity>) {
    searchResultList.addAll(list)
  }

  override fun setWallpaperImageList(list: List<ImagePresenterEntity>) {
    wallpaperImageList.addAll(list)
  }

  override fun addToSearchResultList(list: List<SearchPicturesPresenterEntity>) {
    list.forEach {
      searchResultList.add(it)
    }
  }

  override fun onBindRepositoryRowViewAtPosition(
    position: Int,
    rowView: ImageRecyclerItemView
  ) {
    when (imageType) {
      SEARCH -> {
        rowView.setImageViewBackgroundAndAttachClickListener(
          searchResultList[position].paletteColor)
        rowView.setSearchImage(
          searchResultList[position].imageQualityUrlPresenterEntity.smallImageLink)
      }
      WALLPAPERS -> {
        rowView.setImageViewBackgroundAndAttachClickListener(wallpaperImageList[position].color)
        rowView.setWallpaperImage(wallpaperImageList[position].imageLink.thumb)
      }
    }
  }

  override fun getItemCount(): Int {
    return when (imageType) {
      SEARCH -> searchResultList.size
      WALLPAPERS -> wallpaperImageList.size
    }
  }

  override fun clearAllSearchResults() {
    searchResultList.clear()
  }

  override fun handleImageClicked(
    position: Int,
    rowView: ImageRecyclerItemView
  ) {
    when (imageType) {
      SEARCH -> rowView.showSearchImageDetails(searchResultList[position])
      WALLPAPERS -> rowView.showWallpaperImageDetails(wallpaperImageList[position])
    }
  }

  enum class ImageListType {
    WALLPAPERS,
    SEARCH
  }

}