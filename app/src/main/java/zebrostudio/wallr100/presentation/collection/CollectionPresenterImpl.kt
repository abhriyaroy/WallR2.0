package zebrostudio.wallr100.presentation.collection

import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionPresenter

class CollectionPresenterImpl(
  widgetHintsUseCase: WidgetHintsUseCase,
  userPremiumStatusUseCase: UserPremiumStatusUseCase
) : CollectionPresenter {

  private var collectionView: CollectionContract.CollectionView? = null

  override fun attachView(view: CollectionContract.CollectionView) {
    collectionView = view
  }

  override fun detachView() {
    collectionView = null
  }

  override fun handleViewCreated() {
    if (true) { // check if user is premium
      if (collectionView?.hasStoragePermission() == true) {

      } else {
        collectionView?.requestStoragePermission()
      }
    } else {
      collectionView?.showPurchasePremiumToContinueDialog()
    }
  }

  override fun handleImportFromLocalStorageClicked() {

  }

  override fun handlePurchaseClicked() {
    collectionView?.redirectToBuyPro()
  }

  override fun handleChangeWallpaperIntervalClicked() {

  }

  override fun handleWallpaperChangerActivated() {

  }

  override fun handleWallpaperChangerDeactivated() {

  }

  override fun handleImageptionsHintDismissed() {

  }

  override fun handleReorderImagesHintHintDismissed() {

  }

}