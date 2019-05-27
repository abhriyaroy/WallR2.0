package zebrostudio.wallr100.presentation.detail.colors

import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import com.uber.autodispose.autoDisposable
import com.yalantis.ucrop.UCrop.REQUEST_CROP
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.permissions.PermissionsHelper
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig.Companion.PURCHASE_SUCCESSFUL_RESULT_CODE
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode.MULTIPLE
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode.SINGLE
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.data.exception.AlreadyPresentInCollectionException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ColorImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType.EDITED
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType.MINIMAL_COLOR
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType.ADD_TO_COLLECTION
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType.DOWNLOAD
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType.EDIT_SET
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType.LOAD_COLOR_WALLPAPER
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType.QUICK_SET
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType.SHARE
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailPresenter
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailView
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.GRADIENT
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.MATERIAL

const val FIRST_ELEMENT_POSITION = 0

class ColorsDetailPresenterImpl(
  private val resourceUtils: ResourceUtils,
  private val postExecutionThread: PostExecutionThread,
  private val userPremiumStatusUseCase: UserPremiumStatusUseCase,
  private val colorImagesUseCase: ColorImagesUseCase,
  private val wallpaperSetter: WallpaperSetter,
  private val permissionsHelper: PermissionsHelper
) : ColorsDetailPresenter {

  internal var colorsDetailMode: ColorsDetailMode = SINGLE
  internal var multiColorImageType: MultiColorImageType? = null
  internal var colorList = mutableListOf<String>()
  internal var isPanelExpanded: Boolean = false
  internal var areColorOperationsDisabled: Boolean = false
  internal var isColorWallpaperOperationActive: Boolean = false
  internal var lastImageOperationType = MINIMAL_COLOR
  private var view: ColorsDetailView? = null

  override fun attachView(view: ColorsDetailView) {
    this.view = view
  }

  override fun detachView() {
    view = null
  }

  override fun setColorsDetailMode(colorsDetailMode: ColorsDetailMode) {
    this.colorsDetailMode = colorsDetailMode
    if (colorsDetailMode == MULTIPLE) {
      multiColorImageType = view?.getMultiColorImageType()
    }
  }

  override fun setColorList(list: List<String>) {
    colorList = list.toMutableList()
  }

  override fun handleViewReadyState() {
    configureView()
  }

  override fun notifyPanelExpanded() {
    isPanelExpanded = true
  }

  override fun notifyPanelCollapsed() {
    isPanelExpanded = false
  }

  override fun handlePermissionRequestResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    if (requestCode == LOAD_COLOR_WALLPAPER.ordinal ||
        requestCode == QUICK_SET.ordinal ||
        requestCode == DOWNLOAD.ordinal ||
        requestCode == EDIT_SET.ordinal ||
        requestCode == ADD_TO_COLLECTION.ordinal ||
        requestCode == SHARE.ordinal
    ) {
      if ((grantResults.isNotEmpty() && grantResults[FIRST_ELEMENT_POSITION]
              == PERMISSION_GRANTED)) {
        handlePermissionGranted(requestCode)
      } else {
        if (requestCode == LOAD_COLOR_WALLPAPER.ordinal) {
          view?.exitView()
        }
        view?.showPermissionRequiredMessage()
      }
    }
  }

  override fun handleViewResult(requestCode: Int, resultCode: Int) {
    if (requestCode == DOWNLOAD.ordinal) {
      if (resultCode == PURCHASE_SUCCESSFUL_RESULT_CODE) {
        handleDownloadClick()
      } else {
        view?.showUnsuccessfulPurchaseError()
      }
    } else if (requestCode == ADD_TO_COLLECTION.ordinal) {
      if (resultCode == PURCHASE_SUCCESSFUL_RESULT_CODE) {
        handleAddToCollectionClick()
      } else {
        view?.showUnsuccessfulPurchaseError()
      }
    } else if (requestCode == SHARE.ordinal) {
      if (resultCode == PURCHASE_SUCCESSFUL_RESULT_CODE) {
        handleShareClick()
      } else {
        view?.showUnsuccessfulPurchaseError()
      }
    } else if (requestCode == REQUEST_CROP && resultCode == RESULT_OK) {
      handleCropResult(view?.getUriFromResultIntent())
    } else {
      view?.hideIndefiniteLoader()
    }
  }

  override fun handleImageViewClicked() {
    if (isPanelExpanded) {
      view?.collapsePanel()
    } else {
      view?.showFullScreenImage()
    }
  }

  override fun handleBackButtonClick() {
    if (isColorWallpaperOperationActive) {
      view?.showOperationInProgressWaitMessage()
    } else {
      if (isPanelExpanded) {
        view?.collapsePanel()
      } else {
        view?.exitView()
      }
    }
  }

  override fun handleQuickSetClick() {
    if (isNotInOperation() && hasStoragePermissions(QUICK_SET)) {
      colorImagesUseCase.getBitmapSingle()
          .doOnSuccess {
            wallpaperSetter.setWallpaper(it)
          }
          .observeOn(postExecutionThread.scheduler)
          .doOnSubscribe {
            isColorWallpaperOperationActive = true
            view?.showIndefiniteLoader(
                resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
          }
          .autoDisposable(view!!.getScope())
          .subscribe({
            isColorWallpaperOperationActive = false
            view?.hideIndefiniteLoader()
            view?.showWallpaperSetSuccessMessage()
          }, {
            isColorWallpaperOperationActive = false
            view?.hideIndefiniteLoader()
            view?.showWallpaperSetErrorMessage()
          })
    }
  }

  override fun handleDownloadClick() {
    if (isNotInOperation() && isUserPremium(DOWNLOAD) && hasStoragePermissions(DOWNLOAD)) {
      colorImagesUseCase.downloadImage()
          .observeOn(postExecutionThread.scheduler)
          .doOnSubscribe {
            view?.showIndefiniteLoader(resourceUtils.getStringResource(
                R.string.crystallizing_wallpaper_wait_message))
            isColorWallpaperOperationActive = true
          }
          .autoDisposable(view!!.getScope())
          .subscribe({
            view?.hideIndefiniteLoader()
            view?.showDownloadCompletedSuccessMessage()
            isColorWallpaperOperationActive = false
          }, {
            view?.hideIndefiniteLoader()
            view?.showGenericErrorMessage()
            isColorWallpaperOperationActive = false
          })
    }
  }

  override fun handleEditSetClick() {
    if (isNotInOperation() && hasStoragePermissions(EDIT_SET)) {
      isColorWallpaperOperationActive = true
      view?.showIndefiniteLoader(
          resourceUtils.getStringResource(R.string.detail_activity_editing_tool_message))
      Single.zip(colorImagesUseCase.getCroppingSourceUri(),
          colorImagesUseCase.getCroppingDestinationUri(),
          BiFunction<Uri, Uri, Pair<Uri, Uri>> { source, destination ->
            Pair(source, destination)
          })
          .observeOn(postExecutionThread.scheduler)
          .autoDisposable(view!!.getScope())
          .subscribe({
            view?.startCroppingActivity(
                it.first,
                it.second,
                wallpaperSetter.getDesiredMinimumWidth(),
                wallpaperSetter.getDesiredMinimumHeight())
            isColorWallpaperOperationActive = false
          }, {
            view?.showGenericErrorMessage()
          })
    }
  }

  override fun handleAddToCollectionClick() {
    if (isNotInOperation() && isUserPremium(ADD_TO_COLLECTION)
        && hasStoragePermissions(ADD_TO_COLLECTION)) {
      colorImagesUseCase.saveToCollectionsCompletable(colorList.toString(),
          lastImageOperationType)
          .observeOn(postExecutionThread.scheduler)
          .doOnSubscribe {
            view?.showIndefiniteLoader(
                resourceUtils.getStringResource(R.string.adding_image_to_collections_message))
            isColorWallpaperOperationActive = true
          }
          .autoDisposable(view!!.getScope())
          .subscribe({
            view?.showAddToCollectionSuccessMessage()
            view?.hideIndefiniteLoader()
            isColorWallpaperOperationActive = false
          }, {
            if (it is AlreadyPresentInCollectionException) {
              view?.showAlreadyPresentInCollectionErrorMessage()
            } else {
              view?.showGenericErrorMessage()
            }
            view?.hideIndefiniteLoader()
            isColorWallpaperOperationActive = false
          })
    }
  }

  override fun handleShareClick() {
    if (isNotInOperation() && isUserPremium(SHARE)
        && hasStoragePermissions(SHARE)) {
      colorImagesUseCase.getCacheImageUri()
          .observeOn(postExecutionThread.scheduler)
          .doOnSubscribe {
            isColorWallpaperOperationActive = true
            view?.showIndefiniteLoader(
                resourceUtils.getStringResource(R.string.preparing_shareable_wallpaper_message))
          }
          .autoDisposable(view!!.getScope())
          .subscribe({
            isColorWallpaperOperationActive = false
            view?.hideIndefiniteLoader()
            view?.showShareIntent(it)
          }, {
            isColorWallpaperOperationActive = false
            view?.hideIndefiniteLoader()
            view?.showGenericErrorMessage()
          })
    }
  }

  private fun configureView() {
    if (hasStoragePermissions(LOAD_COLOR_WALLPAPER)) {
      setImageTypeText()
      loadImage()
    }
  }

  private fun handlePermissionGranted(requestCode: Int) {
    when (requestCode) {
      LOAD_COLOR_WALLPAPER.ordinal -> configureView()
      QUICK_SET.ordinal -> handleQuickSetClick()
      DOWNLOAD.ordinal -> handleDownloadClick()
      EDIT_SET.ordinal -> handleEditSetClick()
      ADD_TO_COLLECTION.ordinal -> handleAddToCollectionClick()
      SHARE.ordinal -> handleShareClick()
    }
  }

  private fun setImageTypeText() {
    if (colorsDetailMode == SINGLE) {
      resourceUtils.getStringResource(R.string.colors_detail_activity_colors_style_name_solid)
    } else {
      when (multiColorImageType) {
        MATERIAL -> resourceUtils.getStringResource(
            R.string.colors_detail_activity_colors_style_name_material)
        GRADIENT -> resourceUtils.getStringResource(
            R.string.colors_detail_activity_colors_style_name_gradient)
        else -> resourceUtils.getStringResource(
            R.string.colors_detail_activity_colors_style_name_plasma)
      }
    }.let {
      view?.showImageTypeText(it)
    }
  }

  private fun loadImage() {
    if (colorsDetailMode == SINGLE) {
      colorImagesUseCase.getSingularColorBitmapSingle(colorList[FIRST_ELEMENT_POSITION])
    } else {
      colorImagesUseCase.getMultiColorBitmapSingle(colorList, multiColorImageType!!)
    }.observeOn(postExecutionThread.scheduler)
        .doOnSubscribe {
          view?.showMainImageWaitLoader()
          disableOperations()
        }
        .autoDisposable(view!!.getScope())
        .subscribe({
          view?.hideMainImageWaitLoader()
          view?.showImage(it)
          enableOperations()
        }, {
          view?.showImageLoadError()
          enableOperations()
        })
  }

  private fun handleCropResult(cropResultUri: Uri?) {
    view?.showIndefiniteLoader(
        resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
    colorImagesUseCase.getBitmapFromUriSingle(cropResultUri)
        .doOnSubscribe {
          isColorWallpaperOperationActive = true
        }
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(view?.getScope()!!)
        .subscribe({
          isColorWallpaperOperationActive = false
          lastImageOperationType = EDITED
          view?.showImage(it)
          if (wallpaperSetter.setWallpaper(it)) {
            view?.showWallpaperSetSuccessMessage()
          } else {
            view?.showWallpaperSetErrorMessage()
          }
          view?.hideIndefiniteLoader()
        }, {
          isColorWallpaperOperationActive = false
          view?.showGenericErrorMessage()
          view?.hideIndefiniteLoader()
        })
  }

  private fun disableOperations() {
    view?.disableColorOperations()
    areColorOperationsDisabled = true
  }

  private fun enableOperations() {
    view?.enableColorOperations()
    areColorOperationsDisabled = false
  }

  private fun isNotInOperation(): Boolean {
    if (!areColorOperationsDisabled) {
      return true
    } else {
      view?.showColorOperationsDisabledMessage()
    }
    return false
  }

  private fun isUserPremium(colorsActionType: ColorsActionType): Boolean {
    if (userPremiumStatusUseCase.isUserPremium()) {
      return true
    } else {
      view?.redirectToBuyPro(colorsActionType.ordinal)
    }
    return false
  }

  private fun hasStoragePermissions(colorsActionType: ColorsActionType): Boolean {
    if (permissionsHelper.isReadPermissionAvailable() && permissionsHelper.isWritePermissionAvailable()) {
      return true
    } else {
      view?.requestStoragePermission(colorsActionType)
    }
    return false
  }
}

enum class ColorsActionType {
  LOAD_COLOR_WALLPAPER,
  QUICK_SET,
  DOWNLOAD,
  EDIT_SET,
  ADD_TO_COLLECTION,
  SHARE
}