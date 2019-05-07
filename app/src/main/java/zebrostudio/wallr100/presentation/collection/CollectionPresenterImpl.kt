package zebrostudio.wallr100.presentation.collection

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.CollectionImagesUseCase
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionPresenter
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionView
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity
import zebrostudio.wallr100.presentation.collection.mapper.CollectionImagesPresenterEntityMapper

private const val SIZE_OF_LIST_WITH_ONE_ELEMENT = 1

class CollectionPresenterImpl(
  private val widgetHintsUseCase: WidgetHintsUseCase,
  private val userPremiumStatusUseCase: UserPremiumStatusUseCase,
  private val imageOptionsUseCase: ImageOptionsUseCase,
  private val collectionImagesUseCase: CollectionImagesUseCase,
  private val collectionImagesPresenterEntityMapper: CollectionImagesPresenterEntityMapper,
  private val postExecutionThread: PostExecutionThread
) : CollectionPresenter {

  private var collectionView: CollectionView? = null
  private var isWallpaperChangerEnabled = false

  override fun attachView(view: CollectionView) {
    collectionView = view
  }

  override fun detachView() {
    collectionView = null
  }

  override fun handleViewCreated() {
    println("here 1")
    if (true) { // check if user is premium
      if (collectionView?.hasStoragePermission() == true) {
        println("here 2")
        showPictures()
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

  override fun handleWallpaperChangerEnabled() {

  }

  override fun handleWallpaperChangerDisabled() {

  }

  override fun handleImageOptionsHintDismissed(listSize: Int) {
    widgetHintsUseCase.saveCollectionsImageOptionHintShown()
    if (listSize > SIZE_OF_LIST_WITH_ONE_ELEMENT) {
      collectionView?.showReorderImagesHint()
    }
  }

  override fun handleReorderImagesHintHintDismissed() {
    widgetHintsUseCase.saveCollectionsImageReorderHintShown()
  }

  override fun handleItemMoved(
    fromPosition: Int,
    toPosition: Int,
    imagePathList: List<CollectionsPresenterEntity>
  ) {

  }

  override fun handleItemClicked(
    position: Int,
    imageList: List<CollectionsPresenterEntity>,
    selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>
  ) {

  }

  override fun handleItemLongClicked(
    position: Int,
    imageList: List<CollectionsPresenterEntity>,
    selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>
  ) {

  }

  override fun handleAutomaticWallpaperChangerEnabled() {

  }

  override fun handleAutomaticWallpaperChangerDisabled() {

  }

  private fun showPictures() {
    collectionImagesUseCase.getAllImages()
        .doAfterSuccess {
          isWallpaperChangerEnabled = imageOptionsUseCase.isAutomaticWallpaperChangerEnabled()
        }
        .map {
          collectionImagesPresenterEntityMapper.mapToPresenterEntity(it)
        }
        .observeOn(postExecutionThread.scheduler)
        .doAfterSuccess {
          showHintsIfSuitable(it.size)
        }
        .autoDisposable(collectionView!!.getScope())
        .subscribe({
          collectionView?.showImages(it)
          collectionView?.hideImagesAbsentLayout()
          validateWallpaperChangerVisibilityState(it.size)
        }, {
          collectionView?.showImagesAbsentLayout()
          collectionView?.showGenericErrorMessage()
        })
  }

  private fun validateWallpaperChangerVisibilityState(listSize: Int) {
    if (isWallpaperChangerEnabled && listSize >= SIZE_OF_LIST_WITH_ONE_ELEMENT) {
      collectionView?.showAutomaticWallpaperStateAsActive()
    }
  }

  private fun showHintsIfSuitable(listSize: Int) {
    if (listSize >= SIZE_OF_LIST_WITH_ONE_ELEMENT
        && !widgetHintsUseCase.isCollectionsImageOptionHintShown()) {
      collectionView?.showImageOptionsHint()
    } else if (listSize > SIZE_OF_LIST_WITH_ONE_ELEMENT
        && widgetHintsUseCase.isCollectionsImageOptionHintShown()) {
      collectionView?.showReorderImagesHint()
    }
  }

}