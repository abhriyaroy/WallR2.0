package zebrostudio.wallr100.presentation.detail

import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

interface DetailContract {

  interface DetailView : BaseView {
    fun getSearchImageDetails(): SearchPicturesPresenterEntity
    fun getWallpaperImageDetails(): ImagePresenterEntity
    fun setAuthorDetails(name: String, profileImageLink: String)
    fun showImage(lowQualityLink: String, highQualityLink: String)
  }

  interface DetailPresenter : BasePresenter<DetailView> {
    fun setImageType(imageType: ImageListType)
    fun notifyQuickSetClick()
    fun notifyDownloadClick()
    fun notifyCrystallizeClick()
    fun notifyEditSetClick()
    fun notifyAddToCollectionClick()
    fun notifyShareClick()
  }
}