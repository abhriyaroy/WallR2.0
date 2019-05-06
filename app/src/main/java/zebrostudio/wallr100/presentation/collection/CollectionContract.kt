package zebrostudio.wallr100.presentation.collection

import zebrostudio.wallr100.presentation.BasePresenter

interface CollectionContract {

  interface CollectionView{
    fun showPurchasePremiumToContinueDialog()
    fun redirectToBuyPro()
    fun hasStoragePermission() : Boolean
    fun requestStoragePermission()
    fun showImageOptionsHint()
    fun showReorderImagesHint()
  }

  interface CollectionPresenter : BasePresenter<CollectionView>{
    fun handleViewCreated()
    fun handleImportFromLocalStorageClicked()
    fun handlePurchaseClicked()
    fun handleChangeWallpaperIntervalClicked()
    fun handleWallpaperChangerActivated()
    fun handleWallpaperChangerDeactivated()
    fun handleImageptionsHintDismissed()
    fun handleReorderImagesHintHintDismissed()
  }

}