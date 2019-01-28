package zebrostudio.wallr100.presentation.detail

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.uber.autodispose.autoDisposable
import com.yalantis.ucrop.UCrop.REQUEST_CROP
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.data.exception.ImageDownloadException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.SEARCH
import zebrostudio.wallr100.presentation.detail.ActionType.*
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity
import android.net.Uri
import java.lang.Exception

class DetailPresenterImpl(
  private var context: Context,
  private var imageOptionsUseCase: ImageOptionsUseCase,
  private var userPremiumStatusUseCase: UserPremiumStatusUseCase,
  private val wallpaperSetter: WallpaperSetter,
  private val postExecutionThread: PostExecutionThread
) : DetailContract.DetailPresenter {

  private var detailView: DetailContract.DetailView? = null
  internal lateinit var imageType: ImageListType
  internal lateinit var wallpaperImage: ImagePresenterEntity
  internal lateinit var searchImage: SearchPicturesPresenterEntity
  private val downloadCompletedValue: Long = 100
  private val showIndefiniteLoaderAtProgressValue: Long = 99
  private val downloadStartedValue: Long = 0
  private var downloadProgress: Long = 0
  internal var isDownloadInProgress: Boolean = false
  internal var isImageOperationInProgress: Boolean = false
  internal var wallpaperHasBeenSet: Boolean = false
  internal var isSlidingPanelExpanded: Boolean = false

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
      if (detailView?.internetAvailability() == true) {
        quickSetWallpaper()
      } else {
        detailView?.showNoInternetError()
      }
    } else {
      detailView?.requestStoragePermission(QUICK_SET)
    }
  }

  override fun handleDownloadClick() {
    if (userPremiumStatusUseCase.isUserPremium()) {
      if (detailView?.hasStoragePermission() == true) {
        if (detailView?.internetAvailability() == true) {
          downloadWallpaper()
        } else {
          detailView?.showNoInternetError()
        }
      } else {
        detailView?.requestStoragePermission(DOWNLOAD)
      }
    } else {
      detailView?.redirectToBuyPro(DOWNLOAD.ordinal)
    }
  }

  override fun handleCrystallizeClick() {
    if (userPremiumStatusUseCase.isUserPremium()) {
      if (detailView?.hasStoragePermission() == true) {
        if (detailView?.internetAvailability() == true) {
          crystallizeWallpaper()
        } else {
          detailView?.showNoInternetError()
        }
      } else {
        detailView?.requestStoragePermission(CRYSTALLIZE)
      }
    } else {
      detailView?.redirectToBuyPro(CRYSTALLIZE.ordinal)
    }
  }

  override fun handleEditSetClick() {
    if (detailView?.hasStoragePermission() == true) {
      if (detailView?.internetAvailability() == true) {
        editSetWallpaper()
      } else {
        detailView?.showNoInternetError()
      }
    } else {
      detailView?.requestStoragePermission(EDIT_SET)
    }
  }

  override fun handleAddToCollectionClick() {
    if (userPremiumStatusUseCase.isUserPremium()) {
      if (detailView?.hasStoragePermission() == true) {
        if (detailView?.internetAvailability() == true) {
          addWallpaperToCollection()
        } else {
          detailView?.showNoInternetError()
        }
      } else {
        detailView?.requestStoragePermission(ADD_TO_COLLECTION)
      }
    } else {
      detailView?.redirectToBuyPro(ADD_TO_COLLECTION.ordinal)
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

  override fun handleBackButtonClick() {
    if (isDownloadInProgress) {
      imageOptionsUseCase.cancelFetchImageOperation()
      isDownloadInProgress = false
      detailView?.hideScreenBlur()
      detailView?.showDownloadWallpaperCancelledMessage()
    } else if (isImageOperationInProgress) {
      detailView?.showWallpaperOperationInProgressWaitMessage()
    } else {
      if (isSlidingPanelExpanded) {
        detailView?.collapseSlidingPanel()
      } else {
        imageOptionsUseCase.clearCachesCompletable()
            .autoDisposable(detailView?.getScope()!!)
            .subscribe({
              detailView?.exitView()
            }, {
              detailView?.exitView()
            })
      }
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

  override fun handleViewResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == DOWNLOAD.ordinal) {
      if (resultCode == PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE) {
        handleDownloadClick()
      } else {
        detailView?.showUnsuccessfulPurchaseError()
      }
    } else if (requestCode == CRYSTALLIZE.ordinal) {
      if (resultCode == PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE) {
        handleCrystallizeClick()
      } else {
        detailView?.showUnsuccessfulPurchaseError()
      }
    } else if (requestCode == ADD_TO_COLLECTION.ordinal) {
      if (resultCode == PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE) {
        handleAddToCollectionClick()
      } else {
        detailView?.showUnsuccessfulPurchaseError()
      }
    } else if (requestCode == SHARE.ordinal) {
      if (resultCode == PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE) {
        handleShareClick()
      } else {
        detailView?.showUnsuccessfulPurchaseError()
      }
    } else if (requestCode == REQUEST_CROP && resultCode == RESULT_OK) {
      detailView?.let {
        val cropResultUri = detailView?.getUriFromIntent(data!!)
        if (cropResultUri != null) {
          handleCropResult(cropResultUri)
        } else {
          detailView?.hideScreenBlur()
          detailView?.showGenericErrorMessage()
        }
      }
    } else {
      isDownloadInProgress = false
      isImageOperationInProgress = false
      detailView?.hideScreenBlur()
    }
  }

  override fun setPanelStateAsExpanded() {
    isSlidingPanelExpanded = true
  }

  override fun setPanelStateAsCollapsed() {
    isSlidingPanelExpanded = false
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
    downloadProgress = downloadStartedValue
    detailView?.hideIndefiniteLoader()
    detailView?.blurScreenAndInitializeProgressPercentage()
    val imageDownloadLink = when (imageType) {
      SEARCH -> searchImage.imageQualityUrlPresenterEntity.largeImageLink
      else -> wallpaperImage.imageLink.large
    }
    imageOptionsUseCase.fetchImageBitmapObservable(imageDownloadLink)
        .doOnNext {
          if (it.progress == downloadCompletedValue) {
            wallpaperHasBeenSet = wallpaperSetter.setWallpaper(it.imageBitmap)
          }
        }
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(detailView?.getScope()!!)
        .subscribe(object : Observer<ImageDownloadModel> {
          override fun onComplete() {
            isDownloadInProgress = false
          }

          override fun onSubscribe(d: Disposable) {
            isDownloadInProgress = true
            wallpaperHasBeenSet = false
          }

          override fun onNext(it: ImageDownloadModel) {
            val progress = it.progress
            if (progress == showIndefiniteLoaderAtProgressValue) {
              isDownloadInProgress = false
              isImageOperationInProgress = true
              detailView?.updateProgressPercentage("$downloadCompletedValue%")
              val message =
                  context.getString(R.string.detail_activity_finalizing_wallpaper_messsage)
              detailView?.showIndefiniteLoaderWithAnimation(message)
            } else if (progress == downloadCompletedValue) {
              val message =
                  context.getString(R.string.detail_activity_finalizing_wallpaper_messsage)
              detailView?.showIndefiniteLoader(message)
              if (wallpaperHasBeenSet) {
                detailView?.showWallpaperSetSuccessMessage()
              } else {
                detailView?.showWallpaperSetErrorMessage()
              }
              isImageOperationInProgress = false
              detailView?.hideScreenBlur()
            } else {
              detailView?.updateProgressPercentage("$progress%")
            }
          }

          override fun onError(throwable: Throwable) {
            if (throwable is ImageDownloadException) {
              detailView?.showUnableToDownloadErrorMessage()
            } else {
              detailView?.showGenericErrorMessage()
            }
            detailView?.hideScreenBlur()
          }
        })
  }

  private fun downloadWallpaper() {
    // To be implemented later
  }

  private fun crystallizeWallpaper() {
    // To be implemented later
  }

  private fun editSetWallpaper() {
    downloadProgress = downloadStartedValue
    detailView?.hideIndefiniteLoader()
    detailView?.blurScreenAndInitializeProgressPercentage()
    val imageDownloadLink = when (imageType) {
      SEARCH -> searchImage.imageQualityUrlPresenterEntity.largeImageLink
      else -> wallpaperImage.imageLink.large
    }
    imageOptionsUseCase.fetchImageBitmapObservable(imageDownloadLink)
        .doOnNext {
          if (it.progress == downloadCompletedValue) {
            detailView?.startCroppingActivity(
                imageOptionsUseCase.getCroppingSourceUri(),
                imageOptionsUseCase.getCroppingDestinationUri(),
                wallpaperSetter.getDesiredMinimumWidth(),
                wallpaperSetter.getDesiredMinimumHeight()
            )
          }
        }
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(detailView?.getScope()!!)
        .subscribe(object : Observer<ImageDownloadModel> {
          override fun onComplete() {
            isDownloadInProgress = false
          }

          override fun onSubscribe(d: Disposable) {
            isDownloadInProgress = true
            wallpaperHasBeenSet = false
          }

          override fun onNext(it: ImageDownloadModel) {
            val progress = it.progress
            if (progress == showIndefiniteLoaderAtProgressValue) {
              isDownloadInProgress = false
              isImageOperationInProgress = true
              detailView?.updateProgressPercentage("$downloadCompletedValue%")
              val message =
                  context.getString(R.string.detail_activity_editing_tool_message)
              detailView?.showIndefiniteLoaderWithAnimation(message)
            } else {
              detailView?.updateProgressPercentage("$progress%")
            }
          }

          override fun onError(throwable: Throwable) {
            if (throwable is ImageDownloadException) {
              detailView?.showUnableToDownloadErrorMessage()
            } else {
              detailView?.showGenericErrorMessage()
            }
            detailView?.hideScreenBlur()
          }

        })

  }

  private fun addWallpaperToCollection() {
    // To be implemented later
  }

  private fun handleCropResult(cropResultUri: Uri) {
    var hasWallpaperBeenSet = false
    detailView?.blurScreen()
    detailView?.showIndefiniteLoader(
        context.getString(R.string.detail_activity_finalizing_wallpaper_messsage))
    imageOptionsUseCase.getBitmapFromUriSingle(cropResultUri)
        .doOnSuccess {
          hasWallpaperBeenSet = wallpaperSetter.setWallpaper(it)
        }
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(detailView?.getScope()!!)
        .subscribe({
          if (hasWallpaperBeenSet) {
            detailView?.showImage(it)
            detailView?.showWallpaperSetSuccessMessage()
          } else {
            detailView?.showWallpaperSetErrorMessage()
          }
          isImageOperationInProgress = false
          detailView?.hideScreenBlur()
        }, {
          detailView?.showGenericErrorMessage()
          isImageOperationInProgress = false
          detailView?.hideScreenBlur()
        })
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