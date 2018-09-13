package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.presentation.adapters.ImageRecyclerviewPresenterImpl.ListType.*
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity

class ImageRecyclerviewPresenterImpl : ImageRecyclerItemContract.ImageRecyclerviewPresenter {

  private lateinit var imageType: ListType
  private var searchResultList = mutableListOf<SearchPicturesPresenterEntity>()

  override fun setTypeAsSearch() {
    imageType = SEARCH
  }

  override fun setSearchResultList(list: List<SearchPicturesPresenterEntity>) {
    searchResultList = list as MutableList<SearchPicturesPresenterEntity>
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
    rowView.setImageviewBackground(searchResultList[position].paletteColor)
    when (imageType) {
      SEARCH ->
        rowView.setImage(searchResultList[position].imageQualityUrls.smallImageLink)
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

  private enum class ListType {
    SEARCH,
    WALLPAPERS
  }

}