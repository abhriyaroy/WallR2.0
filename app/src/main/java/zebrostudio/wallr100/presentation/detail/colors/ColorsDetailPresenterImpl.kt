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
        loadImage()
      } else {
        view?.requestStoragePermission(LOAD_COLOR_WALLPAPER)
      }
    } else {
      view?.throwIllegalStateException()
    }
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
    view?.exitView()
  }

  override fun handleQuickSetClick() {
    if (view?.hasStoragePermission() == true) {
      colorsDetailsUseCase.getBitmapSingle()
          .observeOn(postExecutionThread.scheduler)
          .doOnSubscribe {
            view?.showIndefiniteWaitLoader(
                context.stringRes(R.string.finalizing_wallpaper_wait_message))
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
  }

  override fun handleDownloadClick() {
    if (userPremiumStatusUseCase.isUserPremium()) {
      if (view?.hasStoragePermission() == true) {

      } else {
        view?.requestStoragePermission(DOWNLOAD)
      }
    } else {
      view?.redirectToBuyPro(DOWNLOAD.ordinal)
    }
  }

  override fun handleEditSetClick() {

  }

  override fun handleAddToCollectionClick() {

  }

  override fun handleShareClick() {

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

  private fun loadImage() {
    if (colorsDetailMode == SINGLE) {
      colorsDetailsUseCase.getColorBitmapSingle(colorList[FIRST_ELEMENT_POSITION])
    } else {
      colorsDetailsUseCase.getMultiColorMaterialSingle(colorList, multiColorImageType!!)
    }.observeOn(postExecutionThread.scheduler)
        .doOnSubscribe {
          view?.showMainImageWaitLoader()
        }
        .autoDisposable(view!!.getScope())
        .subscribe({
          view?.hideMainImageWaitLoader()
          view?.showImage(it)
        }, {
          it.printStackTrace()
          view?.showImageLoadError()
        })
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