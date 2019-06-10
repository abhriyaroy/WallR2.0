package zebrostudio.wallr100.presentation.detail.images

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
    fun requestStoragePermission(actionType: ActionType)
    fun showPermissionRequiredMessage()
    fun showNoInternetToShareError()
    fun showUnsuccessfulPurchaseError()
    fun shareLink(intentExtra: String, intentType: String)
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
    fun getUriFromResultIntent(): Uri?
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
    fun showCrystallizedDownloadCompletedSuccessMessage()
    fun showCrystallizeDescriptionDialog()
    fun showCrystallizeSuccessMessage()
    fun showImageHasAlreadyBeenCrystallizedMessage()
    fun showAddToCollectionSuccessMessage()
    fun showAlreadyPresentInCollectionErrorMessage()
    fun showExpandedImage(lowQualityLink: String, highQualityLink: String)
    fun showCrystallizedExpandedImage()
    fun showEditedExpandedImage()
    fun collapseSlidingPanel()
    fun exitView()
  }

  interface DetailPresenter : BasePresenter<DetailView> {
    fun setImageType(imageTypeOrdinal: Int)
    fun handleHighQualityImageLoadFailed()
    fun handleQuickSetClick()
    fun handleDownloadClick()
    fun handleCrystallizeClick()
    fun handleEditSetClick()
    fun handleAddToCollectionClick()
    fun handleShareClick()
    fun handleBackButtonClick()
    fun handleViewResult(requestCode: Int, resultCode: Int)
    fun handleDownloadQualitySelectionEvent(downloadType: ImageListType, selectedIndex: Int)
    fun handleCrystallizeDialogPositiveClick()
    fun handleImageViewClicked()
    fun setPanelStateAsExpanded()
    fun setPanelStateAsCollapsed()
    fun handlePermissionRequestResult(
      requestCode: Int, permissions: Array<String>, grantResults: IntArray
    )
  }
}