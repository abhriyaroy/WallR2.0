package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.domain.datafactory.CollectionsImageModelFactory.getCollectionsImageModel
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.CollectionImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionView
import zebrostudio.wallr100.presentation.collection.CollectionPresenterImpl
import zebrostudio.wallr100.presentation.collection.mapper.CollectionImagesPresenterEntityMapper
import zebrostudio.wallr100.presentation.datafactory.CollectionImagesPresenterEntityFactory.getCollectionImagesPresenterEntityFactory

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
        listOf(getCollectionImagesPresenterEntityFactory())
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
        listOf(getCollectionImagesPresenterEntityFactory(),
            getCollectionImagesPresenterEntityFactory())
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
        listOf(getCollectionImagesPresenterEntityFactory())
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
        listOf(getCollectionImagesPresenterEntityFactory(),
            getCollectionImagesPresenterEntityFactory())
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
        listOf(getCollectionImagesPresenterEntityFactory())
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
        listOf(getCollectionImagesPresenterEntityFactory(),
            getCollectionImagesPresenterEntityFactory())
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
        listOf(getCollectionImagesPresenterEntityFactory())
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
        listOf(getCollectionImagesPresenterEntityFactory(),
            getCollectionImagesPresenterEntityFactory())
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