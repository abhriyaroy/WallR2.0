package zebrostudio.wallr100.domain

import android.graphics.Bitmap
import android.net.Uri
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.interactor.ColorImagesInteractor
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageTypeModel.CRYSTALLIZED
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageTypeModel.EDITED
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageTypeModel.MINIMAL_COLOR
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageTypeModel.SEARCH
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageTypeModel.WALLPAPER
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.GRADIENT
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.MATERIAL
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.PLASMA
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class ColorImagesUseCaseTest {

  @Mock lateinit var wallrRepository: WallrRepository
  @Mock lateinit var mockBitmap: Bitmap
  @Mock lateinit var mockUri: Uri
  private lateinit var colorImagesInteractor: ColorImagesInteractor
  private val randomString = randomUUID().toString()

  @Before fun setup() {
    colorImagesInteractor = ColorImagesInteractor(wallrRepository)
  }

  @Test fun `should return single of bitmap on getSingularColorBitmapSingle call success`() {
    `when`(wallrRepository.getSingleColorBitmap(randomString)).thenReturn(Single.just(mockBitmap))

    val result = colorImagesInteractor.getSingularColorBitmapSingle(randomString).test().values()[0]

    assertEquals(mockBitmap, result)
    verify(wallrRepository).getSingleColorBitmap(randomString)
  }

  @Test
  fun `should return single of bitmap on getMultiColorBitmapSingle of Material type call success`() {
    val list = listOf(randomString)
    `when`(wallrRepository.getMultiColorBitmap(list, MATERIAL)).thenReturn(Single.just(mockBitmap))

    val result =
        colorImagesInteractor.getMultiColorBitmapSingle(list, MATERIAL).test().values()[0]

    assertEquals(mockBitmap, result)
    verify(wallrRepository).getMultiColorBitmap(list, MATERIAL)
  }

  @Test
  fun `should return single of bitmap on getMultiColorBitmapSingle of Gradient type call success`() {
    val list = listOf(randomString)
    `when`(wallrRepository.getMultiColorBitmap(list, GRADIENT)).thenReturn(Single.just(mockBitmap))

    val result =
        colorImagesInteractor.getMultiColorBitmapSingle(list, GRADIENT).test().values()[0]

    assertEquals(mockBitmap, result)
    verify(wallrRepository).getMultiColorBitmap(list, GRADIENT)
  }

  @Test
  fun `should return single of bitmap on getMultiColorBitmapSingle of Plasma type call success`() {
    val list = listOf(randomString)
    `when`(wallrRepository.getMultiColorBitmap(list, PLASMA)).thenReturn(Single.just(mockBitmap))

    val result =
        colorImagesInteractor.getMultiColorBitmapSingle(list, PLASMA).test().values()[0]

    assertEquals(mockBitmap, result)
    verify(wallrRepository).getMultiColorBitmap(list, PLASMA)
    verify(wallrRepository).getMultiColorBitmap(list, PLASMA)
  }

  @Test
  fun `should return single of bitmap on getBitmapSingle call success`() {
    `when`(wallrRepository.getImageBitmap()).thenReturn(Single.just(mockBitmap))

    val result = colorImagesInteractor.getBitmapSingle().test().values()[0]

    assertEquals(mockBitmap, result)
    verify(wallrRepository).getImageBitmap()
  }

  @Test
  fun `should return single of bitmap on getBitmapFromUriSingle call success`() {
    `when`(wallrRepository.getBitmapFromUri(mockUri)).thenReturn(Single.just(mockBitmap))

    val result = colorImagesInteractor.getBitmapFromUriSingle(mockUri).test().values()[0]

    assertEquals(mockBitmap, result)
    verify(wallrRepository).getBitmapFromUri(mockUri)
  }

  @Test fun `should complete on saveToCollectionsCompletable of Wallpaper type call success`() {
    `when`(wallrRepository.saveImageToCollections(randomString, WALLPAPER))
        .thenReturn(Completable.complete())

    colorImagesInteractor.saveToCollectionsCompletable(randomString,
        WALLPAPER)
        .test().assertComplete()

    verify(wallrRepository).saveImageToCollections(randomString, WALLPAPER)
  }

  @Test fun `should complete on saveToCollectionsCompletable of Search type call success`() {
    `when`(wallrRepository.saveImageToCollections(randomString, SEARCH))
        .thenReturn(Completable.complete())

    colorImagesInteractor.saveToCollectionsCompletable(randomString, SEARCH)
        .test().assertComplete()

    verify(wallrRepository).saveImageToCollections(randomString, SEARCH)
  }

  @Test fun `should complete on saveToCollectionsCompletable of Crystallised type call success`() {
    `when`(wallrRepository.saveImageToCollections(randomString, CRYSTALLIZED))
        .thenReturn(Completable.complete())

    colorImagesInteractor.saveToCollectionsCompletable(randomString,
        CRYSTALLIZED)
        .test().assertComplete()

    verify(wallrRepository).saveImageToCollections(randomString, CRYSTALLIZED)
  }

  @Test fun `should complete on saveToCollectionsCompletable of Edited type call success`() {
    `when`(wallrRepository.saveImageToCollections(randomString, EDITED))
        .thenReturn(Completable.complete())

    colorImagesInteractor.saveToCollectionsCompletable(randomString, EDITED)
        .test().assertComplete()

    verify(wallrRepository).saveImageToCollections(randomString, EDITED)
  }

  @Test fun `should complete on saveToCollectionsCompletable of Minimal Color type call success`() {
    `when`(
        wallrRepository.saveImageToCollections(randomString, MINIMAL_COLOR))
        .thenReturn(Completable.complete())

    colorImagesInteractor.saveToCollectionsCompletable(randomString,
        MINIMAL_COLOR)
        .test().assertComplete()

    verify(wallrRepository).saveImageToCollections(randomString,
        MINIMAL_COLOR)
  }

  @Test fun `should return uri on getCacheSourceUri call success`() {
    `when`(wallrRepository.getCacheSourceUri()).thenReturn(mockUri)

    val result = colorImagesInteractor.getCacheSourceUri()

    assertEquals(mockUri, result)
    verify(wallrRepository).getCacheSourceUri()
  }

  @Test fun `should return uri on getCroppingDestinationUri call success`() {
    `when`(wallrRepository.getCacheResultUri()).thenReturn(mockUri)

    val result = colorImagesInteractor.getCroppingDestinationUri()

    assertEquals(mockUri, result)
    verify(wallrRepository).getCacheResultUri()
  }

  @Test fun `should return single of uri on getCacheImageUri call success`() {
    `when`(wallrRepository.getShareableImageUri()).thenReturn(Single.just(mockUri))

    val result = colorImagesInteractor.getCacheImageUri().test().values()[0]

    assertEquals(mockUri, result)
    verify(wallrRepository).getShareableImageUri()
  }

  @Test fun `should complete on downloadImage call success`() {
    `when`(wallrRepository.saveCachedImageToDownloads()).thenReturn(Completable.complete())

    colorImagesInteractor.downloadImage().test().assertComplete()

    verify(wallrRepository).saveCachedImageToDownloads()
    verifyNoMoreInteractions(wallrRepository)
  }

  @Test fun `should complete on clearCachesCompletable call success`() {
    `when`(wallrRepository.clearImageCaches()).thenReturn(Completable.complete())

    colorImagesInteractor.clearCachesCompletable().test().assertComplete()

    verify(wallrRepository).clearImageCaches()
  }

  @After fun tearDown() {
    verifyNoMoreInteractions(wallrRepository)
  }
}