package zebrostudio.wallr100.presentation.detail

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

interface DetailContract {

  interface DetailView : BaseView {
    fun getSearchImageDetails(): SearchPicturesPresenterEntity
    fun getWallpaperImageDetails(): ImagePresenterEntity
    fun showAuthorDetails(name: String, profileImageLink: String)
    fun showImage(lowQualityLink: String, highQualityLink: String)
    fun showImage(bitmap: Bitmap)
    fun showImageLoadError()
    fun showNoInternetError()
    fun hasStoragePermission(): Boolean
    fun requestStoragePermission(actionType: ActionType)
    fun showPermissionRequiredMessage()
    fun showNoInternetToShareError()
    fun showUnsuccessfulPurchaseError()
    fun shareLink(shortLink: String)
    fun showWaitLoader(message: String)
    fun hideWaitLoader()
    fun redirectToBuyPro(requestCode: Int)
    fun showGenericErrorMessage()
    fun blurScreen()
    fun blurScreenAndInitializeProgressPercentage()
    fun updateProgressPercentage(progress: String)
    fun showIndefiniteLoader(message: String)
    fun showIndefiniteLoaderWithAnimation(message: String)
    fun hideIndefiniteLoader()
    fun getUriFromIntent(data: Intent): Uri?
    fun hideScreenBlur()
    fun showWallpaperSetSuccessMessage()
    fun showWallpaperSetErrorMessage()
    fun showUnableToDownloadErrorMessage()
    fun showWallpaperOperationInProgressWaitMessage()
    fun showDownloadWallpaperCancelledMessage()
    fun startCroppingActivity(source: Uri, destination: Uri, minimumWidth: Int, minimumHeight: Int)
    fun showSearchTypeDownloadDialog(showCrystallizedOption: Boolean)
    fun showWallpaperTypeDownloadDialog(showCrystallizedOption: Boolean)
    fun showDownloadStartedMessage()
    fun showDownloadAlreadyInProgressMessage()
    fun showDownloadCompletedSuccessMessage()
    fun collapseSlidingPanel()
    fun exitView()
  }

  interface DetailPresenter : BasePresenter<DetailView> {
    fun setImageType(imageType: ImageListType)
    fun handleHighQualityImageLoadFailed()
    fun handleQuickSetClick()
    fun handleDownloadClick()
    fun handleCrystallizeClick()
    fun handleEditSetClick()
    fun handleAddToCollectionClick()
    fun handleShareClick()
    fun handleBackButtonClick()
    fun handlePermissionRequestResult(
      requestCode: Int, permissions: Array<String>, grantResults: IntArray
    )

    fun handleViewResult(requestCode: Int, resultCode: Int, data: Intent?)
    fun handleDownloadQualitySelectionEvent(downloadType: ImageListType, selectedIndex: Int)
    fun setPanelStateAsExpanded()
    fun setPanelStateAsCollapsed()
  }
}