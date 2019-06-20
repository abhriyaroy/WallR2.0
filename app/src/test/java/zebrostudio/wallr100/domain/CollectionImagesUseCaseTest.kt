package zebrostudio.wallr100.domain

import android.graphics.Bitmap
import android.net.Uri
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.service.ServiceManager
import zebrostudio.wallr100.domain.datafactory.CollectionsImageModelFactory
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerIntervalUpdateResultState.INTERVAL_UPDATED
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerIntervalUpdateResultState.SERVICE_RESTARTED
import zebrostudio.wallr100.domain.interactor.CollectionImagesUseCase
import zebrostudio.wallr100.domain.interactor.CollectionsImagesInteractor
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class CollectionImagesUseCaseTest {

  @Mock
  lateinit var serviceManager: ServiceManager
  @Mock
  lateinit var wallrRepository: WallrRepository
  @Mock
  lateinit var uri: Uri
  @Mock
  lateinit var bitmap: Bitmap
  private lateinit var collectionsImagesUseCase: CollectionImagesUseCase

  @Before
  fun setup() {
    collectionsImagesUseCase = CollectionsImagesInteractor(serviceManager, wallrRepository)
  }

  @Test
  fun `should return Single of list of CollectionsImageModel on getAllImages call success`() {
    val collectionImagesModelList = listOf(CollectionsImageModelFactory.getCollectionsImageModel())
    `when`(wallrRepository.getImagesInCollection()).thenReturn(
      Single.just(collectionImagesModelList))

    val result = collectionsImagesUseCase.getAllImages().test().values()[0]

    assertEquals(collectionImagesModelList, result)
    verify(wallrRepository).getImagesInCollection()
  }

  @Test
  fun `should return error on getAllImages call failure`() {
    `when`(wallrRepository.getImagesInCollection()).thenReturn(Single.error(Exception()))

    collectionsImagesUseCase.getAllImages().test().assertError(Exception::class.java)

    verify(wallrRepository).getImagesInCollection()
  }

  @Test
  fun `should return Single of list of CollectionsImageModel on addImages call success`() {
    val uriList = listOf(uri)
    val collectionImagesModelList = listOf(CollectionsImageModelFactory.getCollectionsImageModel())
    `when`(wallrRepository.addImagesToCollection(uriList)).thenReturn(
      Single.just(collectionImagesModelList))

    val result = collectionsImagesUseCase.addImage(uriList).test().values()[0]

    assertEquals(collectionImagesModelList, result)
    verify(wallrRepository).addImagesToCollection(uriList)
  }

  @Test
  fun `should return error on addImage call failure`() {
    val uriList = listOf(uri)
    `when`(wallrRepository.addImagesToCollection(uriList)).thenReturn(
      Single.error(Exception()))

    collectionsImagesUseCase.addImage(uriList).test().assertError(Exception::class.java)

    verify(wallrRepository).addImagesToCollection(uriList)
  }

  @Test
  fun `should return Single of list of CollectionsImageModel on reorderImage call success`() {
    val collectionImagesModelList = listOf(CollectionsImageModelFactory.getCollectionsImageModel())
    `when`(wallrRepository.reorderImagesInCollectionDatabase(collectionImagesModelList)).thenReturn(
      Single.just(collectionImagesModelList))

    val result = collectionsImagesUseCase.reorderImage(collectionImagesModelList).test().values()[0]

    assertEquals(collectionImagesModelList, result)
    verify(wallrRepository).reorderImagesInCollectionDatabase(collectionImagesModelList)
  }

  @Test
  fun `should return error on reorderImage call failure`() {
    val collectionImagesModelList = listOf(CollectionsImageModelFactory.getCollectionsImageModel())
    `when`(wallrRepository.reorderImagesInCollectionDatabase(collectionImagesModelList))
        .thenReturn(Single.error(Exception()))

    collectionsImagesUseCase.reorderImage(collectionImagesModelList).test()
        .assertError(Exception::class.java)

    verify(wallrRepository).reorderImagesInCollectionDatabase(collectionImagesModelList)
  }

  @Test
  fun `should return Single of list of CollectionsImageModel on deleteImages call success`() {
    val collectionImagesModelList = listOf(CollectionsImageModelFactory.getCollectionsImageModel())
    `when`(wallrRepository.deleteImageFromCollection(collectionImagesModelList)).thenReturn(
      Single.just(collectionImagesModelList))

    val result = collectionsImagesUseCase.deleteImages(collectionImagesModelList).test().values()[0]

    assertEquals(collectionImagesModelList, result)
    verify(wallrRepository).deleteImageFromCollection(collectionImagesModelList)
  }

  @Test
  fun `should return error on deleteImages call failure`() {
    val collectionImagesModelList = listOf(CollectionsImageModelFactory.getCollectionsImageModel())
    `when`(wallrRepository.deleteImageFromCollection(collectionImagesModelList))
        .thenReturn(Single.error(Exception()))

    collectionsImagesUseCase.deleteImages(collectionImagesModelList).test()
        .assertError(Exception::class.java)

    verify(wallrRepository).deleteImageFromCollection(collectionImagesModelList)
  }

  @Test
  fun `should return Single of list of CollectionsImageModel on getImageBitmap call success`() {
    val collectionImagesModel = CollectionsImageModelFactory.getCollectionsImageModel()
    `when`(wallrRepository.getBitmapFromDatabaseImage(collectionImagesModel)).thenReturn(
      Single.just(bitmap))

    val result = collectionsImagesUseCase.getImageBitmap(collectionImagesModel).test().values()[0]

    assertEquals(bitmap, result)
    verify(wallrRepository).getBitmapFromDatabaseImage(collectionImagesModel)
  }

  @Test
  fun `should return error on getImageBitmap call failure`() {
    val collectionImagesModel = CollectionsImageModelFactory.getCollectionsImageModel()
    `when`(wallrRepository.getBitmapFromDatabaseImage(collectionImagesModel))
        .thenReturn(Single.error(Exception()))

    collectionsImagesUseCase.getImageBitmap(collectionImagesModel).test()
        .assertError(Exception::class.java)

    verify(wallrRepository).getBitmapFromDatabaseImage(collectionImagesModel)
  }

  @Test
  fun `should return Single of list of collectionsImageModel on saveCrystallizedImage call success`() {
    val collectionImagesModel = CollectionsImageModelFactory.getCollectionsImageModel()
    val collectionImagesModelList = listOf(collectionImagesModel)
    `when`(wallrRepository.saveCrystallizedImageInDatabase(collectionImagesModel)).thenReturn(
      Single.just(collectionImagesModelList))

    val result =
        collectionsImagesUseCase.saveCrystallizedImage(collectionImagesModel).test().values()[0]

    assertEquals(collectionImagesModelList, result)
    verify(wallrRepository).saveCrystallizedImageInDatabase(collectionImagesModel)
  }

  @Test
  fun `should return error on saveCrystallizedImage call failure`() {
    val collectionImagesModel = CollectionsImageModelFactory.getCollectionsImageModel()
    `when`(wallrRepository.saveCrystallizedImageInDatabase(collectionImagesModel))
        .thenReturn(Single.error(Exception()))

    collectionsImagesUseCase.saveCrystallizedImage(collectionImagesModel).test()
        .assertError(Exception::class.java)

    verify(wallrRepository).saveCrystallizedImageInDatabase(collectionImagesModel)
  }

  @Test
  fun `should return true on isAutomaticWallpaperChangerRunning call success when wallpaper changer is running`() {
    `when`(serviceManager.isAutomaticWallpaperChangerRunning()).thenReturn(true)

    assertTrue(collectionsImagesUseCase.isAutomaticWallpaperChangerRunning())

    verify(serviceManager).isAutomaticWallpaperChangerRunning()
  }

  @Test
  fun `should return false on isAutomaticWallpaperChangerRunning call success when wallpaper changer is not running`() {
    `when`(serviceManager.isAutomaticWallpaperChangerRunning()).thenReturn(false)

    assertFalse(collectionsImagesUseCase.isAutomaticWallpaperChangerRunning())

    verify(serviceManager).isAutomaticWallpaperChangerRunning()
  }

  @Test
  fun `should start automatic wallpaper changer on startAutomaticWallpaperChanger call success`() {
    collectionsImagesUseCase.startAutomaticWallpaperChanger()

    verify(serviceManager).startAutomaticWallpaperChangerService()
  }

  @Test
  fun `should start automatic wallpaper changer on stopAutomaticWallpaperChanger call success`() {
    collectionsImagesUseCase.stopAutomaticWallpaperChanger()

    verify(serviceManager).stopAutomaticWallpaperChangerService()
  }

  @Test
  fun `should return interval on getAutomaticWallpaperChangerInterval call success`() {
    val interval = Random().nextLong()
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(interval)

    assertEquals(interval, collectionsImagesUseCase.getAutomaticWallpaperChangerInterval())

    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @Test
  fun `should update interval on setAutomaticWallpaperChangerInterval call success when wallpaper changer is not running`() {
    val interval = Random().nextLong()
    `when`(serviceManager.isAutomaticWallpaperChangerRunning()).thenReturn(false)

    val result = collectionsImagesUseCase.setAutomaticWallpaperChangerInterval(interval)

    assertEquals(INTERVAL_UPDATED, result)
    verify(wallrRepository).setWallpaperChangerInterval(interval)
    verify(serviceManager).isAutomaticWallpaperChangerRunning()
  }

  @Test
  fun `should update interval and restart wallpaper changer on setAutomaticWallpaperChangerInterval call success when wallpaper changer is already running`() {
    val interval = Random().nextLong()
    val inorder = inOrder(serviceManager)
    `when`(serviceManager.isAutomaticWallpaperChangerRunning()).thenReturn(true)

    val result = collectionsImagesUseCase.setAutomaticWallpaperChangerInterval(interval)

    assertEquals(SERVICE_RESTARTED, result)
    verify(wallrRepository).setWallpaperChangerInterval(interval)
    inorder.verify(serviceManager).isAutomaticWallpaperChangerRunning()
    inorder.verify(serviceManager).stopAutomaticWallpaperChangerService()
    inorder.verify(serviceManager).startAutomaticWallpaperChangerService()
  }

  @Test
  fun `should return true on wasAutomaticWallpaperChangerEnabled call success when wallpaper changer was enabled`() {
    `when`(wallrRepository.wasAutomaticWallpaperChangerEnabled()).thenReturn(true)

    assertTrue(collectionsImagesUseCase.wasAutomaticWallpaperChangerEnabled())

    verify(wallrRepository).wasAutomaticWallpaperChangerEnabled()
  }

  @Test
  fun `should return false on wasAutomaticWallpaperChangerEnabled call success when wallpaper changer was not enabled`() {
    `when`(wallrRepository.wasAutomaticWallpaperChangerEnabled()).thenReturn(false)

    assertFalse(collectionsImagesUseCase.wasAutomaticWallpaperChangerEnabled())

    verify(wallrRepository).wasAutomaticWallpaperChangerEnabled()
  }

  @After
  fun teardown() {
    verifyNoMoreInteractions(serviceManager, wallrRepository)
  }
}