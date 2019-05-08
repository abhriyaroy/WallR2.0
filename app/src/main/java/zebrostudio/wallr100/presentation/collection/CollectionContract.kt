package zebrostudio.wallr100.presentation.collection

import android.net.Uri
import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity

interface CollectionContract {

  interface CollectionView : BaseView {
    fun showPurchasePremiumToContinueDialog()
    fun redirectToBuyPro()
    fun hasStoragePermission(): Boolean
    fun requestStoragePermission()
    fun showReorderImagesHint()
    fun showImages(imageList: List<CollectionsPresenterEntity>)
    fun clearImages()
    fun hideImagesAbsentLayout()
    fun showImagesAbsentLayout()
    fun showAutomaticWallpaperStateAsActive()
    fun showAutomaticWallpaperStateAsInActive()
    fun showWallpaperChangerIntervalDialog(choice: Int)
    fun showImagePicker()
    fun showImagesAddedSuccessfullyMessage(count: Int)
    fun updateAllItemViews()
    fun updateItemView(position: Int)
    fun addToSelectedItems(position: Int, collectionsPresenterEntity: CollectionsPresenterEntity)
    fun removeFromSelectedItems(position: Int)
    fun clearAllSelectedItems()
    fun isCabActive(): Boolean
    fun showSingleImageSelectedCab()
    fun showMultipleImagesSelectedCab()
    fun hideCab()
    fun showGenericErrorMessage()
  }

  interface CollectionPresenter : BasePresenter<CollectionView> {
    fun handleViewCreated()
    fun handleImportFromLocalStorageClicked()
    fun handlePurchaseClicked()
    fun handleChangeWallpaperIntervalClicked()
    fun handleWallpaperChangerEnabled()
    fun handleWallpaperChangerDisabled()
    fun handleReorderImagesHintHintDismissed()
    fun handleItemMoved(
      fromPosition: Int,
      toPosition: Int,
      imagePathList: List<CollectionsPresenterEntity>
    )

    fun handleItemClicked(
      position: Int,
      imageList: List<CollectionsPresenterEntity>,
      selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>
    )

    fun handleAutomaticWallpaperChangerEnabled()
    fun handleAutomaticWallpaperChangerDisabled()
    fun handleAutomaticWallpaperIntervalChangerMenuItemClicked()
    fun updateWallpaperChangerInterval(choice: Int)
    fun handleImagePickerResult(uriList: List<Uri>)
    fun handleSetWallpaperMenuItemClicked()
    fun handleCrystallizeWallpaperMenuItemClicked()
    fun handleDeleteWallpaperMenuItemClicked()
    fun handleCabDestroyed()
  }

}