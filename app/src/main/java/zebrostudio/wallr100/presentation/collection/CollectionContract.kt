package zebrostudio.wallr100.presentation.collection

import zebrostudio.wallr100.presentation.BasePresenter

interface CollectionContract {

  interface CollectionView{
    fun redirectToBuyPro()
    fun requestStoragePermission()
    fun showImageOptionsHint()
    fun showReoderImagesHint()
  }

  interface CollectionPresenter : BasePresenter<CollectionView>{
    fun handleViewCreated()
    fun handleImportFromLocalStorageClicked()
    fun handleChangeWallpaperIntervalClicked()
    fun handleWallpaperChangerActivated()
    fun handleWallpaperChangerDeactivated()
    fun handleImageptionsHintDismissed()
    fun handleReorderImagesHintHintDismissed()
  }

}