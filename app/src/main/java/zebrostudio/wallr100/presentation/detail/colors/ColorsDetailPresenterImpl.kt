package zebrostudio.wallr100.presentation.detail.colors

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.detail.colors.COLORS_DETAIL_MODE_INTENT_EXTRA_TAG
import zebrostudio.wallr100.android.ui.detail.colors.COLORS_DETAIL_MULTIPLE_TYPE_INTENT_EXTRA_TAG
import zebrostudio.wallr100.android.ui.detail.colors.COLORS_HEX_VALUE_LIST_INTENT_EXTRA_TAG
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode.MULTIPLE
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode.SINGLE
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ColorsDetailsUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType.ADD_TO_COLLECTION
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType.DOWNLOAD
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType.EDIT_SET
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType.LOAD_COLOR_WALLPAPER
import zebrostudio.wallr100.presentation.detail.colors.ColorsActionType.QUICK_SET
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
  private val colorsDetailsUseCase: ColorsDetailsUseCase,
  private val wallpaperSetter: WallpaperSetter
) : ColorsDetailPresenter {

  internal var colorsDetailMode: ColorsDetailMode = SINGLE
  internal var multiColorImageType: MultiColorImageType? = null
  internal var colorList = mutableListOf<String>()
  internal var isPanelExpanded: Boolean = false
  internal var areColorOperationsDisabled: Boolean = false
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

  override fun handleBackButtonClick() {
    if (isPanelExpanded) {
      view?.collapsePanel()
    } else {
      view?.exitView()
    }
  }

  override fun handleQuickSetClick() {
    if (!areColorOperationsDisabled) {
      if (view?.hasStoragePermission() == true) {
        colorsDetailsUseCase.getBitmapSingle()
            .doOnSuccess {
              wallpaperSetter.setWallpaper(it)
            }
            .observeOn(postExecutionThread.scheduler)
            .doOnSubscribe {
              view?.showIndefiniteWaitLoader(
                  context.stringRes(R.string.finalizing_stuff_wait_message))
            }
            .autoDisposable(view!!.getScope())
            .subscribe({
              view?.hideIndefiniteWaitLoader()
              view?.showWallpaperSetSuccessMessage()
            }, {
              view?.hideIndefiniteWaitLoader()
              view?.showWallpaperSetErrorMessage()
            })
      } else {
        view?.requestStoragePermission(QUICK_SET)
      }
    } else {
      view?.showColorOperationsDisbaledMessage()
    }
  }

  override fun handleDownloadClick() {
    if (!areColorOperationsDisabled) {
      if (userPremiumStatusUseCase.isUserPremium()) {
        if (view?.hasStoragePermission() == true) {

        } else {
          view?.requestStoragePermission(DOWNLOAD)
        }
      } else {
        view?.redirectToBuyPro(DOWNLOAD.ordinal)
      }
    } else {
      view?.showColorOperationsDisbaledMessage()
    }
  }

  override fun handleEditSetClick() {
    if (!areColorOperationsDisabled) {

    } else {
      view?.showColorOperationsDisbaledMessage()
    }
  }

  override fun handleAddToCollectionClick() {
    if (!areColorOperationsDisabled) {

    } else {
      view?.showColorOperationsDisbaledMessage()
    }
  }

  override fun handleShareClick() {
    if (!areColorOperationsDisabled) {

    } else {
      view?.showColorOperationsDisbaledMessage()
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
    } else when (multiColorImageType) {
      MATERIAL -> context.stringRes(R.string.colors_detail_activity_colors_style_name_material)
      GRADIENT -> context.stringRes(R.string.colors_detail_activity_colors_style_name_solid)
      else -> context.stringRes(R.string.colors_detail_activity_colors_style_name_solid)
    }.let {
      view?.showImageTypeText(it)
    }
  }

  private fun loadImage() {
    if (colorsDetailMode == SINGLE) {
      colorsDetailsUseCase.getColorBitmapSingle(colorList[FIRST_ELEMENT_POSITION])
    } else {
      colorsDetailsUseCase.getMultiColorMaterialSingle(colorList, multiColorImageType!!)
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

  private fun disableOperations(){
    view?.disableColorOperations()
    areColorOperationsDisabled = true
  }

  private fun enableOperations(){
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