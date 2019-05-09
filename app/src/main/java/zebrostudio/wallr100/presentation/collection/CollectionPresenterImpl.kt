package zebrostudio.wallr100.presentation.collection

import android.net.Uri
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
import java.util.Collections

private const val MINIMUM_LIST_SIZE_REQUIRED_TO_SHOW_HINT = 2
private const val MINIMUM_NUMBER_OF_SELECTED_ITEMS = 1
private const val INDEX_OF_THIRTY_MINUTES_WALLPAPER_CHANGER_INTERVAL = 0
private const val INDEX_OF_THREE_DAYS_WALLPAPER_CHANGER_INTERVAL = 4

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
  private val wallpaperChangerIntervals = arrayListOf<Long>(
      1800000,
      3600000,
      21600000,
      86400000,
      259200000
  )

  override fun attachView(view: CollectionView) {
    collectionView = view
  }

  override fun detachView() {
    collectionView = null
  }

  override fun handleViewCreated() {
    if (true && isStoragePermissionAvailable()) {
      showPictures()
    }
  }

  override fun handleImportFromLocalStorageClicked() {
    if (true && isStoragePermissionAvailable()) {
      collectionView?.showImagePicker()
    }
  }

  override fun handlePurchaseClicked() {
    collectionView?.redirectToBuyPro()
  }

  override fun handleChangeWallpaperIntervalClicked() {
    imageOptionsUseCase.getAutomaticWallpaperChangerInterval().let {
      var isDialogShown = false
      wallpaperChangerIntervals.forEachIndexed { index, interval ->
        if (it == interval) {
          collectionView?.showWallpaperChangerIntervalDialog(index)
          isDialogShown = true
        } else if (index == INDEX_OF_THREE_DAYS_WALLPAPER_CHANGER_INTERVAL && !isDialogShown) {
          imageOptionsUseCase.setAutomaticWallpaperChangerInterval(
              wallpaperChangerIntervals[INDEX_OF_THREE_DAYS_WALLPAPER_CHANGER_INTERVAL])
          collectionView?.showWallpaperChangerIntervalDialog(
              INDEX_OF_THIRTY_MINUTES_WALLPAPER_CHANGER_INTERVAL)
        }
      }
    }
  }

  override fun handleWallpaperChangerEnabled() {

  }

  override fun handleWallpaperChangerDisabled() {

  }

  override fun handleReorderImagesHintHintDismissed() {
    widgetHintsUseCase.saveCollectionsImageReorderHintShown()
  }

  override fun handleItemMoved(
    fromPosition: Int,
    toPosition: Int,
    imagePathList: MutableList<CollectionsPresenterEntity>
  ) {
    println("pre item moved $imagePathList")
    Collections.swap(imagePathList, fromPosition, toPosition)
    println("post item moved $imagePathList")
    collectionView?.updateItemViewMovement(fromPosition, toPosition)
    collectionImagesUseCase.reorderImage(
        collectionImagesPresenterEntityMapper.mapFromPresenterEntity(imagePathList))
        .observeOn(postExecutionThread.scheduler)
        .map {
          collectionImagesPresenterEntityMapper.mapToPresenterEntity(it)
        }
        .autoDisposable(collectionView!!.getScope())
        .subscribe({
          println("post result item moved $it")
          collectionView?.setImagesList(it)
        }, {
          collectionView?.updateChangesInEveryItemView()
          collectionView?.showUnableToReorderErrorMessage()
        })
  }

  override fun handleItemClicked(
    position: Int,
    imageList: List<CollectionsPresenterEntity>,
    selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>
  ) {
    if (selectedItemsMap.containsKey(position)) {
      collectionView?.removeFromSelectedItems(position)
    } else {
      collectionView?.addToSelectedItems(position, imageList[position])
    }
    updateSelectionChangesInCab(position, selectedItemsMap.size)
  }

  override fun notifyDragStarted() {
    if (collectionView?.isCabActive() == true) {
      collectionView?.hideCab()
    }
  }

  override fun handleAutomaticWallpaperChangerEnabled() {

  }

  override fun handleAutomaticWallpaperChangerDisabled() {

  }

  override fun handleAutomaticWallpaperIntervalChangerMenuItemClicked() {

  }

  override fun updateWallpaperChangerInterval(choice: Int) {
    imageOptionsUseCase.setAutomaticWallpaperChangerInterval(wallpaperChangerIntervals[choice])
    // Restart service for changing wallpaper
  }

  override fun handleImagePickerResult(uriList: List<Uri>) {
    collectionImagesUseCase.addImage(uriList)
        .map {
          collectionImagesPresenterEntityMapper.mapToPresenterEntity(it)
        }
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(collectionView!!.getScope())
        .subscribe({
          collectionView?.setImagesList(it)
          collectionView?.updateChangesInEveryItemView()
          collectionView?.showImagesAddedSuccessfullyMessage(uriList.size)
        }, {
          println(it.message)
          collectionView?.showGenericErrorMessage()
        })
  }

  override fun handleSetWallpaperMenuItemClicked() {

  }

  override fun handleCrystallizeWallpaperMenuItemClicked() {

  }

  override fun handleDeleteWallpaperMenuItemClicked() {

  }

  override fun handleCabDestroyed() {
    collectionView?.clearAllSelectedItems()
    collectionView?.updateChangesInEveryItemView()
  }

  private fun isUserPremium(): Boolean {
    return if (userPremiumStatusUseCase.isUserPremium()) {
      true
    } else {
      collectionView?.showPurchasePremiumToContinueDialog()
      false
    }
  }

  private fun isStoragePermissionAvailable(): Boolean {
    return if (collectionView?.hasStoragePermission() == true) {
      true
    } else {
      collectionView?.requestStoragePermission()
      false
    }
  }

  private fun showPictures() {
    collectionImagesUseCase.getAllImages()
        .doOnSuccess {
          isWallpaperChangerEnabled = imageOptionsUseCase.isAutomaticWallpaperChangerEnabled()
        }
        .map {
          collectionImagesPresenterEntityMapper.mapToPresenterEntity(it)
        }
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(collectionView!!.getScope())
        .subscribe({
          if (it.isNotEmpty()) {
            collectionView?.setImagesList(it)
            collectionView?.hideImagesAbsentLayout()
            //showHintsIfSuitable(it.size)
          } else {
            collectionView?.clearImages()
            collectionView?.showImagesAbsentLayout()
          }
          collectionView?.updateChangesInEveryItemView()
        }, {
          collectionView?.showImagesAbsentLayout()
          collectionView?.showGenericErrorMessage()
        })
  }

  private fun showHintsIfSuitable(listSize: Int) {
    if (listSize > MINIMUM_LIST_SIZE_REQUIRED_TO_SHOW_HINT) {
      collectionView?.showReorderImagesHint()
    }
  }

  private fun updateSelectionChangesInCab(position: Int, selectedMapSize: Int) {
    println("udate position $position")
    collectionView?.updateChangesInSingleItemView(position)
    if (selectedMapSize > MINIMUM_NUMBER_OF_SELECTED_ITEMS) {
      collectionView?.showMultipleImagesSelectedCab()
    } else if (selectedMapSize == MINIMUM_NUMBER_OF_SELECTED_ITEMS) {
      collectionView?.showSingleImageSelectedCab()
    } else {
      collectionView?.hideCab()
    }
  }

}