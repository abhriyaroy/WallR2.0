package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.*
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

class ImageRecyclerViewPresenterImpl : ImageRecyclerItemContract.ImageRecyclerViewPresenter {

  private lateinit var imageType: ImageListType
  private val searchResultList = mutableListOf<SearchPicturesPresenterEntity>()
  private val wallpaperImageList = mutableListOf<ImagePresenterEntity>()

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
    rowView: ImageRecyclerItemContract.ImageRecyclerItemView
  ) {
    when (imageType) {
      SEARCH -> {
        rowView.setImageViewBackground(searchResultList[position].paletteColor)
        rowView.setImage(searchResultList[position].imageQualityUrlPresenterEntity.smallImageLink)
      }
      WALLPAPERS -> {
        rowView.setImageViewBackground(wallpaperImageList[position].color)
        rowView.setImage(wallpaperImageList[position].imageLink.thumb)
      }
    }
  }

  override fun getItemCount(): Int {
    return when (imageType) {
      SEARCH -> searchResultList.size
      WALLPAPERS -> wallpaperImageList.size
    }
  }

  override fun clearAll() {
    searchResultList.clear()
  }

  enum class ImageListType {
    SEARCH,
    WALLPAPERS
  }

}