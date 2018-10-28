package zebrostudio.wallr100.presentation.wallpaper

import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

interface WallpaperContract {

  interface WallpaperView : BaseView {
    fun showLoader()
    fun showImages(imagePresenterEntityList: List<ImagePresenterEntity>)
    fun hideLoader()
    fun hideAllLoadersAndMessageViews()
    fun showGenericErrorMessageView()
  }

  interface WallpaperPresenter : BasePresenter<WallpaperView>

}