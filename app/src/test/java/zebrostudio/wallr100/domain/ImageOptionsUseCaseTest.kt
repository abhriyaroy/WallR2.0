package zebrostudio.wallr100.domain

import android.graphics.Bitmap
import android.net.Uri
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.interactor.ImageOptionsInteractor
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType.WALLPAPER
import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class ImageOptionsUseCaseTest {

  @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock private lateinit var wallrRepository: WallrRepository
  @Mock private lateinit var mockBitmap: Bitmap
  @Mock private lateinit var mockUri: Uri
  private lateinit var imageOptionsUseCase: ImageOptionsUseCase
  private var randomString = randomUUID().toString()
  private var downloadCompleteValue: Long = 100

  @Before
  fun setup() {
    imageOptionsUseCase = ImageOptionsInteractor(wallrRepository)
  }

  @Test fun `should return imageDownloadModel on getImageBitmap call success`() {
    val expectedImageModel = ImageDownloadModel(downloadCompleteValue, mockBitmap)
    `when`(wallrRepository.getImageBitmap(randomString))
        .thenReturn(Observable.just(expectedImageModel))

    val imageModel = imageOptionsUseCase.fetchImageBitmapObservable(randomString).test().values()[0]

    assertEquals(expectedImageModel, imageModel)
    verify(wallrRepository).getImageBitmap(randomString)
  }

  @Test fun `should return single of shareable link on getShareableImageLink call success`() {
    `when`(wallrRepository.getShortImageLink(randomString)).thenReturn(
        Single.just(randomString))
    imageOptionsUseCase.getImageShareableLinkSingle(randomString)

    verify(wallrRepository).getShortImageLink(randomString)
  }

  @Test fun `should return completable on clearImageCaches call success`() {
    `when`(wallrRepository.clearImageCaches()).thenReturn(Completable.complete())

    imageOptionsUseCase.clearCachesCompletable().test().assertComplete()

    verify(wallrRepository).clearImageCaches()
  }

  @Test fun `should call cancelImageBitmapFetchingOperation on canImageFetching call success`() {
    imageOptionsUseCase.cancelFetchImageOperation()

    verify(wallrRepository).cancelImageBitmapFetchOperation()
  }

  @Test fun `should return uri on getCroppingSourceUri call success`() {
    `when`(wallrRepository.getCacheSourceUri()).thenReturn(Single.just(mockUri))

    val uri = imageOptionsUseCase.getCroppingSourceUri().test().values()[0]

    assertEquals(mockUri, uri)
    verify(wallrRepository).getCacheSourceUri()
  }

  @Test fun `should return uri on getCroppingDestinationUri call success`() {
    `when`(wallrRepository.getCacheResultUri()).thenReturn(Single.just(mockUri))

    val uri = imageOptionsUseCase.getCroppingDestinationUri().test().values()[0]

    assertEquals(mockUri, uri)
    verify(wallrRepository).getCacheResultUri()
  }

  @Test fun `should return Single of bitmap on getBitmapFromUriSingle call success`() {
    `when`(wallrRepository.getBitmapFromUri(mockUri)).thenReturn(Single.just(mockBitmap))

    imageOptionsUseCase.getBitmapFromUriSingle(mockUri).test()
        .assertValue(mockBitmap)

    verify(wallrRepository).getBitmapFromUri(mockUri)
  }

  @Test fun `should complete on downloadImageCompletable call success`() {
    `when`(wallrRepository.downloadImage(randomString)).thenReturn(Completable.complete())

    imageOptionsUseCase.downloadImageCompletable(randomString).test().assertComplete()

    verify(wallrRepository).downloadImage(randomString)
  }

  @Test fun `should complete on downloadCrystallizedImageCompletable call success`() {
    `when`(wallrRepository.saveCachedImageToDownloads()).thenReturn(Completable.complete())

    imageOptionsUseCase.downloadCrystallizedImageCompletable().test().assertComplete()

    verify(wallrRepository).saveCachedImageToDownloads()
  }

  @Test fun `should return true on isDownloadInProgress call success`() {
    `when`(wallrRepository.checkIfDownloadIsInProgress(randomString)).thenReturn(true)

    assertTrue(imageOptionsUseCase.isDownloadInProgress(randomString))

    verify(wallrRepository).checkIfDownloadIsInProgress(randomString)
  }

  @Test fun `should return false on isCrystallizeDescriptionDialogShown call success`() {
    `when`(wallrRepository.isCrystallizeDescriptionShown()).thenReturn(false)

    assertFalse(imageOptionsUseCase.isCrystallizeDescriptionDialogShown())

    verify(wallrRepository).isCrystallizeDescriptionShown()
  }

  @Test
  fun `should invoke saveCrystallizeDescriptionShown on setCrystallizeDescriptionShownOnce call success`() {
    imageOptionsUseCase.setCrystallizeDescriptionShownOnce()

    verify(wallrRepository).saveCrystallizeDescriptionShown()
  }

  @Test fun `should return Single of bitmap on getCrystallizedBitmapSingle call success`() {
    `when`(wallrRepository.getCacheImageBitmap()).thenReturn(Single.just(mockBitmap))

    imageOptionsUseCase.getCrystallizedImageSingle().test().assertValue(mockBitmap)

    verify(wallrRepository).getCacheImageBitmap()
  }

  @Test fun `should return Single of bitmap on getEditedImageSingle call success`() {
    `when`(wallrRepository.getCacheImageBitmap()).thenReturn(Single.just(mockBitmap))

    imageOptionsUseCase.getEditedImageSingle().test().assertValue(mockBitmap)

    verify(wallrRepository).getCacheImageBitmap()
  }

  @Test fun `should complete on addImageToCollection call success`() {
    `when`(wallrRepository.saveImageToCollections(randomString,
        WALLPAPER)).thenReturn(Completable.complete())

    imageOptionsUseCase.addImageToCollection(randomString, WALLPAPER).test()
        .assertComplete()

    verify(wallrRepository).saveImageToCollections(randomString, WALLPAPER)
  }

  @After fun tearDown() {
    verifyNoMoreInteractions(wallrRepository)
  }
}