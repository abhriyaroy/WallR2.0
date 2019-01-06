package zebrostudio.wallr100.presentation.detail

import android.content.Intent
import android.content.pm.PackageManager
import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.domain.interactor.ShareImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.SEARCH
import zebrostudio.wallr100.presentation.detail.ActionType.*
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

class DetailPresenterImpl(
  private var shareImagesUseCase: ShareImagesUseCase,
  private var userPremiumStatusUseCase: UserPremiumStatusUseCase
) : DetailContract.DetailPresenter {

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
    decorateView()
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
    if (detailView?.isInternetAvailable() == true) {
      if (userPremiumStatusUseCase.isUserPremium()) {
        val link = if (imageType == SEARCH) {
          searchImage.imageQualityUrlPresenterEntity.largeImageLink
        } else {
          wallpaperImage.imageLink.large
        }
        shareImagesUseCase.getImageShareableLink(link)
            .autoDisposable(detailView?.getScope()!!)
            .subscribe({
              detailView?.hideWaitLoader()
              detailView?.shareLink(it)
            }, {
              detailView?.showGenericErrorMessage()
            })
      } else {
        detailView?.redirectToBuyPro(SHARE.ordinal)
      }
    } else {
      detailView?.showNoInternetToShareError()
    }
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

  override fun notifyActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == QUICK_SET.ordinal) {
      notifyQuickSetClick()
    } else if (requestCode == DOWNLOAD.ordinal) {
      notifyDownloadClick()
    } else if (requestCode == CRYSTALLIZE.ordinal) {
      notifyCrystallizeClick()
    } else if (requestCode == EDIT_SET.ordinal) {
      notifyEditSetClick()
    } else if (requestCode == ADD_TO_COLLECTION.ordinal) {
      notifyAddToCollectionClick()
    } else if (requestCode == SHARE.ordinal) {
      notifyShareClick()
    }
  }

  private fun decorateView() {
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

  private fun quickSetWallpaper() {

  }

  private fun downloadWallpaper() {

  }

  private fun crystallizeWallpaper() {

  }

  private fun editSetWallpaper() {

  }

  private fun addWallpaperToCollection() {

  }
}

enum class ActionType {
  QUICK_SET,
  DOWNLOAD,
  CRYSTALLIZE,
  EDIT_SET,
  ADD_TO_COLLECTION,
  SHARE
}