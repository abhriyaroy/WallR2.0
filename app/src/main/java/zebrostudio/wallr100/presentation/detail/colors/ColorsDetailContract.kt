package zebrostudio.wallr100.presentation.detail.colors

import android.graphics.Bitmap
import android.net.Uri
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailMode
import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType

interface ColorsDetailContract {

  interface ColorsDetailView : BaseView {
    fun getMultiColorImageType(): MultiColorImageType
    fun showImageTypeText(text: String)
    fun requestStoragePermission(colorsActionType: ColorsActionType)
    fun showPermissionRequiredMessage()
    fun redirectToBuyPro(requestCode: Int)
    fun showUnsuccessfulPurchaseError()
    fun showImage(bitmap: Bitmap)
    fun showMainImageWaitLoader()
    fun hideMainImageWaitLoader()
    fun showNotEnoughFreeSpaceErrorMessage()
    fun showImageLoadError()
    fun showNoInternetError()
    fun showIndefiniteLoader(message: String)
    fun hideIndefiniteLoader()
    fun showWallpaperSetErrorMessage()
    fun showWallpaperSetSuccessMessage()
    fun showAddToCollectionSuccessMessage()
    fun collapsePanel()
    fun disableColorOperations()
    fun enableColorOperations()
    fun showColorOperationsDisabledMessage()
    fun getUriFromResultIntent(): Uri?
    fun showGenericErrorMessage()
    fun showOperationInProgressWaitMessage()
    fun showFullScreenImage()
    fun showDownloadCompletedSuccessMessage()
    fun showAlreadyPresentInCollectionErrorMessage()
    fun showShareIntent(uri: Uri)
    fun exitView()
    fun startCroppingActivity(
      source: Uri,
      destination: Uri,
      minimumWidth: Int,
      minimumHeight: Int
    )
  }

  interface ColorsDetailPresenter : BasePresenter<ColorsDetailView> {
    fun setColorsDetailMode(colorsDetailMode: ColorsDetailMode)
    fun setColorList(list: List<String>)
    fun handleViewReadyState()
    fun notifyPanelExpanded()
    fun notifyPanelCollapsed()
    fun handleViewResult(requestCode: Int, resultCode: Int)
    fun handleImageViewClicked()
    fun handleQuickSetClick()
    fun handleDownloadClick()
    fun handleEditSetClick()
    fun handleAddToCollectionClick()
    fun handleShareClick()
    fun handleBackButtonClick()
    fun handlePermissionRequestResult(
      requestCode: Int, permissions: Array<String>, grantResults: IntArray
    )
  }
}