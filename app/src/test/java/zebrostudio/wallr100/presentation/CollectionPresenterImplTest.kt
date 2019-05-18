package zebrostudio.wallr100.presentation

import android.graphics.Bitmap
import android.net.Uri
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.data.exception.AlreadyPresentInCollectionException
import zebrostudio.wallr100.domain.datafactory.CollectionsImageModelFactory.getCollectionsImageModel
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerIntervalUpdateResultState.INTERVAL_UPDATED
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerIntervalUpdateResultState.SERVICE_RESTARTED
import zebrostudio.wallr100.domain.interactor.CollectionImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionView
import zebrostudio.wallr100.presentation.collection.CollectionPresenterImpl
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity
import zebrostudio.wallr100.presentation.collection.mapper.CollectionImagesPresenterEntityMapper
import zebrostudio.wallr100.presentation.datafactory.CollectionImagesPresenterEntityFactory.getCollectionImagesPresenterEntity
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class CollectionPresenterImplTest {

  @Mock lateinit var widgetHintsUseCase: WidgetHintsUseCase
  @Mock lateinit var userPremiumStatusUseCase: UserPremiumStatusUseCase
  @Mock lateinit var collectionImagesUseCase: CollectionImagesUseCase
  @Mock lateinit var collectionImagesPresenterEntityMapper: CollectionImagesPresenterEntityMapper
  @Mock lateinit var wallpaperSetter: WallpaperSetter
  @Mock lateinit var resourceUtils: ResourceUtils
  @Mock lateinit var postExecutionThread: PostExecutionThread
  @Mock lateinit var collectionView: CollectionView
  @Mock lateinit var mockUri: Uri
  @Mock lateinit var mockBitmap: Bitmap
  private val randomString = randomUUID().toString()
  private lateinit var collectionPresenterImpl: CollectionPresenterImpl

  @Before fun setUp() {
    collectionPresenterImpl = CollectionPresenterImpl(widgetHintsUseCase, userPremiumStatusUseCase,
        collectionImagesUseCase, collectionImagesPresenterEntityMapper, wallpaperSetter,
        resourceUtils, postExecutionThread)

    val testScopeProvider = TestLifecycleScopeProvider.createInitial(
        TestLifecycleScopeProvider.TestLifecycle.STARTED)
    Mockito.`when`(collectionView.getScope()).thenReturn(testScopeProvider)
    whenever(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())

    collectionPresenterImpl.attachView(collectionView)
  }

  @Test
  fun `should show purchase pro to continue dialog on handleViewCreated call failure due to non pro user`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(false)

    collectionPresenterImpl.handleViewCreated()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionView).showPurchaseProToContinueDialog()
  }

  @Test
  fun `should request storage permission on handleViewCreated call failure due to missing storage permission`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(false)

    collectionPresenterImpl.handleViewCreated()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).requestStoragePermission()
  }

  @Test
  fun `should set automatic wallpaper changer as active and show pictures on handleViewCreated call success`() {
    val collectionsImageModelList = listOf(getCollectionsImageModel())
    val collectionsPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity())
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(true)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(
        Single.just(collectionsImageModelList))
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(
        collectionsImageModelList)).thenReturn(collectionsPresenterEntityList)

    collectionPresenterImpl.handleViewCreated()

    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(collectionsImageModelList)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsActive()
    verify(collectionView).getScope()
    verify(collectionView).setImagesList(collectionsPresenterEntityList)
    verify(collectionView).hideImagesAbsentLayout()
    verify(collectionView).showWallpaperChangerLayout()
    verify(collectionView).updateChangesInEveryItemView()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as active and show pictures and hint on handleViewCreated call success`() {
    val collectionsImageModelList = listOf(getCollectionsImageModel(), getCollectionsImageModel())
    val collectionsPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity(),
            getCollectionImagesPresenterEntity())
    `when`(widgetHintsUseCase.isCollectionsImageReorderHintShown()).thenReturn(false)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(true)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(
        Single.just(collectionsImageModelList))
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(
        collectionsImageModelList)).thenReturn(collectionsPresenterEntityList)

    collectionPresenterImpl.handleViewCreated()

    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(collectionsImageModelList)
    verify(widgetHintsUseCase).isCollectionsImageReorderHintShown()
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsActive()
    verify(collectionView).getScope()
    verify(collectionView).showReorderImagesHintWithDelay()
    verify(collectionView).setImagesList(collectionsPresenterEntityList)
    verify(collectionView).hideImagesAbsentLayout()
    verify(collectionView).showWallpaperChangerLayout()
    verify(collectionView).updateChangesInEveryItemView()
    verify(collectionView).showReorderImagesHintWithDelay()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as active and show images absent layout on handleViewCreated call failure`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(true)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(Single.error(Exception()))

    collectionPresenterImpl.handleViewCreated()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsActive()
    verify(collectionView).getScope()
    verify(collectionView).showImagesAbsentLayout()
    verify(collectionView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as inactive and show pictures on handleViewCreated call success`() {
    val collectionsImageModelList = listOf(getCollectionsImageModel())
    val collectionsPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity())
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(false)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(
        Single.just(collectionsImageModelList))
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(
        collectionsImageModelList)).thenReturn(collectionsPresenterEntityList)

    collectionPresenterImpl.handleViewCreated()

    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(collectionsImageModelList)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsInActive()
    verify(collectionView).getScope()
    verify(collectionView).setImagesList(collectionsPresenterEntityList)
    verify(collectionView).hideImagesAbsentLayout()
    verify(collectionView).showWallpaperChangerLayout()
    verify(collectionView).updateChangesInEveryItemView()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as inactive and show pictures and hint on handleViewCreated call success`() {
    val collectionsImageModelList = listOf(getCollectionsImageModel(), getCollectionsImageModel())
    val collectionsPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity(),
            getCollectionImagesPresenterEntity())
    `when`(widgetHintsUseCase.isCollectionsImageReorderHintShown()).thenReturn(false)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(false)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(
        Single.just(collectionsImageModelList))
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(
        collectionsImageModelList)).thenReturn(collectionsPresenterEntityList)

    collectionPresenterImpl.handleViewCreated()

    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(collectionsImageModelList)
    verify(widgetHintsUseCase).isCollectionsImageReorderHintShown()
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsInActive()
    verify(collectionView).getScope()
    verify(collectionView).showReorderImagesHintWithDelay()
    verify(collectionView).setImagesList(collectionsPresenterEntityList)
    verify(collectionView).hideImagesAbsentLayout()
    verify(collectionView).showWallpaperChangerLayout()
    verify(collectionView).updateChangesInEveryItemView()
    verify(collectionView).showReorderImagesHintWithDelay()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as inactive and show empty collection view on handleViewCreated call success due to empty collection`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(false)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(Single.just(listOf()))
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(listOf()))
        .thenReturn(listOf())

    collectionPresenterImpl.handleViewCreated()

    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(listOf())
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsInActive()
    verify(collectionView).getScope()
    verify(collectionView).clearImages()
    verify(collectionView).clearAllSelectedItems()
    verify(collectionView).showImagesAbsentLayout()
    verify(collectionView).hideWallpaperChangerLayout()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as inactive and images show absent layout on handleViewCreated call failure`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(false)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(Single.error(Exception()))

    collectionPresenterImpl.handleViewCreated()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsInActive()
    verify(collectionView).getScope()
    verify(collectionView).showImagesAbsentLayout()
    verify(collectionView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as active and show pictures on handleActivityResult call failure due to missing storage permission`() {
    val collectionsImageModelList = listOf(getCollectionsImageModel())
    val collectionsPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity())
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(true)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(
        Single.just(collectionsImageModelList))
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(
        collectionsImageModelList)).thenReturn(collectionsPresenterEntityList)

    collectionPresenterImpl.handleActivityResult()

    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(collectionsImageModelList)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsActive()
    verify(collectionView).getScope()
    verify(collectionView).setImagesList(collectionsPresenterEntityList)
    verify(collectionView).hideImagesAbsentLayout()
    verify(collectionView).showWallpaperChangerLayout()
    verify(collectionView).updateChangesInEveryItemView()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as active and show pictures and hint on handleActivityResult call failure due to missing storage permission`() {
    val collectionsImageModelList = listOf(getCollectionsImageModel(), getCollectionsImageModel())
    val collectionsPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity(),
            getCollectionImagesPresenterEntity())
    `when`(widgetHintsUseCase.isCollectionsImageReorderHintShown()).thenReturn(false)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(true)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(
        Single.just(collectionsImageModelList))
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(
        collectionsImageModelList)).thenReturn(collectionsPresenterEntityList)

    collectionPresenterImpl.handleActivityResult()

    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(collectionsImageModelList)
    verify(widgetHintsUseCase).isCollectionsImageReorderHintShown()
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsActive()
    verify(collectionView).getScope()
    verify(collectionView).showReorderImagesHintWithDelay()
    verify(collectionView).setImagesList(collectionsPresenterEntityList)
    verify(collectionView).hideImagesAbsentLayout()
    verify(collectionView).showWallpaperChangerLayout()
    verify(collectionView).updateChangesInEveryItemView()
    verify(collectionView).showReorderImagesHintWithDelay()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as active and images absent layout on handleActivityResult call failure`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(true)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(Single.error(Exception()))

    collectionPresenterImpl.handleActivityResult()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsActive()
    verify(collectionView).getScope()
    verify(collectionView).showImagesAbsentLayout()
    verify(collectionView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as inactive and show pictures on handleActivityResult call failure due to missing storage permission`() {
    val collectionsImageModelList = listOf(getCollectionsImageModel())
    val collectionsPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity())
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(false)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(
        Single.just(collectionsImageModelList))
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(
        collectionsImageModelList)).thenReturn(collectionsPresenterEntityList)

    collectionPresenterImpl.handleActivityResult()

    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(collectionsImageModelList)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsInActive()
    verify(collectionView).getScope()
    verify(collectionView).setImagesList(collectionsPresenterEntityList)
    verify(collectionView).hideImagesAbsentLayout()
    verify(collectionView).showWallpaperChangerLayout()
    verify(collectionView).updateChangesInEveryItemView()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as inactive and show pictures and hint on handleActivityResult call failure due to missing storage permission`() {
    val collectionsImageModelList = listOf(getCollectionsImageModel(), getCollectionsImageModel())
    val collectionsPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity(),
            getCollectionImagesPresenterEntity())
    `when`(widgetHintsUseCase.isCollectionsImageReorderHintShown()).thenReturn(false)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(false)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(
        Single.just(collectionsImageModelList))
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(
        collectionsImageModelList)).thenReturn(collectionsPresenterEntityList)

    collectionPresenterImpl.handleActivityResult()

    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(collectionsImageModelList)
    verify(widgetHintsUseCase).isCollectionsImageReorderHintShown()
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsInActive()
    verify(collectionView).getScope()
    verify(collectionView).showReorderImagesHintWithDelay()
    verify(collectionView).setImagesList(collectionsPresenterEntityList)
    verify(collectionView).hideImagesAbsentLayout()
    verify(collectionView).showWallpaperChangerLayout()
    verify(collectionView).updateChangesInEveryItemView()
    verify(collectionView).showReorderImagesHintWithDelay()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as inactive and show empty collection view on handleActivityResult call success due to empty collection`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(false)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(Single.just(listOf()))
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(listOf()))
        .thenReturn(listOf())

    collectionPresenterImpl.handleActivityResult()

    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(listOf())
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsInActive()
    verify(collectionView).getScope()
    verify(collectionView).clearImages()
    verify(collectionView).clearAllSelectedItems()
    verify(collectionView).showImagesAbsentLayout()
    verify(collectionView).hideWallpaperChangerLayout()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set automatic wallpaper changer as inactive and images absent layout on handleActivityResult call failure`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)
    `when`(collectionImagesUseCase.isAutomaticWallpaperChangerRunning()).thenReturn(false)
    `when`(collectionImagesUseCase.getAllImages()).thenReturn(Single.error(Exception()))

    collectionPresenterImpl.handleActivityResult()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionImagesUseCase).isAutomaticWallpaperChangerRunning()
    verify(collectionImagesUseCase).getAllImages()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showAutomaticWallpaperStateAsInActive()
    verify(collectionView).getScope()
    verify(collectionView).showImagesAbsentLayout()
    verify(collectionView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show purchase pro to continue dialog on handleImportFromLocalStorageClicked call failure due to non pro user`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(false)

    collectionPresenterImpl.handleImportFromLocalStorageClicked()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionView).showPurchaseProToContinueDialog()
  }

  @Test
  fun `should request storage permission on handleImportFromLocalStorageClicked call failure due to missing storage permission`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(false)

    collectionPresenterImpl.handleImportFromLocalStorageClicked()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).requestStoragePermission()
  }

  @Test
  fun `should show image picker on handleImportFromLocalStorageClicked call success`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(collectionView.hasStoragePermission()).thenReturn(true)

    collectionPresenterImpl.handleImportFromLocalStorageClicked()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(collectionView).hasStoragePermission()
    verify(collectionView).showImagePicker()
  }

  @Test fun `should redirect to buy pro on handlePurchaseClick call success`() {
    collectionPresenterImpl.handlePurchaseClicked()

    verify(collectionView).redirectToBuyPro()
  }

  @Test
  fun `should save reorder hint shown state on handleReorderImagesHintHintDismissed call success`() {
    collectionPresenterImpl.handleReorderImagesHintHintDismissed()

    verify(widgetHintsUseCase).saveCollectionsImageReorderHintShown()
  }

  @Test fun `should reorder images on handleItemMoved call success`() {
    val fromPosition = 0
    val toPosition = 2
    val firstItem = getCollectionImagesPresenterEntity()
    val secondItem = getCollectionImagesPresenterEntity()
    val thirdItem = getCollectionImagesPresenterEntity()
    val fourthItem = getCollectionImagesPresenterEntity()
    val originalList = mutableListOf(firstItem, secondItem, thirdItem, fourthItem)
    val reorderedList = listOf(secondItem, thirdItem, firstItem, fourthItem)
    val collectionImageModelList = listOf(getCollectionsImageModel())
    `when`(collectionImagesPresenterEntityMapper.mapFromPresenterEntity(originalList)).thenReturn(
        collectionImageModelList)
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(collectionImageModelList))
        .thenReturn(reorderedList)
    `when`(collectionImagesUseCase.reorderImage(collectionImageModelList))
        .thenReturn(Single.just(collectionImageModelList))

    collectionPresenterImpl.handleItemMoved(fromPosition, toPosition, originalList)

    verify(collectionImagesPresenterEntityMapper).mapFromPresenterEntity(originalList)
    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(collectionImageModelList)
    verify(collectionImagesUseCase).reorderImage(collectionImageModelList)
    verify(collectionView).getScope()
    verify(collectionView).updateItemViewMovement(fromPosition, toPosition)
    verify(collectionView).setImagesList(reorderedList)
    verify(collectionView).showReorderSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show failure message and restore images order on handleItemMoved call failure`() {
    val fromPosition = 0
    val toPosition = 2
    val firstItem = getCollectionImagesPresenterEntity()
    val secondItem = getCollectionImagesPresenterEntity()
    val thirdItem = getCollectionImagesPresenterEntity()
    val fourthItem = getCollectionImagesPresenterEntity()
    val originalList = mutableListOf(firstItem, secondItem, thirdItem, fourthItem)
    val restoredList = listOf(firstItem, secondItem, thirdItem, fourthItem)
    val collectionImageModelList = listOf(getCollectionsImageModel())
    `when`(collectionImagesPresenterEntityMapper.mapFromPresenterEntity(originalList)).thenReturn(
        collectionImageModelList)
    `when`(collectionImagesUseCase.reorderImage(collectionImageModelList))
        .thenReturn(Single.error(Exception()))

    collectionPresenterImpl.handleItemMoved(fromPosition, toPosition, originalList)

    verify(collectionImagesPresenterEntityMapper).mapFromPresenterEntity(originalList)
    verify(collectionImagesUseCase).reorderImage(collectionImageModelList)
    verify(collectionView).getScope()
    verify(collectionView).updateItemViewMovement(fromPosition, toPosition)
    verify(collectionView).setImagesList(restoredList)
    verify(collectionView).updateChangesInEveryItemView()
    verify(collectionView).showUnableToReorderErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should add image to selected items and update single image selection changes in cab on handleItemClicked call success`() {
    val position = 1
    val collectionPresenterEntityList = listOf(
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity())
    val selectedItemsMap = hashMapOf<Int, CollectionsPresenterEntity>()
    val resultantSelectedItemsMap = hashMapOf<Int, CollectionsPresenterEntity>()
    resultantSelectedItemsMap[position] = collectionPresenterEntityList[position]

    collectionPresenterImpl.handleItemClicked(position, collectionPresenterEntityList,
        selectedItemsMap)

    assertEquals(resultantSelectedItemsMap, selectedItemsMap)
    verify(collectionView).hideAppBar()
    verify(collectionView).updateChangesInItemView(position)
    verify(collectionView).showSingleImageSelectedCab()
  }

  @Test
  fun `should add image to selected items and update multiple image selection changes in cab on handleItemClicked call success`() {
    val position = 2
    val collectionPresenterEntityList = listOf(
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity())
    val selectedItemsMap = hashMapOf<Int, CollectionsPresenterEntity>()
    selectedItemsMap[1] = collectionPresenterEntityList[1]
    val resultantSelectedItemsMap = hashMapOf<Int, CollectionsPresenterEntity>()
    resultantSelectedItemsMap.putAll(selectedItemsMap)
    resultantSelectedItemsMap[position] = collectionPresenterEntityList[position]

    collectionPresenterImpl.handleItemClicked(position, collectionPresenterEntityList,
        selectedItemsMap)

    assertEquals(resultantSelectedItemsMap, selectedItemsMap)
    verify(collectionView).updateChangesInItemView(position)
    verify(collectionView).showMultipleImagesSelectedCab()
  }

  @Test
  fun `should remove image from selected items and update single image selection changes in cab on handleItemClicked call success`() {
    val position = 2
    val collectionPresenterEntityList = listOf(
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity())
    val selectedItemsMap = hashMapOf<Int, CollectionsPresenterEntity>()
    selectedItemsMap[1] = collectionPresenterEntityList[1]
    selectedItemsMap[position] = collectionPresenterEntityList[position]
    val resultantSelectedItemsMap = hashMapOf<Int, CollectionsPresenterEntity>()
    resultantSelectedItemsMap[1] = collectionPresenterEntityList[1]

    collectionPresenterImpl.handleItemClicked(position, collectionPresenterEntityList,
        selectedItemsMap)

    assertEquals(resultantSelectedItemsMap, selectedItemsMap)
    verify(collectionView).updateChangesInItemView(position)
    verify(collectionView).showSingleImageSelectedCab()
  }

  @Test
  fun `should remove image from selected items and update multiple image selection changes in cab on handleItemClicked call success`() {
    val position = 2
    val collectionPresenterEntityList = listOf(
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity())
    val selectedItemsMap = hashMapOf<Int, CollectionsPresenterEntity>()
    selectedItemsMap[1] = collectionPresenterEntityList[1]
    selectedItemsMap[3] = collectionPresenterEntityList[3]
    selectedItemsMap[position] = collectionPresenterEntityList[position]
    val resultantSelectedItemsMap = hashMapOf<Int, CollectionsPresenterEntity>()
    resultantSelectedItemsMap[1] = collectionPresenterEntityList[1]
    resultantSelectedItemsMap[3] = collectionPresenterEntityList[3]

    collectionPresenterImpl.handleItemClicked(position, collectionPresenterEntityList,
        selectedItemsMap)

    assertEquals(resultantSelectedItemsMap, selectedItemsMap)
    verify(collectionView).updateChangesInItemView(position)
    verify(collectionView).showMultipleImagesSelectedCab()
  }

  @Test
  fun `should remove image from selected items and hide cab on handleItemClicked call success`() {
    val position = 2
    val collectionPresenterEntityList = listOf(
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity(),
        getCollectionImagesPresenterEntity())
    val selectedItemsMap = hashMapOf<Int, CollectionsPresenterEntity>()
    selectedItemsMap[position] = collectionPresenterEntityList[position]
    val resultantSelectedItemsMap = hashMapOf<Int, CollectionsPresenterEntity>()

    collectionPresenterImpl.handleItemClicked(position, collectionPresenterEntityList,
        selectedItemsMap)

    assertEquals(resultantSelectedItemsMap, selectedItemsMap)
    verify(collectionView).updateChangesInItemView(position)
    verify(collectionView).hideCab()
  }

  @Test fun `should hide cab on notifyDragStarted call success as cab is active`() {
    `when`(collectionView.isCabActive()).thenReturn(true)

    collectionPresenterImpl.notifyDragStarted()

    verify(collectionView).isCabActive()
    verify(collectionView).hideCab()
  }

  @Test fun `should do nothing on notifyDragStarted call success as cab is not active`() {
    `when`(collectionView.isCabActive()).thenReturn(false)

    collectionPresenterImpl.notifyDragStarted()

    verify(collectionView).isCabActive()
  }

  @Test
  fun `should start automatic wallpaper changer on handleAutomaticWallpaperChangerEnabled call success on a first party oem`() {
    `when`(collectionView.getManufacturerName()).thenReturn(randomUUID().toString())
    collectionPresenterImpl.handleAutomaticWallpaperChangerEnabled()

    verify(collectionImagesUseCase).startAutomaticWallpaperChanger()
    verify(collectionView).getManufacturerName()
  }

  @Test
  fun `should start automatic wallpaper changer and show autostart hint dialog on handleAutomaticWallpaperChangerEnabled call success on a samsung oem`() {
    `when`(collectionView.getManufacturerName()).thenReturn("samsung")
    collectionPresenterImpl.handleAutomaticWallpaperChangerEnabled()

    verify(collectionImagesUseCase).startAutomaticWallpaperChanger()
    verify(collectionView).getManufacturerName()
    verify(collectionView).showWallpaperChangerPermissionsRequiredDialog()
  }

  @Test
  fun `should start automatic wallpaper changer and show autostart hint dialog on handleAutomaticWallpaperChangerEnabled call success on a xiaomi oem`() {
    `when`(collectionView.getManufacturerName()).thenReturn("xiaomi")
    collectionPresenterImpl.handleAutomaticWallpaperChangerEnabled()

    verify(collectionImagesUseCase).startAutomaticWallpaperChanger()
    verify(collectionView).getManufacturerName()
    verify(collectionView).showWallpaperChangerPermissionsRequiredDialog()
  }

  @Test
  fun `should start automatic wallpaper changer and show autostart hint dialog on handleAutomaticWallpaperChangerEnabled call success on a oneplus oem`() {
    `when`(collectionView.getManufacturerName()).thenReturn("oneplus")
    collectionPresenterImpl.handleAutomaticWallpaperChangerEnabled()

    verify(collectionImagesUseCase).startAutomaticWallpaperChanger()
    verify(collectionView).getManufacturerName()
    verify(collectionView).showWallpaperChangerPermissionsRequiredDialog()
  }

  @Test
  fun `should start automatic wallpaper changer and show autostart hint dialog on handleAutomaticWallpaperChangerEnabled call success on a oppo oem`() {
    `when`(collectionView.getManufacturerName()).thenReturn("oppo")
    collectionPresenterImpl.handleAutomaticWallpaperChangerEnabled()

    verify(collectionImagesUseCase).startAutomaticWallpaperChanger()
    verify(collectionView).getManufacturerName()
    verify(collectionView).showWallpaperChangerPermissionsRequiredDialog()
  }

  @Test
  fun `should start automatic wallpaper changer and show autostart hint dialog on handleAutomaticWallpaperChangerEnabled call success on a vivo oem`() {
    `when`(collectionView.getManufacturerName()).thenReturn("vivo")
    collectionPresenterImpl.handleAutomaticWallpaperChangerEnabled()

    verify(collectionImagesUseCase).startAutomaticWallpaperChanger()
    verify(collectionView).getManufacturerName()
    verify(collectionView).showWallpaperChangerPermissionsRequiredDialog()
  }

  @Test
  fun `should start automatic wallpaper changer and show autostart hint dialog on handleAutomaticWallpaperChangerEnabled call success on a asus oem`() {
    `when`(collectionView.getManufacturerName()).thenReturn("asus")
    collectionPresenterImpl.handleAutomaticWallpaperChangerEnabled()

    verify(collectionImagesUseCase).startAutomaticWallpaperChanger()
    verify(collectionView).getManufacturerName()
    verify(collectionView).showWallpaperChangerPermissionsRequiredDialog()
  }

  @Test
  fun `should stop automatic wallpaper changer on handleAutomaticWallpaperChangerDisabled call success`() {
    collectionPresenterImpl.handleAutomaticWallpaperChangerDisabled()

    verify(collectionImagesUseCase).stopAutomaticWallpaperChanger()
  }

  @Test
  fun `should show wallpaper changer interval dialog with 30 minutes highlighted on handleAutomaticWallpaperChangerIntervalMenuItemClicked call success`() {
    `when`(collectionImagesUseCase.getAutomaticWallpaperChangerInterval()).thenReturn(1800000)

    collectionPresenterImpl.handleAutomaticWallpaperChangerIntervalMenuItemClicked()

    verify(collectionImagesUseCase).getAutomaticWallpaperChangerInterval()
    verify(collectionView).showWallpaperChangerIntervalDialog(0)
  }

  @Test
  fun `should show wallpaper changer interval dialog with 1 hour highlighted on handleAutomaticWallpaperChangerIntervalMenuItemClicked call success`() {
    `when`(collectionImagesUseCase.getAutomaticWallpaperChangerInterval()).thenReturn(3600000)

    collectionPresenterImpl.handleAutomaticWallpaperChangerIntervalMenuItemClicked()

    verify(collectionImagesUseCase).getAutomaticWallpaperChangerInterval()
    verify(collectionView).showWallpaperChangerIntervalDialog(1)
  }

  @Test
  fun `should show wallpaper changer interval dialog with 6 hours highlighted on handleAutomaticWallpaperChangerIntervalMenuItemClicked call success`() {
    `when`(collectionImagesUseCase.getAutomaticWallpaperChangerInterval()).thenReturn(21600000)

    collectionPresenterImpl.handleAutomaticWallpaperChangerIntervalMenuItemClicked()

    verify(collectionImagesUseCase).getAutomaticWallpaperChangerInterval()
    verify(collectionView).showWallpaperChangerIntervalDialog(2)
  }

  @Test
  fun `should show wallpaper changer interval dialog with 1 day highlighted on handleAutomaticWallpaperChangerIntervalMenuItemClicked call success`() {
    `when`(collectionImagesUseCase.getAutomaticWallpaperChangerInterval()).thenReturn(86400000)

    collectionPresenterImpl.handleAutomaticWallpaperChangerIntervalMenuItemClicked()

    verify(collectionImagesUseCase).getAutomaticWallpaperChangerInterval()
    verify(collectionView).showWallpaperChangerIntervalDialog(3)
  }

  @Test
  fun `should show wallpaper changer interval dialog with 3 days highlighted on handleAutomaticWallpaperChangerIntervalMenuItemClicked call success`() {
    `when`(collectionImagesUseCase.getAutomaticWallpaperChangerInterval()).thenReturn(259200000)

    collectionPresenterImpl.handleAutomaticWallpaperChangerIntervalMenuItemClicked()

    verify(collectionImagesUseCase).getAutomaticWallpaperChangerInterval()
    verify(collectionView).showWallpaperChangerIntervalDialog(4)
  }

  @Test
  fun `should show wallpaper changer interval dialog with 30 minutes highlighted on handleAutomaticWallpaperChangerIntervalMenuItemClicked call failure`() {
    `when`(collectionImagesUseCase.getAutomaticWallpaperChangerInterval()).thenReturn(999999999999)

    collectionPresenterImpl.handleAutomaticWallpaperChangerIntervalMenuItemClicked()

    verify(collectionImagesUseCase).getAutomaticWallpaperChangerInterval()
    verify(collectionImagesUseCase).setAutomaticWallpaperChangerInterval(1800000)
    verify(collectionView).showWallpaperChangerIntervalDialog(0)
  }

  @Test
  fun `should show interval updated message on updateWallpaperChangerInterval call success with 30 minutes interval`() {
    `when`(collectionImagesUseCase.setAutomaticWallpaperChangerInterval(1800000))
        .thenReturn(INTERVAL_UPDATED)

    collectionPresenterImpl.updateWallpaperChangerInterval(0)

    verify(collectionImagesUseCase).setAutomaticWallpaperChangerInterval(1800000)
    verify(collectionView).showWallpaperChangerIntervalUpdatedSuccessMessage()
  }

  @Test
  fun `should show service restarted message on updateWallpaperChangerInterval call success with 30 minutes interval`() {
    `when`(collectionImagesUseCase.setAutomaticWallpaperChangerInterval(1800000))
        .thenReturn(SERVICE_RESTARTED)

    collectionPresenterImpl.updateWallpaperChangerInterval(0)

    verify(collectionImagesUseCase).setAutomaticWallpaperChangerInterval(1800000)
    verify(collectionView).showWallpaperChangerRestartedSuccessMessage()
  }

  @Test
  fun `should show interval updated message on updateWallpaperChangerInterval call success with 1 hour interval`() {
    `when`(collectionImagesUseCase.setAutomaticWallpaperChangerInterval(3600000))
        .thenReturn(INTERVAL_UPDATED)

    collectionPresenterImpl.updateWallpaperChangerInterval(1)

    verify(collectionImagesUseCase).setAutomaticWallpaperChangerInterval(3600000)
    verify(collectionView).showWallpaperChangerIntervalUpdatedSuccessMessage()
  }

  @Test
  fun `should show service restarted message on updateWallpaperChangerInterval call success with 1 hour interval`() {
    `when`(collectionImagesUseCase.setAutomaticWallpaperChangerInterval(3600000))
        .thenReturn(SERVICE_RESTARTED)

    collectionPresenterImpl.updateWallpaperChangerInterval(1)

    verify(collectionImagesUseCase).setAutomaticWallpaperChangerInterval(3600000)
    verify(collectionView).showWallpaperChangerRestartedSuccessMessage()
  }

  @Test
  fun `should show interval updated message on updateWallpaperChangerInterval call success with 6 hours interval`() {
    `when`(collectionImagesUseCase.setAutomaticWallpaperChangerInterval(21600000))
        .thenReturn(INTERVAL_UPDATED)

    collectionPresenterImpl.updateWallpaperChangerInterval(2)

    verify(collectionImagesUseCase).setAutomaticWallpaperChangerInterval(21600000)
    verify(collectionView).showWallpaperChangerIntervalUpdatedSuccessMessage()
  }

  @Test
  fun `should show service restarted message on updateWallpaperChangerInterval call success with 6 hours interval`() {
    `when`(collectionImagesUseCase.setAutomaticWallpaperChangerInterval(21600000))
        .thenReturn(SERVICE_RESTARTED)

    collectionPresenterImpl.updateWallpaperChangerInterval(2)

    verify(collectionImagesUseCase).setAutomaticWallpaperChangerInterval(21600000)
    verify(collectionView).showWallpaperChangerRestartedSuccessMessage()
  }

  @Test
  fun `should show interval updated message on updateWallpaperChangerInterval call success with 1 day interval`() {
    `when`(collectionImagesUseCase.setAutomaticWallpaperChangerInterval(86400000))
        .thenReturn(INTERVAL_UPDATED)

    collectionPresenterImpl.updateWallpaperChangerInterval(3)

    verify(collectionImagesUseCase).setAutomaticWallpaperChangerInterval(86400000)
    verify(collectionView).showWallpaperChangerIntervalUpdatedSuccessMessage()
  }

  @Test
  fun `should show service restarted message on updateWallpaperChangerInterval call success with 1 day interval`() {
    `when`(collectionImagesUseCase.setAutomaticWallpaperChangerInterval(86400000))
        .thenReturn(SERVICE_RESTARTED)

    collectionPresenterImpl.updateWallpaperChangerInterval(3)

    verify(collectionImagesUseCase).setAutomaticWallpaperChangerInterval(86400000)
    verify(collectionView).showWallpaperChangerRestartedSuccessMessage()
  }

  @Test
  fun `should show interval updated message on updateWallpaperChangerInterval call success with 3 days interval`() {
    `when`(collectionImagesUseCase.setAutomaticWallpaperChangerInterval(259200000))
        .thenReturn(INTERVAL_UPDATED)

    collectionPresenterImpl.updateWallpaperChangerInterval(4)

    verify(collectionImagesUseCase).setAutomaticWallpaperChangerInterval(259200000)
    verify(collectionView).showWallpaperChangerIntervalUpdatedSuccessMessage()
  }

  @Test
  fun `should show service restarted message on updateWallpaperChangerInterval call success with 3 days interval`() {
    `when`(collectionImagesUseCase.setAutomaticWallpaperChangerInterval(259200000))
        .thenReturn(SERVICE_RESTARTED)

    collectionPresenterImpl.updateWallpaperChangerInterval(4)

    verify(collectionImagesUseCase).setAutomaticWallpaperChangerInterval(259200000)
    verify(collectionView).showWallpaperChangerRestartedSuccessMessage()
  }

  @Test fun `should add image to collection on handleImagePickerResult call success`() {
    val uriList = listOf(mockUri)
    val collectionImagesModelList = listOf(getCollectionsImageModel())
    val collectionPresenterEntityList = listOf(getCollectionImagesPresenterEntity())
    `when`(collectionImagesUseCase.addImage(uriList))
        .thenReturn(Single.just(collectionImagesModelList))
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(collectionImagesModelList))
        .thenReturn(collectionPresenterEntityList)

    collectionPresenterImpl.handleImagePickerResult(uriList)

    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(collectionImagesModelList)
    verify(collectionImagesUseCase).addImage(uriList)
    verify(collectionView).getScope()
    verify(collectionView).setImagesList(collectionPresenterEntityList)
    verify(collectionView).updateChangesInEveryItemView()
    verify(collectionView).hideImagesAbsentLayout()
    verify(collectionView).showWallpaperChangerLayout()
    verify(collectionView).showSingleImageAddedSuccessfullyMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should add image to collection and show hint on handleImagePickerResult call success`() {
    val uriList = listOf(mockUri, mockUri)
    val collectionImagesModelList =
        listOf(getCollectionsImageModel(), getCollectionsImageModel())
    val collectionPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity(), getCollectionImagesPresenterEntity())
    `when`(widgetHintsUseCase.isCollectionsImageReorderHintShown()).thenReturn(false)
    `when`(collectionImagesUseCase.addImage(uriList))
        .thenReturn(Single.just(collectionImagesModelList))
    `when`(collectionImagesPresenterEntityMapper.mapToPresenterEntity(collectionImagesModelList))
        .thenReturn(collectionPresenterEntityList)

    collectionPresenterImpl.handleImagePickerResult(uriList)

    verify(widgetHintsUseCase).isCollectionsImageReorderHintShown()
    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(collectionImagesModelList)
    verify(collectionImagesUseCase).addImage(uriList)
    verify(collectionView).getScope()
    verify(collectionView).setImagesList(collectionPresenterEntityList)
    verify(collectionView).updateChangesInEveryItemView()
    verify(collectionView).hideImagesAbsentLayout()
    verify(collectionView).showWallpaperChangerLayout()
    verify(collectionView).showMultipleImagesAddedSuccessfullyMessage(uriList.size)
    verify(collectionView).showReorderImagesHintWithDelay()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error on handleImagePickerResult call failure`() {
    val uriList = listOf(mockUri)
    `when`(collectionImagesUseCase.addImage(uriList))
        .thenReturn(Single.error(Exception()))

    collectionPresenterImpl.handleImagePickerResult(uriList)

    verify(collectionImagesUseCase).addImage(uriList)
    verify(collectionView).getScope()
    verify(collectionView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should set wallpaper on handleSetWallpaperMenuItemClicked call success`() {
    val collectionImagesModelList =
        listOf(getCollectionsImageModel())
    val collectionPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity())
    val selectedItemsMap = hashMapOf(Pair(0, collectionPresenterEntityList.first()))
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage)).thenReturn(
        randomString)
    `when`(collectionView.isCabActive()).thenReturn(true)
    `when`(
        collectionImagesPresenterEntityMapper.mapFromPresenterEntity(
            collectionPresenterEntityList.first()))
        .thenReturn(collectionImagesModelList)
    `when`(collectionImagesUseCase.getImageBitmap(collectionImagesModelList.first()))
        .thenReturn(Single.just(mockBitmap))

    collectionPresenterImpl.handleSetWallpaperMenuItemClicked(selectedItemsMap)

    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    verify(collectionImagesPresenterEntityMapper).mapFromPresenterEntity(
        collectionPresenterEntityList.first())
    verify(collectionImagesUseCase).getImageBitmap(collectionImagesModelList.first())
    verify(collectionView).getScope()
    verify(collectionView).blockBackPress()
    verify(collectionView).blurScreen()
    verify(collectionView).showIndefiniteLoaderWithMessage(randomString)
    verify(collectionView).isCabActive()
    verify(collectionView).hideCab()
    verify(collectionView).removeBlurFromScreen()
    verify(collectionView).showSetWallpaperSuccessMessage()
    verify(collectionView).releaseBlockPress()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error message on handleSetWallpaperMenuItemClicked call failure`() {
    val collectionImagesModelList =
        listOf(getCollectionsImageModel())
    val collectionPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity())
    val selectedItemsMap = hashMapOf(Pair(0, collectionPresenterEntityList.first()))
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage)).thenReturn(
        randomString)
    `when`(collectionView.isCabActive()).thenReturn(true)
    `when`(
        collectionImagesPresenterEntityMapper.mapFromPresenterEntity(
            collectionPresenterEntityList.first()))
        .thenReturn(collectionImagesModelList)
    `when`(collectionImagesUseCase.getImageBitmap(collectionImagesModelList.first()))
        .thenReturn(Single.error(Exception()))

    collectionPresenterImpl.handleSetWallpaperMenuItemClicked(selectedItemsMap)

    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(collectionImagesPresenterEntityMapper).mapFromPresenterEntity(
        collectionPresenterEntityList.first())
    verify(collectionImagesUseCase).getImageBitmap(collectionImagesModelList.first())
    verify(collectionView).getScope()
    verify(collectionView).blockBackPress()
    verify(collectionView).blurScreen()
    verify(collectionView).showIndefiniteLoaderWithMessage(randomString)
    verify(collectionView).isCabActive()
    verify(collectionView).hideCab()
    verify(collectionView).removeBlurFromScreen()
    verify(collectionView).showGenericErrorMessage()
    verify(collectionView).releaseBlockPress()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should crystallize and add to collection on handleCrystallizeWallpaperMenuItemClicked call success`() {
    val collectionImagesModelList =
        listOf(getCollectionsImageModel())
    val collectionPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity())
    val selectedItemsMap = hashMapOf(Pair(0, collectionPresenterEntityList.first()))
    `when`(resourceUtils.getStringResource(R.string.crystallizing_wallpaper_wait_message))
        .thenReturn(randomString)
    `when`(collectionView.isCabActive()).thenReturn(true)
    `when`(collectionImagesPresenterEntityMapper
        .mapFromPresenterEntity(collectionPresenterEntityList.first()))
        .thenReturn(collectionImagesModelList)
    `when`(collectionImagesPresenterEntityMapper
        .mapToPresenterEntity(collectionImagesModelList)).thenReturn(collectionPresenterEntityList)
    `when`(collectionImagesUseCase.saveCrystallizedImage(collectionImagesModelList.first()))
        .thenReturn(Single.just(collectionImagesModelList))

    collectionPresenterImpl.handleCrystallizeWallpaperMenuItemClicked(selectedItemsMap)

    verify(resourceUtils).getStringResource(R.string.crystallizing_wallpaper_wait_message)
    verify(collectionImagesPresenterEntityMapper).mapFromPresenterEntity(
        collectionPresenterEntityList.first())
    verify(collectionImagesPresenterEntityMapper).mapToPresenterEntity(collectionImagesModelList)
    verify(collectionImagesUseCase).saveCrystallizedImage(collectionImagesModelList.first())
    verify(collectionView).getScope()
    verify(collectionView).blockBackPress()
    verify(collectionView).blurScreen()
    verify(collectionView).showIndefiniteLoaderWithMessage(randomString)
    verify(collectionView).isCabActive()
    verify(collectionView).hideCab()
    verify(collectionView).setImagesList(collectionPresenterEntityList)
    verify(collectionView).updateChangesInEveryItemView()
    verify(collectionView).removeBlurFromScreen()
    verify(collectionView).showCrystallizeSuccessMessage()
    verify(collectionView).releaseBlockPress()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show already present in collection error message on handleCrystallizeWallpaperMenuItemClicked call failure`() {
    val collectionImagesModelList =
        listOf(getCollectionsImageModel())
    val collectionPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity())
    val selectedItemsMap = hashMapOf(Pair(0, collectionPresenterEntityList.first()))
    `when`(resourceUtils.getStringResource(R.string.crystallizing_wallpaper_wait_message))
        .thenReturn(randomString)
    `when`(collectionView.isCabActive()).thenReturn(true)
    `when`(collectionImagesPresenterEntityMapper
        .mapFromPresenterEntity(collectionPresenterEntityList.first()))
        .thenReturn(collectionImagesModelList)
    `when`(collectionImagesUseCase.saveCrystallizedImage(collectionImagesModelList.first()))
        .thenReturn(Single.error(AlreadyPresentInCollectionException()))

    collectionPresenterImpl.handleCrystallizeWallpaperMenuItemClicked(selectedItemsMap)

    verify(resourceUtils).getStringResource(R.string.crystallizing_wallpaper_wait_message)
    verify(collectionImagesPresenterEntityMapper).mapFromPresenterEntity(
        collectionPresenterEntityList.first())
    verify(collectionImagesUseCase).saveCrystallizedImage(collectionImagesModelList.first())
    verify(collectionView).getScope()
    verify(collectionView).blockBackPress()
    verify(collectionView).blurScreen()
    verify(collectionView).showIndefiniteLoaderWithMessage(randomString)
    verify(collectionView).isCabActive()
    verify(collectionView).hideCab()
    verify(collectionView).removeBlurFromScreen()
    verify(collectionView).showCrystallizedImageAlreadyPresentInCollectionErrorMessage()
    verify(collectionView).releaseBlockPress()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error message on handleCrystallizeWallpaperMenuItemClicked call failure`() {
    val collectionImagesModelList =
        listOf(getCollectionsImageModel())
    val collectionPresenterEntityList =
        listOf(getCollectionImagesPresenterEntity())
    val selectedItemsMap = hashMapOf(Pair(0, collectionPresenterEntityList.first()))
    `when`(resourceUtils.getStringResource(R.string.crystallizing_wallpaper_wait_message))
        .thenReturn(randomString)
    `when`(collectionView.isCabActive()).thenReturn(true)
    `when`(collectionImagesPresenterEntityMapper
        .mapFromPresenterEntity(collectionPresenterEntityList.first()))
        .thenReturn(collectionImagesModelList)
    `when`(collectionImagesUseCase.saveCrystallizedImage(collectionImagesModelList.first()))
        .thenReturn(Single.error(Exception()))

    collectionPresenterImpl.handleCrystallizeWallpaperMenuItemClicked(selectedItemsMap)

    verify(resourceUtils).getStringResource(R.string.crystallizing_wallpaper_wait_message)
    verify(collectionImagesPresenterEntityMapper).mapFromPresenterEntity(
        collectionPresenterEntityList.first())
    verify(collectionImagesUseCase).saveCrystallizedImage(collectionImagesModelList.first())
    verify(collectionView).getScope()
    verify(collectionView).blockBackPress()
    verify(collectionView).blurScreen()
    verify(collectionView).showIndefiniteLoaderWithMessage(randomString)
    verify(collectionView).isCabActive()
    verify(collectionView).hideCab()
    verify(collectionView).removeBlurFromScreen()
    verify(collectionView).showGenericErrorMessage()
    verify(collectionView).releaseBlockPress()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should refresh screen on handleCabDestroyed call success`() {
    collectionPresenterImpl.handleCabDestroyed()

    verify(collectionView).clearAllSelectedItems()
    verify(collectionView).updateChangesInEveryItemView()
    verify(collectionView).enableToolbar()
    verify(collectionView).showAppBarWithDelay()
  }

  @After fun tearDown() {
    verifyNoMoreInteractions(collectionView, widgetHintsUseCase, userPremiumStatusUseCase,
        collectionImagesUseCase, collectionImagesPresenterEntityMapper, wallpaperSetter,
        resourceUtils, postExecutionThread)

    collectionPresenterImpl.detachView()
  }

  private fun verifyPostExecutionThreadSchedulerCall(times: Int = 1) {
    verify(postExecutionThread, times(times)).scheduler
  }
}