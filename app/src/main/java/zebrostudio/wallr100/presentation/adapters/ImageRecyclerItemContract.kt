package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity

interface ImageRecyclerItemContract {

  interface ImageRecyclerItemView {
    fun setImageviewBackground(colorHexCode: String)
    fun setImage(link: String)
  }

  interface ImageRecyclerviewPresenter {
    fun setTypeAsSearch()
    fun setSearchResultList(list: List<SearchPicturesPresenterEntity>)
    fun addToSearchResultList(list: List<SearchPicturesPresenterEntity>)
    fun onBindRepositoryRowViewAtPosition(position: Int, rowView: ImageRecyclerItemView)
    fun getItemCount(): Int
    fun clearAll()
  }
}