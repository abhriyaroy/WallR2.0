package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.*
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity

class ImageRecyclerViewPresenterImpl : ImageRecyclerItemContract.ImageRecyclerViewPresenter {

  private lateinit var imageType: ImageListType
  private val searchResultList = mutableListOf<SearchPicturesPresenterEntity>()

  override fun setListType(imageListType: ImageListType) {
    imageType = imageListType
  }

  override fun setSearchResultList(list: List<SearchPicturesPresenterEntity>) {
    searchResultList.addAll(list)
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
    rowView.setImageViewBackground(searchResultList[position].paletteColor)
    when (imageType) {
      SEARCH ->
        rowView.setImage(searchResultList[position].imageQualityUrlPresenterEntity.smallImageLink)
      WALLPAPERS -> {
        // Do something
      }
    }
  }

  override fun getItemCount(): Int {
    return searchResultList.size
  }

  override fun clearAll() {
    searchResultList.clear()
  }

  enum class ImageListType {
    SEARCH,
    WALLPAPERS
  }

}