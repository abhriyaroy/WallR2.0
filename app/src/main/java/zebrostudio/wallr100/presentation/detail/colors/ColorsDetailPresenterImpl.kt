package zebrostudio.wallr100.presentation.detail.colors

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.uber.autodispose.autoDisposable
import com.yalantis.ucrop.UCrop.REQUEST_CROP
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig
import zebrostudio.wallr100.android.ui.detail.colors.COLORS_DETAIL_MODE_INTENT_EXTRA_TAG
import zebrostudio.wallr100.android.ui.detail.colors.COLORS_DETAIL_MULTIPLE_TYPE_INTENT_EXTRA_TAG
import zebrostudio.wallr100.android.ui.detail.colors.COLORS_HEX_VALUE_LIST_INTENT_EXTRA_TAG
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode.MULTIPLE
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode.SINGLE
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.data.exception.AlreadyPresentInCollectionException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ColorImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.model.CollectionsImageModel.EDITED
import zebrostudio.wallr100.domain.model.CollectionsImageModel.MINIMAL_COLOR
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
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.PLASMA

const val FIRST_ELEMENT_POSITION = 0

class ColorsDetailPresenterImpl(
  private val context: Context,
  private val postExecutionThread: PostExecutionThread,
  private val userPremiumStatusUseCase: UserPremiumStatusUseCase,
  private val colorImagesUseCase: ColorImagesUseCase,
  private val wallpaperSetter: WallpaperSetter
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

  override fun setCalledIntent(intent: Intent) {
    if (intent.extras != null) {
      processIntent(intent)
      if (view?.hasStoragePermission() == true) {
        setImageTypeText()
        loadImage()
      } else {
        view?.requestStoragePermission(LOAD_COLOR_WALLPAPER)
      }
    } else {
      view?.throwIllegalStateException()
    }
  }

  override fun setPanelStateAsExpanded() {
    isPanelExpanded = true
  }

  override fun setPanelStateAsCollapsed() {
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
        requestCode == ADD_TO_COLLECTION.ordinal
    ) {
      if ((grantResults.isNotEmpty() && grantResults[FIRST_ELEMENT_POSITION]
              == PackageManager.PERMISSION_GRANTED)) {
        handlePermissionGranted(requestCode)
      } else {
        if (requestCode == LOAD_COLOR_WALLPAPER.ordinal) {
          view?.exitView()
        }
        view?.showPermissionRequiredMessage()
      }
    }
  }

  override fun handleViewResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == DOWNLOAD.ordinal) {
      if (resultCode == PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE) {
        handleDownloadClick()
      } else {
        view?.showUnsuccessfulPurchaseError()
      }
    } else if (requestCode == ADD_TO_COLLECTION.ordinal) {
      if (resultCode == PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE) {
        handleAddToCollectionClick()
      } else {
        view?.showUnsuccessfulPurchaseError()
      }
    } else if (requestCode == ColorsActionType.SHARE.ordinal) {
      if (resultCode == PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE) {
        handleShareClick()
      } else {
        view?.showUnsuccessfulPurchaseError()
      }
    } else if (requestCode == REQUEST_CROP && resultCode == RESULT_OK) {
      view?.let {
        val cropResultUri = view?.getUriFromIntent(data!!)
        if (cropResultUri != null) {
          handleCropResult(cropResultUri)
        } else {
          view?.hideIndefiniteWaitLoader()
          view?.showGenericErrorMessage()
        }
      }
    } else {
      view?.hideIndefiniteWaitLoader()
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
    if (!areColorOperationsDisabled) {
      if (view?.hasStoragePermission() == true) {
        colorImagesUseCase.getBitmapSingle()
            .doOnSuccess {
              wallpaperSetter.setWallpaper(it)
            }
            .observeOn(postExecutionThread.scheduler)
            .doOnSubscribe {
              isColorWallpaperOperationActive = true
              view?.showIndefiniteWaitLoader(
                  context.stringRes(R.string.finalizing_wallpaper_messsage))
            }
            .autoDisposable(view!!.getScope())
            .subscribe({
              isColorWallpaperOperationActive = false
              view?.hideIndefiniteWaitLoader()
              view?.showWallpaperSetSuccessMessage()
            }, {
              isColorWallpaperOperationActive = false
              view?.hideIndefiniteWaitLoader()
              view?.showWallpaperSetErrorMessage()
            })
      } else {
        view?.requestStoragePermission(QUICK_SET)
      }
    } else {
      view?.showColorOperationsDisabledMessage()
    }
  }

  override fun handleDownloadClick() {
    if (!areColorOperationsDisabled) {
      if (userPremiumStatusUseCase.isUserPremium()) {
        if (view?.hasStoragePermission() == true) {
          colorImagesUseCase.downloadImage()
              .observeOn(postExecutionThread.scheduler)
              .doOnSubscribe {
                view?.showIndefiniteWaitLoader(context.stringRes(
                    R.string.detail_activity_crystallizing_wallpaper_please_wait_message))
                isColorWallpaperOperationActive = true
              }
              .autoDisposable(view!!.getScope())
              .subscribe({
                view?.hideIndefiniteWaitLoader()
                view?.showDownloadCompletedSuccessMessage()
                isColorWallpaperOperationActive = false
              }, {
                view?.hideIndefiniteWaitLoader()
                view?.showGenericErrorMessage()
                isColorWallpaperOperationActive = false
              })
        } else {
          view?.requestStoragePermission(DOWNLOAD)
        }
      } else {
        view?.redirectToBuyPro(DOWNLOAD.ordinal)
      }
    } else {
      view?.showColorOperationsDisabledMessage()
    }
  }

  override fun handleEditSetClick() {
    if (!areColorOperationsDisabled) {
      if (view?.hasStoragePermission() == true) {
        isColorWallpaperOperationActive = true
        view?.showIndefiniteWaitLoader(
            context.stringRes(R.string.detail_activity_editing_tool_message))
        view?.startCroppingActivity(
            colorImagesUseCase.getCacheSourceUri(),
            colorImagesUseCase.getCroppingDestinationUri(),
            wallpaperSetter.getDesiredMinimumWidth(),
            wallpaperSetter.getDesiredMinimumHeight())
        isColorWallpaperOperationActive = false
      } else {
        view?.requestStoragePermission(EDIT_SET)
      }
    } else {
      view?.showColorOperationsDisabledMessage()
    }
  }

  override fun handleAddToCollectionClick() {
    if (!areColorOperationsDisabled) {
      if (userPremiumStatusUseCase.isUserPremium()) {
        if (view?.hasStoragePermission() == true) {
          colorImagesUseCase.saveToCollectionsCompletable(colorList.toString(),
              lastImageOperationType)
              .observeOn(postExecutionThread.scheduler)
              .doOnSubscribe {
                view?.showIndefiniteWaitLoader(
                    context.stringRes(R.string.adding_image_to_collections_message))
                isColorWallpaperOperationActive = true
              }
              .autoDisposable(view!!.getScope())
              .subscribe({
                view?.showAddToCollectionSuccessMessage()
                view?.hideIndefiniteWaitLoader()
                isColorWallpaperOperationActive = false
              }, {
                if (it is AlreadyPresentInCollectionException) {
                  view?.showAlreadyPresentInCollectionErrorMessage()
                } else {
                  view?.showGenericErrorMessage()
                }
                view?.hideIndefiniteWaitLoader()
                isColorWallpaperOperationActive = false
              })
        } else {
          view?.requestStoragePermission(ADD_TO_COLLECTION)
        }
      } else {
        view?.redirectToBuyPro(ADD_TO_COLLECTION.ordinal)
      }
    } else {
      view?.showColorOperationsDisabledMessage()
    }
  }

  override fun handleShareClick() {
    if (!areColorOperationsDisabled) {
      if (userPremiumStatusUseCase.isUserPremium()) {
        if (view?.hasStoragePermission() == true) {
          colorImagesUseCase.getCacheImageUri()
              .observeOn(postExecutionThread.scheduler)
              .doOnSubscribe {
                isColorWallpaperOperationActive = true
                view?.showIndefiniteWaitLoader(
                    context.stringRes(R.string.preparing_shareable_wallpaper_message))
              }
              .autoDisposable(view!!.getScope())
              .subscribe({
                isColorWallpaperOperationActive = false
                view?.hideIndefiniteWaitLoader()
                view?.showShareIntent(it)
              }, {
                it.printStackTrace()
                isColorWallpaperOperationActive = false
                view?.hideIndefiniteWaitLoader()
                view?.showGenericErrorMessage()
              })
        } else {
          view?.requestStoragePermission(SHARE)
        }
      } else {
        view?.redirectToBuyPro(SHARE.ordinal)
      }
    } else {
      view?.showColorOperationsDisabledMessage()
    }
  }

  private fun handlePermissionGranted(requestCode: Int) {
    when (requestCode) {
      LOAD_COLOR_WALLPAPER.ordinal -> loadImage()
      QUICK_SET.ordinal -> handleQuickSetClick()
      DOWNLOAD.ordinal -> handleDownloadClick()
      EDIT_SET.ordinal -> handleEditSetClick()
      ADD_TO_COLLECTION.ordinal -> handleAddToCollectionClick()
    }
  }

  private fun processIntent(intent: Intent) {
    colorsDetailMode =
        if (intent.getIntExtra(COLORS_DETAIL_MODE_INTENT_EXTRA_TAG, SINGLE.ordinal)
            == SINGLE.ordinal) {
          SINGLE
        } else {
          MULTIPLE
        }
    if (colorsDetailMode == MULTIPLE) {
      val ordinal =
          intent.getIntExtra(COLORS_DETAIL_MULTIPLE_TYPE_INTENT_EXTRA_TAG, MATERIAL.ordinal)
      multiColorImageType = when (ordinal) {
        MATERIAL.ordinal -> MATERIAL
        GRADIENT.ordinal -> GRADIENT
        else -> PLASMA
      }
    }
    colorList = intent.getStringArrayListExtra(COLORS_HEX_VALUE_LIST_INTENT_EXTRA_TAG)
  }

  private fun setImageTypeText() {
    if (colorsDetailMode == SINGLE) {
      context.stringRes(R.string.colors_detail_activity_colors_style_name_solid)
    } else {
      when (multiColorImageType) {
        MATERIAL -> context.stringRes(R.string.colors_detail_activity_colors_style_name_material)
        GRADIENT -> context.stringRes(R.string.colors_detail_activity_colors_style_name_gradient)
        else -> context.stringRes(R.string.colors_detail_activity_colors_style_name_plasma)
      }
    }.let {
      view?.showImageTypeText(it)
    }
  }

  private fun loadImage() {
    if (colorsDetailMode == SINGLE) {
      colorImagesUseCase.getSingularColorBitmapSingle(colorList[FIRST_ELEMENT_POSITION])
    } else {
      colorImagesUseCase.getMultiColorMaterialSingle(colorList, multiColorImageType!!)
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
          it.printStackTrace()
          view?.showImageLoadError()
          enableOperations()
        })
  }

  private fun handleCropResult(cropResultUri: Uri) {
    var hasWallpaperBeenSet = false
    view?.showIndefiniteWaitLoader(context.stringRes(R.string.finalizing_wallpaper_messsage))
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
          hasWallpaperBeenSet = wallpaperSetter.setWallpaper(it)
          if (hasWallpaperBeenSet) {
            view?.showWallpaperSetSuccessMessage()
          } else {
            view?.showWallpaperSetErrorMessage()
          }
          view?.hideIndefiniteWaitLoader()
        }, {
          isColorWallpaperOperationActive = false
          view?.showGenericErrorMessage()
          view?.hideIndefiniteWaitLoader()
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
}

enum class ColorsActionType {
  LOAD_COLOR_WALLPAPER,
  QUICK_SET,
  DOWNLOAD,
  EDIT_SET,
  ADD_TO_COLLECTION,
  SHARE
}