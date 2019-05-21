package zebrostudio.wallr100.presentation.collection

import android.net.Uri
import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity

interface CollectionContract {

  interface CollectionView : BaseView {
    fun getManufacturerName(): String
    fun showAppBarWithDelay()
    fun hideAppBar()
    fun enableToolbar()
    fun showPurchaseProToContinueDialog()
    fun redirectToBuyPro()
    fun hasStoragePermission(): Boolean
    fun requestStoragePermission()
    fun showReorderImagesHintWithDelay()
    fun setImagesList(imageList: List<CollectionsPresenterEntity>)
    fun clearImages()
    fun hideImagesAbsentLayout()
    fun showImagesAbsentLayout()
    fun showWallpaperChangerLayout()
    fun hideWallpaperChangerLayout()
    fun showAutomaticWallpaperStateAsActive()
    fun showAutomaticWallpaperStateAsInActive()
    fun showWallpaperChangerIntervalUpdatedSuccessMessage()
    fun showWallpaperChangerRestartedSuccessMessage()
    fun showWallpaperChangerIntervalDialog(choice: Int)
    fun showImagePicker()
    fun showSingleImageAddedSuccessfullyMessage()
    fun showMultipleImagesAddedSuccessfullyMessage(count: Int)
    fun updateChangesInEveryItemView()
    fun updateChangesInEveryItemViewWithDelay()
    fun updateChangesInItemView(position: Int)
    fun updateItemViewMovement(fromPosition: Int, toPosition: Int)
    fun removeItemView(position: Int)
    fun clearAllSelectedItems()
    fun isCabActive(): Boolean
    fun showSingleImageSelectedCab()
    fun showMultipleImagesSelectedCab()
    fun hideCab()
    fun showReorderSuccessMessage()
    fun showUnableToReorderErrorMessage()
    fun showSingleImageDeleteSuccessMessage()
    fun showMultipleImageDeleteSuccessMessage(count: Int)
    fun blurScreen()
    fun removeBlurFromScreen()
    fun showIndefiniteLoaderWithMessage(message: String)
    fun showSetWallpaperSuccessMessage()
    fun showCrystallizedImageAlreadyPresentInCollectionErrorMessage()
    fun showCrystallizeSuccessMessage()
    fun showUnableToDeleteErrorMessage()
    fun showGenericErrorMessage()
    fun showWallpaperChangerPermissionsRequiredDialog()
    fun disableBackPress()
    fun enableBackPress()
  }

  interface CollectionPresenter : BasePresenter<CollectionView> {
    fun handleViewCreated()
    fun handleActivityResult()
    fun handleImportFromLocalStorageClicked()
    fun handlePurchaseClicked()
    fun handleReorderImagesHintHintDismissed()
    fun handleItemMoved(
      fromPosition: Int,
      toPosition: Int,
      imagePathList: MutableList<CollectionsPresenterEntity>
    )

    fun handleItemClicked(
      position: Int,
      imageList: List<CollectionsPresenterEntity>,
      selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>
    )

    fun notifyDragStarted()
    fun handleAutomaticWallpaperChangerEnabled()
    fun handleAutomaticWallpaperChangerDisabled()
    fun handleAutomaticWallpaperChangerIntervalMenuItemClicked()
    fun updateWallpaperChangerInterval(choice: Int)
    fun handleImagePickerResult(uriList: List<Uri>)
    fun handleSetWallpaperMenuItemClicked(selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>)
    fun handleCrystallizeWallpaperMenuItemClicked(selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>)

    fun handleDeleteWallpaperMenuItemClicked(
      imageList: MutableList<CollectionsPresenterEntity>,
      selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>
    )

    fun handleCabDestroyed()
  }

}