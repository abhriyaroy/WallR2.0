package zebrostudio.wallr100.presentation.collection

import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity

interface CollectionContract {

  interface CollectionView : BaseView {
    fun showPurchasePremiumToContinueDialog()
    fun redirectToBuyPro()
    fun hasStoragePermission(): Boolean
    fun requestStoragePermission()
    fun showImageOptionsHint()
    fun showReorderImagesHint()
    fun showImages(imageList: List<CollectionsPresenterEntity>)
  }

  interface CollectionPresenter : BasePresenter<CollectionView> {
    fun handleViewCreated()
    fun handleImportFromLocalStorageClicked()
    fun handlePurchaseClicked()
    fun handleChangeWallpaperIntervalClicked()
    fun handleWallpaperChangerActivated()
    fun handleWallpaperChangerDeactivated()
    fun handleImageOptionsHintDismissed()
    fun handleReorderImagesHintHintDismissed()
    fun handleItemMoved(fromPosition: Int, toPosition: Int, imagePathList: List<String>)
    fun handleItemClicked(
      position: Int,
      imagePathList: List<String>,
      selectedItemsMap: HashMap<Int, String>
    )

    fun handleItemLongClicked(
      position: Int,
      imagePathList: List<String>,
      selectedItemsMap: HashMap<Int, String>
    )
  }

}