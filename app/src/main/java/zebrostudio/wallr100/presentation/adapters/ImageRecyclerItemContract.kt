package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

interface ImageRecyclerItemContract {

  interface ImageRecyclerItemView {
    fun setImageViewBackground(colorHexCode: String)
    fun setImage(link: String)
  }

  interface ImageRecyclerViewPresenter {
    fun setListType(imageListType: ImageListType)
    fun setSearchResultList(list: List<SearchPicturesPresenterEntity>)
    fun setWallpaperImageList(list: List<ImagePresenterEntity>)
    fun addToSearchResultList(list: List<SearchPicturesPresenterEntity>)
    fun onBindRepositoryRowViewAtPosition(position: Int, rowView: ImageRecyclerItemView)
    fun getItemCount(): Int
    fun clearAll()
  }
}