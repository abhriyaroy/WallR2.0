package zebrostudio.wallr100.presentation.detail.images

import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import com.uber.autodispose.autoDisposable
import com.yalantis.ucrop.UCrop.REQUEST_CROP
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.permissions.PermissionsChecker
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig
import zebrostudio.wallr100.android.ui.detail.colors.WALLR_DOWNLOAD_LINK
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.data.exception.AlreadyPresentInCollectionException
import zebrostudio.wallr100.data.exception.ImageDownloadException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType.*
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.SEARCH
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.WALLPAPERS
import zebrostudio.wallr100.presentation.detail.images.ActionType.*
import zebrostudio.wallr100.presentation.detail.images.DetailContract.DetailPresenter
import zebrostudio.wallr100.presentation.detail.images.mapper.ImageDownloadPresenterEntityMapper
import zebrostudio.wallr100.presentation.detail.images.model.ImageDownloadPresenterEntity
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

const val DOWNLOAD_COMPLETED_VALUE: Long = 100
const val PROGRESS_VALUE_99: Long = 99
const val DOWNLOAD_STARTED_VALUE: Long = 0
const val ILLEGAL_STATE_EXCEPTION_MESSAGE = "Activity is not invoked using getCallingIntent method"
private const val SHARE_INTENT_TYPE = "text/plain"

class DetailPresenterImpl(
  private val resourceUtils: ResourceUtils,
  private val imageOptionsUseCase: ImageOptionsUseCase,
  private var userPremiumStatusUseCase: UserPremiumStatusUseCase,
  private val wallpaperSetter: WallpaperSetter,
  private val postExecutionThread: PostExecutionThread,
  private val imageDownloadPresenterEntityMapper: ImageDownloadPresenterEntityMapper,
  private val permissionsChecker: PermissionsChecker
) : DetailPresenter {

  internal lateinit var imageType: ImageListType
  internal lateinit var wallpaperImage: ImagePresenterEntity
  internal lateinit var searchImage: SearchPicturesPresenterEntity
  internal var isDownloadInProgress = false
  internal var isImageOperationInProgress = false
  internal var wallpaperHasBeenSet = false
  internal var isSlidingPanelExpanded = false
  internal var imageHasBeenCrystallized = false
  internal var imageHasBeenEdited = false
  internal var lastImageOperationType = WALLPAPER
  private var downloadProgress: Long = 0
  private var detailView: DetailContract.DetailView? = null
  private var cropDestinationUri : Uri?=null

  override fun attachView(view: DetailContract.DetailView) {
    detailView = view
  }

  override fun detachView() {
    imageHasBeenCrystallized = false
    detailView = null
  }

  override fun setImageType(imageTypeOrdinal: Int) {
    if (imageTypeOrdinal == SEARCH.ordinal) {
      imageType = SEARCH
      searchImage = detailView!!.getSearchImageDetails()
      lastImageOperationType = CollectionsImageType.SEARCH
    } else {
      imageType = WALLPAPERS
      wallpaperImage = detailView!!.getWallpaperImageDetails()
      lastImageOperationType = WALLPAPER
    }
    decorateView()
  }

  override fun handleHighQualityImageLoadFailed() {
    detailView?.showImageLoadError()
  }

  override fun handleQuickSetClick() {
    if (hasStoragePermissions(QUICK_SET) && isInternetAvailable()) {
      quickSetWallpaper()
    }
  }

  override fun handleDownloadClick() {
    if (isUserPremium(DOWNLOAD) && hasStoragePermissions(DOWNLOAD) && isInternetAvailable()) {
      downloadWallpaper()
    }
  }

  override fun handleCrystallizeClick() {
    if (isUserPremium(CRYSTALLIZE) && hasStoragePermissions(CRYSTALLIZE) && isInternetAvailable()) {
      if (imageOptionsUseCase.isCrystallizeDescriptionDialogShown()) {
        if (!imageHasBeenCrystallized) {
          crystallizeWallpaper()
        } else {
          detailView?.showImageHasAlreadyBeenCrystallizedMessage()
        }
      } else {
        detailView?.showCrystallizeDescriptionDialog()
      }
    }
  }

  override fun handleEditSetClick() {
    if (isInternetAvailable() && hasStoragePermissions(EDIT_SET)) {
      editSetWallpaper()
    }
  }

  override fun handleAddToCollectionClick() {
    if (isUserPremium(ADD_TO_COLLECTION) && hasStoragePermissions(
          ADD_TO_COLLECTION
        ) && isInternetAvailable()
    ) {
      addWallpaperToCollection()
    }
  }

  override fun handleShareClick() {
    if (detailView?.internetAvailability() == true) {
      if (isUserPremium(SHARE)) {
        val link = if (imageType == SEARCH) {
          searchImage.imageQualityUrlPresenterEntity.largeImageLink
        } else {
          wallpaperImage.imageLink.large
        }
        imageOptionsUseCase.getImageShareableLinkSingle(link)
            .observeOn(postExecutionThread.scheduler)
            .autoDisposable(detailView?.getScope()!!)
            .subscribe({
              detailView?.hideWaitLoader()
              val message = "${resourceUtils.getStringResource(
                R.string.share_intent_message
              )} $WALLR_DOWNLOAD_LINK \n\n Image link - $it"
              detailView?.shareLink(message, SHARE_INTENT_TYPE)
            }, {
              detailView?.hideWaitLoader()
              detailView?.showGenericErrorMessage()
            })
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
            .observeOn(postExecutionThread.scheduler)
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
        detailView?.showPermissionRequiredRationale()
      }
    }
  }

  override fun handleViewResult(
    requestCode: Int,
    resultCode: Int
  ) {
    println("request code is $requestCode $resultCode")
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
      handleCropResult()
    } else {
      isDownloadInProgress = false
      isImageOperationInProgress = false
      detailView?.hideScreenBlur()
    }
  }

  override fun handleDownloadQualitySelectionEvent(
    downloadType: ImageListType,
    selectedIndex: Int
  ) {
    if (selectedIndex < 5) {
      val downloadLink = getDownloadLink(downloadType, selectedIndex)
      if (imageOptionsUseCase.isDownloadInProgress(downloadLink)) {
        detailView?.showDownloadAlreadyInProgressMessage()
      } else {
        imageOptionsUseCase.downloadImageCompletable(downloadLink)
            .observeOn(postExecutionThread.scheduler)
            .doOnSubscribe {
              detailView?.showDownloadStartedMessage()
            }
            .autoDisposable(detailView?.getScope()!!)
            .subscribe({
              detailView?.showDownloadCompletedSuccessMessage()
            }, {
              it.printStackTrace()
              detailView?.showGenericErrorMessage()
            })
      }
    } else {
      imageOptionsUseCase.downloadCrystallizedImageCompletable()
          .observeOn(postExecutionThread.scheduler)
          .doOnSubscribe {
            detailView?.blurScreen()
            detailView?.showIndefiniteLoader(
              resourceUtils.getStringResource(
                R.string.crystallizing_wallpaper_wait_message
              )
            )
          }
          .autoDisposable(detailView?.getScope()!!)
          .subscribe({
            detailView?.hideScreenBlur()
            detailView?.showDownloadCompletedSuccessMessage()
          }, {
            it.printStackTrace()
            detailView?.hideScreenBlur()
            detailView?.showGenericErrorMessage()
          })
    }
  }

  override fun handleCrystallizeDialogPositiveClick() {
    imageOptionsUseCase.setCrystallizeDescriptionShownOnce()
    handleCrystallizeClick()
  }

  override fun handleImageViewClicked() {
    if (isSlidingPanelExpanded) {
      detailView?.collapseSlidingPanel()
    } else {
      if (lastImageOperationType == CRYSTALLIZED) {
        detailView?.showCrystallizedExpandedImage()
      } else if (lastImageOperationType == EDITED) {
        detailView?.showEditedExpandedImage()
      } else {
        if (imageType == SEARCH) {
          detailView?.showExpandedImage(
            searchImage.imageQualityUrlPresenterEntity.smallImageLink,
            searchImage.imageQualityUrlPresenterEntity.largeImageLink
          )
        } else {
          detailView?.showExpandedImage(
            wallpaperImage.imageLink.thumb,
            wallpaperImage.imageLink.large
          )
        }
      }
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
      detailView?.showAuthorDetails(
        searchImage.userPresenterEntity.name,
        searchImage.userPresenterEntity.profileImageLink
      )
      detailView?.showImage(
        searchImage.imageQualityUrlPresenterEntity.smallImageLink,
        searchImage.imageQualityUrlPresenterEntity.largeImageLink
      )
    } else {
      detailView?.showAuthorDetails(
        wallpaperImage.author.name,
        wallpaperImage.author.profileImageLink
      )
      detailView?.showImage(wallpaperImage.imageLink.thumb, wallpaperImage.imageLink.large)
    }
  }

  private fun isUserPremium(actionType: ActionType): Boolean {
    if (userPremiumStatusUseCase.isUserPremium()) {
      return true
    } else {
      detailView?.redirectToBuyPro(actionType.ordinal)
    }
    return false
  }

  private fun hasStoragePermissions(actionType: ActionType): Boolean {
    if (permissionsChecker.isReadPermissionAvailable() && permissionsChecker.isWritePermissionAvailable()) {
      return true
    } else {
      detailView?.requestStoragePermission(actionType)
    }
    return false
  }

  private fun isInternetAvailable(): Boolean {
    if (detailView?.internetAvailability() == true) {
      return true
    } else {
      detailView?.showNoInternetError()
    }
    return false
  }

  private fun handlePermissionGranted(requestCode: Int) {
    when (requestCode) {
      QUICK_SET.ordinal -> handleQuickSetClick()
      DOWNLOAD.ordinal -> handleDownloadClick()
      CRYSTALLIZE.ordinal -> handleCrystallizeClick()
      EDIT_SET.ordinal -> handleEditSetClick()
      ADD_TO_COLLECTION.ordinal -> handleAddToCollectionClick()
    }
  }

  private fun quickSetWallpaper() {
    performPreImageFetchingRituals()
    getImageBitmapFetchObservable()
        .doOnNext {
          if (it.progress == DOWNLOAD_COMPLETED_VALUE) {
            wallpaperHasBeenSet = wallpaperSetter.setWallpaper(it.imageBitmap)
          }
        }
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(detailView?.getScope()!!)
        .subscribe(object : Observer<ImageDownloadPresenterEntity> {
          override fun onComplete() {
            isDownloadInProgress = false
          }

          override fun onSubscribe(d: Disposable) {
            isDownloadInProgress = true
            wallpaperHasBeenSet = false
          }

          override fun onNext(it: ImageDownloadPresenterEntity) {
            handleQuickSetWallpaperOnNext(it)
          }

          override fun onError(throwable: Throwable) {
            handleQuickSetWallpaperOnError(throwable)
          }
        })
  }

  private fun handleQuickSetWallpaperOnNext(it: ImageDownloadPresenterEntity) {
    val progress = it.progress
    if (progress == PROGRESS_VALUE_99) {
      isDownloadInProgress = false
      isImageOperationInProgress = true
      detailView?.updateProgressPercentage("$DOWNLOAD_COMPLETED_VALUE%")
      resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage)
          .let {
            detailView?.showIndefiniteLoaderWithAnimation(it)
          }
    } else if (progress == DOWNLOAD_COMPLETED_VALUE) {
      resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage)
          .let {
            detailView?.showIndefiniteLoader(it)
          }
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

  private fun handleQuickSetWallpaperOnError(throwable: Throwable) {
    throwable.printStackTrace()
    if (throwable is ImageDownloadException) {
      detailView?.showUnableToDownloadErrorMessage()
    } else {
      detailView?.showGenericErrorMessage()
    }
    detailView?.hideScreenBlur()
    resetImageOperationFlags()
  }

  private fun downloadWallpaper() {
    if (imageType == SEARCH) {
      if (imageHasBeenCrystallized) {
        detailView?.showSearchTypeDownloadDialog(true)
      } else {
        detailView?.showSearchTypeDownloadDialog(false)
      }
    } else {
      if (imageHasBeenCrystallized) {
        detailView?.showWallpaperTypeDownloadDialog(true)
      } else {
        detailView?.showWallpaperTypeDownloadDialog(false)
      }
    }
  }

  private fun crystallizeWallpaper() {
    performPreImageFetchingRituals()
    getImageBitmapFetchObservable()
        .observeOn(postExecutionThread.scheduler)
        .doOnNext {
          handleCrystallizeWallpaperDoOnNext(it)
        }
        .doOnSubscribe {
          isDownloadInProgress = true
          wallpaperHasBeenSet = false
        }
        .flatMapSingle {
          if (it.progress == DOWNLOAD_COMPLETED_VALUE) {
            imageOptionsUseCase.crystallizeImageSingle()
                .observeOn(postExecutionThread.scheduler)
          } else {
            Single.just(Pair(false, null))
          }
        }
        .autoDisposable(detailView?.getScope()!!)
        .subscribe({
          handleCrystallizeWallpaperOnSuccess(it)
        }, {
          handleCrystallizeWallpaperOnError(it)
        })

  }

  private fun handleCrystallizeWallpaperDoOnNext(it: ImageDownloadPresenterEntity) {
    val progress = it.progress
    if (progress == PROGRESS_VALUE_99) {
      isDownloadInProgress = false
      isImageOperationInProgress = true
      detailView?.updateProgressPercentage("$DOWNLOAD_COMPLETED_VALUE%")
      val message =
          resourceUtils.getStringResource(
            R.string.detail_activity_crystallizing_wallpaper_message
          )
      detailView?.showIndefiniteLoaderWithAnimation(message)
    } else if (progress != DOWNLOAD_COMPLETED_VALUE) {
      detailView?.updateProgressPercentage("$progress%")
    }
  }

  private fun handleCrystallizeWallpaperOnSuccess(it: Pair<Boolean, Bitmap?>) {
    if (it.first) {
      lastImageOperationType = CRYSTALLIZED
      detailView?.showImage(it.second!!)
      detailView?.hideScreenBlur()
      detailView?.showCrystallizeSuccessMessage()
      imageHasBeenCrystallized = true
      isImageOperationInProgress = false
    }
  }

  private fun handleCrystallizeWallpaperOnError(throwable: Throwable) {
    if (throwable is ImageDownloadException) {
      detailView?.showUnableToDownloadErrorMessage()
    } else {
      detailView?.showGenericErrorMessage()
    }
    detailView?.hideScreenBlur()
    resetImageOperationFlags()
  }

  private fun editSetWallpaper() {
    performPreImageFetchingRituals()
    getImageBitmapFetchObservable()
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(detailView?.getScope()!!)
        .subscribe(object : Observer<ImageDownloadPresenterEntity> {
          override fun onComplete() {
            isDownloadInProgress = false
          }

          override fun onSubscribe(d: Disposable) {
            isDownloadInProgress = true
            wallpaperHasBeenSet = false
          }

          override fun onNext(it: ImageDownloadPresenterEntity) {
            handleEditSetWallpaperOnNext(it)
          }

          override fun onError(throwable: Throwable) {
            throwable.printStackTrace()
            handleEditSetWallpaperOnError(throwable)
          }
        })
  }

  private fun handleEditSetWallpaperOnNext(it: ImageDownloadPresenterEntity) {
    val progress = it.progress
    if (progress == DOWNLOAD_COMPLETED_VALUE) {
     openCropView()
    } else if (progress == PROGRESS_VALUE_99) {
      isDownloadInProgress = false
      isImageOperationInProgress = true
      detailView?.updateProgressPercentage("$DOWNLOAD_COMPLETED_VALUE%")
      val message =
          resourceUtils.getStringResource(R.string.detail_activity_editing_tool_message)
      detailView?.showIndefiniteLoaderWithAnimation(message)
    } else {
      isDownloadInProgress = true
      detailView?.updateProgressPercentage("$progress%")
    }
  }

  private fun openCropView(){
    imageOptionsUseCase.getCroppingDestinationUri()
            .flatMap {
              cropDestinationUri = it
              imageOptionsUseCase.getCroppingSourceUri()
            }
            .observeOn(postExecutionThread.scheduler)
            .autoDisposable(detailView!!.getScope())
            .subscribe({
              detailView?.startCroppingActivity(
                      it,
                      cropDestinationUri!!,
                      wallpaperSetter.getDesiredMinimumWidth(),
                      wallpaperSetter.getDesiredMinimumHeight()
              )
            },{
              detailView?.showGenericErrorMessage()
            })
  }

  private fun handleEditSetWallpaperOnError(throwable: Throwable) {
    if (throwable is ImageDownloadException) {
      detailView?.showUnableToDownloadErrorMessage()
    } else {
      detailView?.showGenericErrorMessage()
    }
    detailView?.hideScreenBlur()
    resetImageOperationFlags()
  }

  private fun addWallpaperToCollection() {
    performPreImageFetchingRituals()
    getImageBitmapFetchObservable()
        .observeOn(postExecutionThread.scheduler)
        .doOnNext {
          handleAddWallpaperToCollectionDoOnNext(it)
        }
        .doOnSubscribe {
          isDownloadInProgress = true
        }
        .flatMapSingle {
          if (it.progress == DOWNLOAD_COMPLETED_VALUE) {
            imageOptionsUseCase.addImageToCollection(getImageFetchingLink(), lastImageOperationType)
                .andThen(
                  Single.just(true)
                )
                .observeOn(postExecutionThread.scheduler)
          } else {
            Single.just(false)
          }
        }
        .autoDisposable(detailView?.getScope()!!)
        .subscribe({
          handleAddWallpaperToCollectionSuccess(it)
        }, {
          handleAddWallpaperToCollectionError(it)
        })
  }

  private fun handleAddWallpaperToCollectionDoOnNext(it: ImageDownloadPresenterEntity) {
    val progress = it.progress
    if (progress == PROGRESS_VALUE_99) {
      isDownloadInProgress = false
      isImageOperationInProgress = true
      detailView?.updateProgressPercentage("$DOWNLOAD_COMPLETED_VALUE%")
      val message =
          resourceUtils.getStringResource(R.string.adding_image_to_collections_message)
      detailView?.showIndefiniteLoaderWithAnimation(message)
    } else if (progress != DOWNLOAD_COMPLETED_VALUE) {
      detailView?.updateProgressPercentage("$progress%")
    }
  }

  private fun handleAddWallpaperToCollectionSuccess(it: Boolean) {
    if (it) {
      detailView?.hideScreenBlur()
      detailView?.showAddToCollectionSuccessMessage()
      isImageOperationInProgress = false
    }
  }

  private fun handleAddWallpaperToCollectionError(error: Throwable) {
    if (error is ImageDownloadException) {
      detailView?.showUnableToDownloadErrorMessage()
    } else if (error is AlreadyPresentInCollectionException) {
      detailView?.showAlreadyPresentInCollectionErrorMessage()
    } else {
      detailView?.showGenericErrorMessage()
    }
    detailView?.hideScreenBlur()
    resetImageOperationFlags()
  }

  private fun handleCropResult() {
    var hasWallpaperBeenSet = false
    detailView?.blurScreen()
    detailView?.showIndefiniteLoader(
      resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage)
    )
    imageOptionsUseCase.getBitmapFromUriSingle(cropDestinationUri)
        .doOnSuccess {
          hasWallpaperBeenSet = wallpaperSetter.setWallpaper(it)
        }
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(detailView?.getScope()!!)
        .subscribe({
          lastImageOperationType = EDITED
          if (hasWallpaperBeenSet) {
            detailView?.showImage(it)
            detailView?.showWallpaperSetSuccessMessage()
            imageHasBeenEdited = true
          } else {
            detailView?.showWallpaperSetErrorMessage()
          }
          isImageOperationInProgress = false
          detailView?.hideScreenBlur()
        }, {
          it.printStackTrace()
          detailView?.showGenericErrorMessage()
          resetImageOperationFlags()
          detailView?.hideScreenBlur()
        })
  }

  private fun getDownloadLink(
    downloadType: ImageListType,
    selectedIndex: Int
  ): String {
    return if (downloadType == SEARCH) {
      if (selectedIndex == 0) {
        searchImage.imageQualityUrlPresenterEntity.rawImageLink
      } else if (selectedIndex == 1) {
        searchImage.imageQualityUrlPresenterEntity.largeImageLink
      } else if (selectedIndex == 2) {
        searchImage.imageQualityUrlPresenterEntity.regularImageLink
      } else if (selectedIndex == 3) {
        searchImage.imageQualityUrlPresenterEntity.thumbImageLink
      } else {
        searchImage.imageQualityUrlPresenterEntity.smallImageLink
      }
    } else {
      if (selectedIndex == 0) {
        wallpaperImage.imageLink.raw
      } else if (selectedIndex == 1) {
        wallpaperImage.imageLink.large
      } else if (selectedIndex == 2) {
        wallpaperImage.imageLink.medium
      } else if (selectedIndex == 3) {
        wallpaperImage.imageLink.thumb
      } else {
        wallpaperImage.imageLink.small
      }
    }
  }

  private fun performPreImageFetchingRituals() {
    downloadProgress = DOWNLOAD_STARTED_VALUE
    detailView?.hideIndefiniteLoader()
    detailView?.blurScreenAndInitializeProgressPercentage()
  }

  private fun getImageFetchingLink(): String {
    return when (imageType) {
      SEARCH -> searchImage.imageQualityUrlPresenterEntity.largeImageLink
      else -> wallpaperImage.imageLink.large
    }
  }

  private fun getImageBitmapFetchObservable(): Observable<ImageDownloadPresenterEntity> {
    return imageOptionsUseCase.fetchImageBitmapObservable(getImageFetchingLink())
        .map {
          imageDownloadPresenterEntityMapper.mapToPresenterEntity(it)
        }
  }

  private fun resetImageOperationFlags() {
    isImageOperationInProgress = false
    isDownloadInProgress = false
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
