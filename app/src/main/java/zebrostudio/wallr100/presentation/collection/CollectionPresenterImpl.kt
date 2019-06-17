package zebrostudio.wallr100.presentation.collection

import android.net.Uri
import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.permissions.PermissionsChecker
import zebrostudio.wallr100.android.service.WALLPAPER_CHANGER_INTERVALS_LIST
import zebrostudio.wallr100.android.system.SystemInfoProvider
import zebrostudio.wallr100.android.ui.collection.MANUFACTURER_NAME_ASUS
import zebrostudio.wallr100.android.ui.collection.MANUFACTURER_NAME_ONEPLUS
import zebrostudio.wallr100.android.ui.collection.MANUFACTURER_NAME_OPPO
import zebrostudio.wallr100.android.ui.collection.MANUFACTURER_NAME_SAMSUNG
import zebrostudio.wallr100.android.ui.collection.MANUFACTURER_NAME_VIVO
import zebrostudio.wallr100.android.ui.collection.MANUFACTURER_NAME_XIAOMI
import zebrostudio.wallr100.android.ui.minimal.SINGLE_ITEM_SIZE
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.android.utils.equalsIgnoreCase
import zebrostudio.wallr100.data.exception.AlreadyPresentInCollectionException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerIntervalUpdateResultState.INTERVAL_UPDATED
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerIntervalUpdateResultState.SERVICE_RESTARTED
import zebrostudio.wallr100.domain.interactor.CollectionImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionPresenter
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionView
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity
import zebrostudio.wallr100.presentation.collection.mapper.CollectionImagesPresenterEntityMapper
import java.util.Collections
import java.util.TreeMap

private const val MINIMUM_LIST_SIZE_REQUIRED_TO_SHOW_HINT = 2
private const val MINIMUM_LIST_SIZE_REQUIRED_TO_SHOW_WALLPAPER_CHANGER_LAYOUT = 2
private const val MINIMUM_NUMBER_OF_SELECTED_ITEMS = 1
private const val INDEX_OF_THIRTY_MINUTES_WALLPAPER_CHANGER_INTERVAL = 0

class CollectionPresenterImpl(
  private val widgetHintsUseCase: WidgetHintsUseCase,
  private val userPremiumStatusUseCase: UserPremiumStatusUseCase,
  private val collectionImagesUseCase: CollectionImagesUseCase,
  private val collectionImagesPresenterEntityMapper: CollectionImagesPresenterEntityMapper,
  private val wallpaperSetter: WallpaperSetter,
  private val resourceUtils: ResourceUtils,
  private val postExecutionThread: PostExecutionThread,
  private val permissionsChecker: PermissionsChecker,
  private val systemInfoProvider: SystemInfoProvider
) : CollectionPresenter {

  private var collectionView: CollectionView? = null

  override fun attachView(view: CollectionView) {
    collectionView = view
  }

  override fun detachView() {
    collectionView = null
  }

  override fun handleViewCreated() {
    if (isUserPremium() && isStoragePermissionAvailable()) {
      showPictures()
    }
  }

  override fun handleActivityResult() {
    handleViewCreated()
  }

  override fun handleImportFromLocalStorageClicked() {
    if (isUserPremium() && isStoragePermissionAvailable()) {
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
    mutableListOf<CollectionsPresenterEntity>().apply {
      addAll(imagePathList)
    }.let { copyOfImageListPriorToReordering ->
      reorderImageList(imagePathList, fromPosition, toPosition)
      collectionView?.updateItemViewMovement(fromPosition, toPosition)
      collectionImagesUseCase.reorderImage(
          collectionImagesPresenterEntityMapper.mapFromPresenterEntity(imagePathList))
          .observeOn(postExecutionThread.scheduler)
          .map {
            collectionImagesPresenterEntityMapper.mapToPresenterEntity(it)
          }
          .autoDisposable(collectionView!!.getScope())
          .subscribe({
            handleItemMovedSuccess(it)
            copyOfImageListPriorToReordering.clear()
          }, {
            handleItemMovedError(copyOfImageListPriorToReordering)
          })
    }
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
    collectionImagesUseCase.saveAutomaticWallpaperChangerStateAsEnabled()
    if (!collectionImagesUseCase.isAutomaticWallpaperChangerRunning()) {
      systemInfoProvider.getManufacturerName().let {
        if (it.equalsIgnoreCase(MANUFACTURER_NAME_SAMSUNG)
            || it.equalsIgnoreCase(MANUFACTURER_NAME_XIAOMI)
            || it.equalsIgnoreCase(MANUFACTURER_NAME_ONEPLUS)
            || it.equalsIgnoreCase(MANUFACTURER_NAME_OPPO)
            || it.equalsIgnoreCase(MANUFACTURER_NAME_VIVO)
            || it.equalsIgnoreCase(MANUFACTURER_NAME_ASUS)) {
          collectionView?.showWallpaperChangerPermissionsRequiredDialog()
        }
      }
    }
    collectionImagesUseCase.startAutomaticWallpaperChanger()
  }

  override fun handleAutomaticWallpaperChangerDisabled() {
    collectionImagesUseCase.saveAutomaticWallpaperChangerStateAsDisabled()
    collectionImagesUseCase.stopAutomaticWallpaperChanger()
  }

  override fun handleAutomaticWallpaperChangerIntervalMenuItemClicked() {
    collectionImagesUseCase.getAutomaticWallpaperChangerInterval().let {
      var isDialogShown = false
      WALLPAPER_CHANGER_INTERVALS_LIST.forEachIndexed { index, interval ->
        if (it == interval) {
          collectionView?.showWallpaperChangerIntervalDialog(index)
          isDialogShown = true
        }
      }
      if (!isDialogShown) {
        collectionImagesUseCase.setAutomaticWallpaperChangerInterval(
            WALLPAPER_CHANGER_INTERVALS_LIST.first())
        collectionView?.showWallpaperChangerIntervalDialog(
            INDEX_OF_THIRTY_MINUTES_WALLPAPER_CHANGER_INTERVAL)
      }
    }
  }

  override fun updateWallpaperChangerInterval(choice: Int) {
    when (collectionImagesUseCase.setAutomaticWallpaperChangerInterval(
        WALLPAPER_CHANGER_INTERVALS_LIST[choice])) {
      INTERVAL_UPDATED -> collectionView?.showWallpaperChangerIntervalUpdatedSuccessMessage()
      SERVICE_RESTARTED -> collectionView?.showWallpaperChangerRestartedSuccessMessage()
    }
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
            showNonEmptyCollectionView(it.size)
            showHintIfSuitable(it.size)
            uriList.size.let {
              if (it == SINGLE_ITEM_SIZE) {
                collectionView?.showSingleImageAddedSuccessfullyMessage()
              } else {
                collectionView?.showMultipleImagesAddedSuccessfullyMessage(it)
              }
            }
          }, {
            collectionView?.showGenericErrorMessage()
          })
    }
  }

  override fun handleSetWallpaperMenuItemClicked(selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>) {
    collectionImagesUseCase.getImageBitmap(
        collectionImagesPresenterEntityMapper.mapFromPresenterEntity(
            selectedItemsMap.values.first())
            .first())
        .doOnSuccess {
          wallpaperSetter.setWallpaper(it)
        }
        .observeOn(postExecutionThread.scheduler)
        .doOnSubscribe {
          doOnSetWallpaperSubscription()
        }
        .autoDisposable(collectionView!!.getScope())
        .subscribe({
          handleSetWallpaperSuccess()
        }, {
          handleSetWallpaperError()
        })
  }

  override fun handleCrystallizeWallpaperMenuItemClicked(
    selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>
  ) {
    collectionImagesUseCase.saveCrystallizedImage(
        collectionImagesPresenterEntityMapper.mapFromPresenterEntity(
            selectedItemsMap.values.first()).first())
        .map {
          collectionImagesPresenterEntityMapper.mapToPresenterEntity(it)
        }
        .observeOn(postExecutionThread.scheduler)
        .doOnSubscribe {
          handleCrystallizationDoOnSubscribe()
        }.autoDisposable(collectionView!!.getScope())
        .subscribe({
          redecorateViewAfterCrystallization(it)
        }, {
          handleCrystallizationOnError(it)
        })
  }

  override fun handleDeleteWallpaperMenuItemClicked(
    imageList: MutableList<CollectionsPresenterEntity>,
    selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>
  ) {
    mutableListOf<CollectionsPresenterEntity>().apply {
      addAll(imageList)
    }.let { backupOfOriginalImageList ->
      reverseSortSelections(selectedItemsMap).let { reverseSortedSelections ->
        mutableListOf<CollectionsPresenterEntity>().let { listOfDeletableImages ->
          removeItemsFromList(reverseSortedSelections, imageList, listOfDeletableImages,
              selectedItemsMap)
          deleteWallpapers(backupOfOriginalImageList, listOfDeletableImages)
        }
      }
    }
  }

  override fun handleCabDestroyed() {
    collectionView?.clearAllSelectedItems()
    collectionView?.updateChangesInEveryItemView()
    collectionView?.enableToolbar()
    collectionView?.showAppBarWithDelay()
  }

  private fun isUserPremium(): Boolean {
    return if (userPremiumStatusUseCase.isUserPremium()) {
      true
    } else {
      collectionView?.showPurchaseProToContinueDialog()
      false
    }
  }

  private fun isStoragePermissionAvailable(): Boolean {
    return if (permissionsChecker.isReadPermissionAvailable()
        && permissionsChecker.isWritePermissionAvailable()) {
      true
    } else {
      collectionView?.requestStoragePermission()
      false
    }
  }

  private fun showPictures() {
    collectionImagesUseCase.getAllImages()
        .map {
          collectionImagesPresenterEntityMapper.mapToPresenterEntity(it)
        }
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(collectionView!!.getScope())
        .subscribe({
          if (it.isNotEmpty()) {
            collectionView?.setImagesList(it)
            showNonEmptyCollectionView(it.size)
            showHintIfSuitable(it.size)
            collectionView?.updateChangesInEveryItemView()
          } else {
            showEmptyCollectionView()
            stopWallpaperChangerAndRemoveLayout()
          }
        }, {
          stopWallpaperChangerAndRemoveLayout()
          collectionView?.showImagesAbsentLayout()
          collectionView?.showGenericErrorMessage()
        })
  }

  private fun showHintIfSuitable(listSize: Int) {
    if (listSize >= MINIMUM_LIST_SIZE_REQUIRED_TO_SHOW_HINT
        && !widgetHintsUseCase.isCollectionsImageReorderHintShown()) {
      collectionView?.showReorderImagesHintWithDelay()
    }
  }

  private fun updateSelectionChangesInCab(position: Int, selectedMapSize: Int) {
    collectionView?.updateChangesInItemView(position)
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

  private fun reorderImageList(
    imagePathList: MutableList<CollectionsPresenterEntity>,
    fromPosition: Int,
    toPosition: Int
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
  }

  private fun showEmptyCollectionView() {
    collectionView?.clearImages()
    collectionView?.clearAllSelectedItems()
    collectionView?.showImagesAbsentLayout()
    stopWallpaperChangerAndRemoveLayout()
  }

  private fun showNonEmptyCollectionView(size: Int) {
    collectionView?.hideImagesAbsentLayout()
    if (size >= MINIMUM_LIST_SIZE_REQUIRED_TO_SHOW_WALLPAPER_CHANGER_LAYOUT) {
      collectionView?.showWallpaperChangerLayout()
      if (collectionImagesUseCase.isAutomaticWallpaperChangerRunning()) {
        collectionView?.showAutomaticWallpaperStateAsActive()
      } else {
        collectionView?.showAutomaticWallpaperStateAsInActive()
      }
    } else {
      stopWallpaperChangerAndRemoveLayout()
    }
  }

  private fun handleItemMovedSuccess(list: List<CollectionsPresenterEntity>) {
    collectionView?.setImagesList(list)
    collectionView?.updateChangesInEveryItemViewWithDelay()
    collectionView?.showReorderSuccessMessage()
  }

  private fun handleItemMovedError(originalList: MutableList<CollectionsPresenterEntity>) {
    collectionView?.setImagesList(originalList)
    collectionView?.updateChangesInEveryItemView()
    collectionView?.showUnableToReorderErrorMessage()
  }

  private fun redecorateViewAfterCrystallization(list: List<CollectionsPresenterEntity>) {
    hideCabIfActive()
    collectionView?.setImagesList(list)
    collectionView?.updateChangesInEveryItemView()
    collectionView?.removeBlurFromScreen()
    collectionView?.showCrystallizeSuccessMessage()
    collectionView?.enableBackPress()
  }

  private fun handleCrystallizationDoOnSubscribe() {
    collectionView?.blurScreen()
    collectionView?.showIndefiniteLoaderWithMessage(
        resourceUtils.getStringResource(R.string.crystallizing_wallpaper_wait_message)
    )
    collectionView?.disableBackPress()
  }

  private fun handleCrystallizationOnError(throwable: Throwable) {
    hideCabIfActive()
    collectionView?.removeBlurFromScreen()
    if (throwable is AlreadyPresentInCollectionException) {
      collectionView?.showCrystallizedImageAlreadyPresentInCollectionErrorMessage()
    } else {
      collectionView?.showGenericErrorMessage()
    }
    collectionView?.enableBackPress()
  }

  private fun reverseSortSelections(map: HashMap<Int, CollectionsPresenterEntity>): TreeMap<Int, CollectionsPresenterEntity> {
    return TreeMap<Int, CollectionsPresenterEntity>(Collections.reverseOrder()).let { treeMap ->
      map.keys.forEach {
        treeMap[it] = map[it]!!
      }
      treeMap
    }
  }

  private fun removeItemsFromList(
    reverseSortedSelections: TreeMap<Int, CollectionsPresenterEntity>,
    imageList: MutableList<CollectionsPresenterEntity>,
    listOfDeletableImages: MutableList<CollectionsPresenterEntity>,
    selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>
  ) {
    reverseSortedSelections.keys.forEach {
      imageList.removeAt(it)
      listOfDeletableImages.add(selectedItemsMap[it]!!)
      selectedItemsMap.remove(it)
      collectionView?.removeItemView(it)
    }
  }

  private fun deleteWallpapers(
    backupOfOriginalImageList: List<CollectionsPresenterEntity>,
    listOfDeletableImages: List<CollectionsPresenterEntity>
  ) {
    collectionImagesUseCase.deleteImages(
        collectionImagesPresenterEntityMapper.mapFromPresenterEntity(listOfDeletableImages))
        .onErrorResumeNext(
            collectionImagesUseCase.reorderImage(collectionImagesPresenterEntityMapper
                .mapFromPresenterEntity(backupOfOriginalImageList)))
        .map {
          collectionImagesPresenterEntityMapper.mapToPresenterEntity(it)
        }
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(collectionView!!.getScope())
        .subscribe({
          handleDeleteWallpaperSuccess(it, backupOfOriginalImageList, listOfDeletableImages)
          hideCabIfActive()
        }, {})
  }

  private fun handleDeleteWallpaperSuccess(
    it: List<CollectionsPresenterEntity>,
    backupOfOriginalImageList: List<CollectionsPresenterEntity>,
    listOfDeletableImages: List<CollectionsPresenterEntity>
  ) {
    if (it.size == backupOfOriginalImageList.size) {
      restoreViewAfterUnsuccessfulDeletion(it)
    } else {
      redecorateViewAfterDeletion(it, listOfDeletableImages)
      stopWallpaperChangerIfNecessary(it.size)
    }
  }

  private fun redecorateViewAfterDeletion(
    collectionPresenterEntityList: List<CollectionsPresenterEntity>,
    deletableItemsList: List<CollectionsPresenterEntity>
  ) {
    collectionView?.setImagesList(collectionPresenterEntityList)
    if (collectionPresenterEntityList.isEmpty()) {
      showEmptyCollectionView()
    }
    deletableItemsList.size.let {
      if (it == SINGLE_ITEM_SIZE) {
        collectionView?.showSingleImageDeleteSuccessMessage()
      } else {
        collectionView?.showMultipleImageDeleteSuccessMessage(it)
      }
    }
  }

  private fun restoreViewAfterUnsuccessfulDeletion(originalList: List<CollectionsPresenterEntity>) {
    collectionView?.setImagesList(originalList)
    collectionView?.updateChangesInEveryItemView()
    collectionView?.showUnableToDeleteErrorMessage()
  }

  private fun stopWallpaperChangerIfNecessary(size: Int) {
    if (size < MINIMUM_LIST_SIZE_REQUIRED_TO_SHOW_WALLPAPER_CHANGER_LAYOUT) {
      stopWallpaperChangerAndRemoveLayout()
    }
  }

  private fun stopWallpaperChangerAndRemoveLayout() {
    collectionView?.hideWallpaperChangerLayout()
    collectionImagesUseCase.stopAutomaticWallpaperChanger()
  }

  private fun doOnSetWallpaperSubscription() {
    collectionView?.blurScreen()
    collectionView?.showIndefiniteLoaderWithMessage(
        resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
    collectionView?.disableBackPress()
  }

  private fun handleSetWallpaperSuccess() {
    hideCabIfActive()
    collectionView?.removeBlurFromScreen()
    collectionView?.showSetWallpaperSuccessMessage()
    collectionView?.enableBackPress()
  }

  private fun handleSetWallpaperError() {
    hideCabIfActive()
    collectionView?.removeBlurFromScreen()
    collectionView?.showGenericErrorMessage()
    collectionView?.enableBackPress()
  }

}