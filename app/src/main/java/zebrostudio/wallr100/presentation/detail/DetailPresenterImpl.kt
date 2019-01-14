package zebrostudio.wallr100.presentation.detail

import android.content.Intent
import android.content.pm.PackageManager
import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.SEARCH
import zebrostudio.wallr100.presentation.detail.ActionType.*
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

class DetailPresenterImpl(
  private var imageOptionsUseCase: ImageOptionsUseCase,
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

  override fun handleHighQualityImageLoadFailed() {
    detailView?.showImageLoadError()
  }

  override fun handleQuickSetClick() {
    if (detailView?.hasStoragePermission() == true) {
      quickSetWallpaper()
    } else {
      detailView?.requestStoragePermission(QUICK_SET)
    }
  }

  override fun handleDownloadClick() {
    if (detailView?.hasStoragePermission() == true) {
      downloadWallpaper()
    } else {
      detailView?.requestStoragePermission(DOWNLOAD)
    }
  }

  override fun handleCrystallizeClick() {
    if (detailView?.hasStoragePermission() == true) {
      crystallizeWallpaper()
    } else {
      detailView?.requestStoragePermission(CRYSTALLIZE)
    }
  }

  override fun handleEditSetClick() {
    if (detailView?.hasStoragePermission() == true) {
      editSetWallpaper()
    } else {
      detailView?.requestStoragePermission(EDIT_SET)
    }
  }

  override fun handleAddToCollectionClick() {
    if (detailView?.hasStoragePermission() == true) {
      addWallpaperToCollection()
    } else {
      detailView?.requestStoragePermission(ADD_TO_COLLECTION)
    }
  }

  override fun handleShareClick() {
    if (detailView?.internetAvailability() == true) {
      if (userPremiumStatusUseCase.isUserPremium()) {
        val link = if (imageType == SEARCH) {
          searchImage.imageQualityUrlPresenterEntity.largeImageLink
        } else {
          wallpaperImage.imageLink.large
        }
        imageOptionsUseCase.getImageShareableLinkSingle(link)
            .autoDisposable(detailView?.getScope()!!)
            .subscribe({
              detailView?.hideWaitLoader()
              detailView?.shareLink(it)
            }, {
              detailView?.hideWaitLoader()
              detailView?.showGenericErrorMessage()
            })
      } else {
        detailView?.redirectToBuyPro(SHARE.ordinal)
      }
    } else {
      detailView?.showNoInternetToShareError()
    }
  }

  override fun handlePermissionRequestResult(
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

  override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == QUICK_SET.ordinal) {
      handleQuickSetClick()
    } else if (requestCode == DOWNLOAD.ordinal) {
      handleDownloadClick()
    } else if (requestCode == CRYSTALLIZE.ordinal) {
      handleCrystallizeClick()
    } else if (requestCode == EDIT_SET.ordinal) {
      handleEditSetClick()
    } else if (requestCode == ADD_TO_COLLECTION.ordinal) {
      handleAddToCollectionClick()
    } else if (requestCode == SHARE.ordinal) {
      handleShareClick()
    }
  }

  private fun decorateView() {
    if (imageType == SEARCH) {
      detailView?.showAuthorDetails(searchImage.userPresenterEntity.name,
          searchImage.userPresenterEntity.profileImageLink)
      detailView?.showImage(searchImage.imageQualityUrlPresenterEntity.smallImageLink,
          searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    } else {
      detailView?.showAuthorDetails(wallpaperImage.author.name,
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
    // To be implemented later
  }

  private fun downloadWallpaper() {
    // To be implemented later
  }

  private fun crystallizeWallpaper() {
    // To be implemented later
  }

  private fun editSetWallpaper() {
    // To be implemented later
  }

  private fun addWallpaperToCollection() {
    // To be implemented later
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