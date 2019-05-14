package zebrostudio.wallr100.domain

import android.graphics.Bitmap
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.datafactory.CollectionsImageModelFactory
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerInteractor
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerUseCase
import java.util.Random

@RunWith(MockitoJUnitRunner::class)
class AutomaticWallpaperChangerUseCaseTest {

  @Mock lateinit var wallrRepository: WallrRepository
  @Mock lateinit var mockBitmap: Bitmap
  private lateinit var automaticWallpaperChangerUseCase: AutomaticWallpaperChangerUseCase

  @Before
  fun setup() {
    automaticWallpaperChangerUseCase = AutomaticWallpaperChangerInteractor(wallrRepository)
  }

  @Test
  fun `should return single of bitmap of first image on getWallpaperBitmap call success when last used uid is not present`() {
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = CollectionsImageModelFactory.getCollectionsImageModel()
    val secondCollectionsImageModel = CollectionsImageModelFactory.getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(wallrRepository.getLastUsedWallpaperUid()).thenReturn(Long.MIN_VALUE)
    `when`(wallrRepository.getBitmapFromDatabaseImage(firstCollectionsImageModel)).thenReturn(
        Single.just(mockBitmap))
    `when`(wallrRepository.getImagesInCollection()).thenReturn(
        Single.just(collectionsImageModelList))

    val result = automaticWallpaperChangerUseCase.getWallpaperBitmap().test().values()[0]

    assertEquals(mockBitmap, result)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
  }

  @Test
  fun `should return single of bitmap of second image on getWallpaperBitmap call success when last used uid is of first image in list`() {
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = CollectionsImageModelFactory.getCollectionsImageModel()
    val secondCollectionsImageModel = CollectionsImageModelFactory.getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(wallrRepository.getLastUsedWallpaperUid()).thenReturn(firstCollectionsImageModel.uid)
    `when`(wallrRepository.getBitmapFromDatabaseImage(secondCollectionsImageModel)).thenReturn(
        Single.just(mockBitmap))
    `when`(wallrRepository.getImagesInCollection()).thenReturn(
        Single.just(collectionsImageModelList))

    val result = automaticWallpaperChangerUseCase.getWallpaperBitmap().test().values()[0]

    assertEquals(mockBitmap, result)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(secondCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(secondCollectionsImageModel)
  }

  @Test
  fun `should return single of bitmap of first image on getWallpaperBitmap call success when last used uid is of last image in list`() {
    val inorder = inOrder(wallrRepository)
    val firstCollectionsImageModel = CollectionsImageModelFactory.getCollectionsImageModel()
    val secondCollectionsImageModel = CollectionsImageModelFactory.getCollectionsImageModel()
    val collectionsImageModelList = listOf(firstCollectionsImageModel, secondCollectionsImageModel)
    `when`(wallrRepository.getLastUsedWallpaperUid()).thenReturn(secondCollectionsImageModel.uid)
    `when`(wallrRepository.getBitmapFromDatabaseImage(firstCollectionsImageModel)).thenReturn(
        Single.just(mockBitmap))
    `when`(wallrRepository.getImagesInCollection()).thenReturn(
        Single.just(collectionsImageModelList))

    val result = automaticWallpaperChangerUseCase.getWallpaperBitmap().test().values()[0]

    assertEquals(mockBitmap, result)
    inorder.verify(wallrRepository).getImagesInCollection()
    inorder.verify(wallrRepository).getLastUsedWallpaperUid()
    inorder.verify(wallrRepository).setLastUsedWallpaperUid(firstCollectionsImageModel.uid)
    inorder.verify(wallrRepository).getBitmapFromDatabaseImage(firstCollectionsImageModel)
  }

  @Test fun `should return interval on getInterval call success`() {
    val interval = Random().nextLong()
    `when`(wallrRepository.getWallpaperChangerInterval()).thenReturn(interval)

    val result = automaticWallpaperChangerUseCase.getInterval()

    assertEquals(interval, result)
    verify(wallrRepository).getWallpaperChangerInterval()
  }

  @After
  fun tearDown() {
    verifyNoMoreInteractions(wallrRepository)
  }
}