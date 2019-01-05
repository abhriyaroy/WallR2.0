package zebrostudio.wallr100.presentation.detail

import android.content.pm.PackageManager
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.SEARCH
import zebrostudio.wallr100.presentation.detail.ActionType.*
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

class DetailPresenterImpl : DetailContract.DetailPresenter {

  private var detailView: DetailContract.DetailView? = null
  private lateinit var imageType: ImageListType
  private lateinit var wallpaperImage: ImagePresenterEntity
  private lateinit var searchImage: SearchPicturesPresenterEntity

  override fun attachView(view: DetailContract.DetailView) {
    detailView = view
  }

  override fun detachView() {
    detailView = null
  }

  override fun setImageType(imageType: ImageListType) {
    this.imageType = imageType
    if (imageType == SEARCH) {
      searchImage = detailView?.getSearchImageDetails()!!
    } else {
      wallpaperImage = detailView?.getWallpaperImageDetails()!!
    }
    decorateScreen()
  }

  override fun notifyHighQualityImageLoadFailed() {
    detailView?.showImageLoadError()
  }

  override fun notifyQuickSetClick() {
    if (detailView?.hasStoragePermission() == true) {
      quickSetWallpaper()
    } else {
      detailView?.requestStoragePermission(QUICK_SET)
    }
  }

  override fun notifyDownloadClick() {
    if (detailView?.hasStoragePermission() == true) {
      downloadWallpaper()
    } else {
      detailView?.requestStoragePermission(DOWNLOAD)
    }
  }

  override fun notifyCrystallizeClick() {
    if (detailView?.hasStoragePermission() == true) {
      crystallizeWallpaper()
    } else {
      detailView?.requestStoragePermission(CRYSTALLIZE)
    }
  }

  override fun notifyEditSetClick() {
    if (detailView?.hasStoragePermission() == true) {
      editSetWallpaper()
    } else {
      detailView?.requestStoragePermission(EDIT_SET)
    }
  }

  override fun notifyAddToCollectionClick() {
    if (detailView?.hasStoragePermission() == true) {
      addWallpaperToCollection()
    } else {
      detailView?.requestStoragePermission(ADD_TO_COLLECTION)
    }
  }

  override fun notifyShareClick() {

  }

  override fun quickSetWallpaper() {

  }

  override fun downloadWallpaper() {

  }

  override fun crystallizeWallpaper() {

  }

  override fun editSetWallpaper() {

  }

  override fun addWallpaperToCollection() {

  }

  override fun notifyPermissionRequestResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    if (requestCode == QUICK_SET.ordinal ||
        requestCode == DOWNLOAD.ordinal ||
        requestCode == CRYSTALLIZE.ordinal ||
        requestCode == EDIT_SET.ordinal ||
        requestCode == ADD_TO_COLLECTION.ordinal
    ) {
      if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
        handlePermissionGranted(requestCode)
      } else {
        detailView?.showPermissionRequiredMessage()
      }
    }
  }

  private fun decorateScreen() {
    if (imageType == SEARCH) {
      detailView?.setAuthorDetails(searchImage.userPresenterEntity.name,
          searchImage.userPresenterEntity.profileImageLink)
      detailView?.showImage(searchImage.imageQualityUrlPresenterEntity.smallImageLink,
          searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    } else {
      detailView?.setAuthorDetails(wallpaperImage.author.name,
          wallpaperImage.author.profileImageLink)
      detailView?.showImage(wallpaperImage.imageLink.thumb, wallpaperImage.imageLink.large)
    }
  }

  private fun handlePermissionGranted(requestCode: Int) {
    when (requestCode) {
      QUICK_SET.ordinal -> quickSetWallpaper()
      DOWNLOAD.ordinal -> downloadWallpaper()
      CRYSTALLIZE.ordinal -> crystallizeWallpaper()
      EDIT_SET.ordinal -> editSetWallpaper()
      ADD_TO_COLLECTION.ordinal -> addWallpaperToCollection()
    }
  }
}

enum class ActionType {
  QUICK_SET,
  DOWNLOAD,
  CRYSTALLIZE,
  EDIT_SET,
  ADD_TO_COLLECTION
}