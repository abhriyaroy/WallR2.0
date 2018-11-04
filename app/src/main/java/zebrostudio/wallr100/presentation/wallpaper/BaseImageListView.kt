package zebrostudio.wallr100.presentation.wallpaper

import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

interface BaseImageListView : BaseView {
  fun showLoader()
  fun hideLoader()
  fun showNoInternetMessageView()
  fun showImageList(list: List<ImagePresenterEntity>)
  fun hideRefreshing()
  fun hideAllLoadersAndMessageViews()
}