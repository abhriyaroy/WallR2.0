package zebrostudio.wallr100.presentation.collection

import android.net.Uri
import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.minimal.SINGLE_ITEM_SIZE
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.WallpaperSetter
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
import java.util.TreeMap

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
  private val wallpaperSetter: WallpaperSetter,
  private val resourceUtils: ResourceUtils,
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

  override fun handleReorderImagesHintHintDismissed() {
    widgetHintsUseCase.saveCollectionsImageReorderHintShown()
  }

  override fun handleItemMoved(
    fromPosition: Int,
    toPosition: Int,
    imagePathList: MutableList<CollectionsPresenterEntity>
  ) {
    if (fromPosition < toPosition) {
      for (i in fromPosition until toPosition) {
        Collections.swap(imagePathList, i, i + 1)
      }
    } else {
      for (i in fromPosition downTo toPosition + 1) {
        Collections.swap(imagePathList, i, i - 1)
      }
    }
    collectionView?.updateItemViewMovement(fromPosition, toPosition)
    collectionImagesUseCase.reorderImage(
        collectionImagesPresenterEntityMapper.mapFromPresenterEntity(imagePathList))
        .observeOn(postExecutionThread.scheduler)
        .map {
          collectionImagesPresenterEntityMapper.mapToPresenterEntity(it)
        }
        .autoDisposable(collectionView!!.getScope())
        .subscribe({
          collectionView?.setImagesList(it)
          collectionView?.showReorderSuccessMessage()
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
    if (selectedItemsMap.isEmpty()) {
      collectionView?.hideAppBar()
    }
    if (selectedItemsMap.containsKey(position)) {
      selectedItemsMap.remove(position)
    } else {
      selectedItemsMap[position] = imageList[position]
    }
    updateSelectionChangesInCab(position, selectedItemsMap.size)
  }

  override fun notifyDragStarted() {
    hideCabIfActive()
  }

  override fun handleAutomaticWallpaperChangerEnabled() {

  }

  override fun handleAutomaticWallpaperChangerDisabled() {

  }

  override fun handleAutomaticWallpaperChangerIntervalMenuItemClicked() {
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

  override fun updateWallpaperChangerInterval(choice: Int) {
    imageOptionsUseCase.setAutomaticWallpaperChangerInterval(wallpaperChangerIntervals[choice])
    // Restart service for changing wallpaper
  }

  override fun handleImagePickerResult(uriList: List<Uri>) {
    if (uriList.isNotEmpty()) {
      collectionImagesUseCase.addImage(uriList)
          .map {
            collectionImagesPresenterEntityMapper.mapToPresenterEntity(it)
          }
          .observeOn(postExecutionThread.scheduler)
          .autoDisposable(collectionView!!.getScope())
          .subscribe({
            collectionView?.setImagesList(it)
            collectionView?.updateChangesInEveryItemView()
            uriList.size.let {
              if (it == SINGLE_ITEM_SIZE) {
                collectionView?.showSingleImageAddedSuccessfullyMessage()
              } else {
                collectionView?.showMultipleImagesAddedSuccessfullyMessage(it)
              }
            }
          }, {
            println(it.message)
            collectionView?.showGenericErrorMessage()
          })
    }
  }

  override fun handleSetWallpaperMenuItemClicked(selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>) {
    collectionImagesUseCase.getImageBitmap(
        collectionImagesPresenterEntityMapper.mapFromPresenterEntity(
            listOf(selectedItemsMap.values.first())).first())
        .doOnSuccess {
          wallpaperSetter.setWallpaper(it)
        }
        .observeOn(postExecutionThread.scheduler)
        .doOnSubscribe {
          collectionView?.blurScreen()
          collectionView?.showIndefiniteLoaderWithMessage(
              resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        }
        .autoDisposable(collectionView!!.getScope())
        .subscribe({
          hideCabIfActive()
          collectionView?.removeBlurFromScreen()
          collectionView?.showSetWallpaperSuccessMessage()
        }, {
          hideCabIfActive()
          collectionView?.removeBlurFromScreen()
          collectionView?.showGenericErrorMessage()
        })
  }

  override fun handleCrystallizeWallpaperMenuItemClicked(
    selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>
  ) {
    collectionImagesUseCase.saveCrystallizedImage(
        collectionImagesPresenterEntityMapper.mapFromPresenterEntity(
            listOf(selectedItemsMap.values.first())).first())
        .observeOn(postExecutionThread.scheduler)
        .doOnSubscribe {
          collectionView?.blurScreen()
          collectionView?.showIndefiniteLoaderWithMessage(
              resourceUtils.getStringResource(R.string.crystallizing_wallpaper_wait_message)
          )
        }.autoDisposable(collectionView!!.getScope())
        .subscribe({
          hideCabIfActive()
          collectionView?.updateChangesInEveryItemView()
          collectionView?.removeBlurFromScreen()
          collectionView?.showCrystallizeWallpaperSuccessMessage()
        }, {
          hideCabIfActive()
          collectionView?.removeBlurFromScreen()
          collectionView?.showGenericErrorMessage()
        })
  }

  override fun handleDeleteWallpaperMenuItemClicked(
    imageList: MutableList<CollectionsPresenterEntity>,
    selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>
  ) {
    val reversedSelectedItems = TreeMap<Int, CollectionsPresenterEntity>(Collections.reverseOrder())
    selectedItemsMap.keys.forEach {
      reversedSelectedItems[it] = selectedItemsMap[it]!!
    }
    mutableListOf<CollectionsPresenterEntity>().let { listOfDeletableImages ->
      reversedSelectedItems.keys.forEach {
        imageList.removeAt(it)
        listOfDeletableImages.add(selectedItemsMap[it]!!)
        selectedItemsMap.remove(it)
        collectionView?.removeItemView(it)
      }
      collectionImagesUseCase.deleteImages(
          collectionImagesPresenterEntityMapper.mapFromPresenterEntity(listOfDeletableImages))
          .map {
            collectionImagesPresenterEntityMapper.mapToPresenterEntity(it)
          }
          .observeOn(postExecutionThread.scheduler)
          .autoDisposable(collectionView!!.getScope())
          .subscribe({
            hideCabIfActive()
            collectionView?.setImagesList(it)
            listOfDeletableImages.size.let {
              if (it == SINGLE_ITEM_SIZE) {
                collectionView?.showSingleImageDeleteSuccessMessage()
              } else {
                collectionView?.showMultipleImageDeleteSuccessMessage(it)
              }
            }
          }, {
            hideCabIfActive()
            collectionView?.showUnableToDeleteErrorMessage()
          })
    }
  }

  override fun handleCabDestroyed() {
    collectionView?.clearAllSelectedItems()
    collectionView?.updateChangesInEveryItemView()
    collectionView?.showAppBar()
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
    collectionView?.updateChangesInSingleItemView(position)
    if (selectedMapSize > MINIMUM_NUMBER_OF_SELECTED_ITEMS) {
      collectionView?.showMultipleImagesSelectedCab()
    } else if (selectedMapSize == MINIMUM_NUMBER_OF_SELECTED_ITEMS) {
      collectionView?.showSingleImageSelectedCab()
    } else {
      collectionView?.hideCab()
    }
  }

  private fun hideCabIfActive() {
    if (collectionView?.isCabActive() == true) {
      collectionView?.hideCab()
    }
  }

}