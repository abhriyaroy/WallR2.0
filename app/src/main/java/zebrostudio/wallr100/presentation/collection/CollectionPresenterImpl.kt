package zebrostudio.wallr100.presentation.collection

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.CollectionImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionPresenter
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionView
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity
import zebrostudio.wallr100.presentation.collection.mapper.CollectionImagesPresenterEntityMapper

class CollectionPresenterImpl(
  private val widgetHintsUseCase: WidgetHintsUseCase,
  private val userPremiumStatusUseCase: UserPremiumStatusUseCase,
  private val collectionImagesUseCase: CollectionImagesUseCase,
  private val collectionImagesPresenterEntityMapper: CollectionImagesPresenterEntityMapper,
  private val postExecutionThread: PostExecutionThread
) : CollectionPresenter {

  private var collectionView: CollectionView? = null

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

  override fun handleWallpaperChangerActivated() {

  }

  override fun handleWallpaperChangerDeactivated() {

  }

  override fun handleImageOptionsHintDismissed() {

  }

  override fun handleReorderImagesHintHintDismissed() {

  }

  override fun handleItemMoved(fromPosition: Int, toPosition: Int, imagePathList: List<CollectionsPresenterEntity>) {

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

  private fun showPictures() {
    collectionImagesUseCase.getAllImages()
        .map {
          collectionImagesPresenterEntityMapper.mapToPresenterEntity(it)
        }
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(collectionView!!.getScope())
        .subscribe({
          println("here $it")
          collectionView?.showImages(it)
          collectionView?.hideImagesAbsentLayout()
        },{
          println(it)
        })
  }

}