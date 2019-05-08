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
    fun showImagePinchHint()
    fun showReorderImagesHint()
    fun showImages(imageList: List<CollectionsPresenterEntity>)
    fun hideImagesAbsentLayout()
    fun showImagesAbsentLayout()
    fun showAutomaticWallpaperStateAsActive()
    fun showAutomaticWallpaperStateAsInActive()
    fun showWallpaperChangerIntervalDialog(choice: Int)
    fun showImagePicker()
    fun showGenericErrorMessage()
  }

  interface CollectionPresenter : BasePresenter<CollectionView> {
    fun handleViewCreated()
    fun handleImportFromLocalStorageClicked()
    fun handlePurchaseClicked()
    fun handleChangeWallpaperIntervalClicked()
    fun handleWallpaperChangerEnabled()
    fun handleWallpaperChangerDisabled()
    fun handleImageOptionsHintDismissed(listSize: Int)
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

    fun handleItemLongClicked(
      position: Int,
      imageList: List<CollectionsPresenterEntity>,
      selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>
    )

    fun handleAutomaticWallpaperChangerEnabled()
    fun handleAutomaticWallpaperChangerDisabled()
    fun handleAutomaticWallpaperIntervalChangerMenuItemClicked()
    fun handleImportImagesMenuItemClicked()
    fun updateWallpaperChangerInterval(choice: Int)
    fun handleImagePickerResult(uriList: List<Uri>)
  }

}