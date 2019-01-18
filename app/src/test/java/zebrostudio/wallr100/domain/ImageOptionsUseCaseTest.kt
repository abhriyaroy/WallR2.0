package zebrostudio.wallr100.domain

import android.graphics.Bitmap
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsInteractor
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.util.UUID.*

@RunWith(MockitoJUnitRunner::class)
class ImageOptionsUseCaseTest {

  @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock private lateinit var postExecutionThread: PostExecutionThread
  @Mock private lateinit var wallrRepository: WallrRepository
  @Mock private lateinit var dummyBitmap: Bitmap
  private lateinit var imageOptionsUseCase: ImageOptionsUseCase
  private var randomString = randomUUID().toString()
  private var downloadCompleteValue: Long = 100

  @Before
  fun setup() {
    imageOptionsUseCase = ImageOptionsInteractor(wallrRepository, postExecutionThread)
    stubPostExecutionThreadReturnsIoScheduler()
  }

  @Test fun `should return imageDownloadModel on getImageBitmap call success`() {
    `when`(wallrRepository.getImageBitmap(randomString)).thenReturn(Observable.just(
        ImageDownloadModel(downloadCompleteValue, dummyBitmap)))

    imageOptionsUseCase.fetchImageBitmapObservable(randomString)

    verify(wallrRepository).getImageBitmap(randomString)
    verifyNoMoreInteractions(wallrRepository)
  }

  @Test fun `should return single of shareable link on getShareableImageLink call success`() {
    `when`(wallrRepository.getShortImageLink(randomString)).thenReturn(
        Single.just(randomString))
    imageOptionsUseCase.getImageShareableLinkSingle(randomString)

    verify(wallrRepository).getShortImageLink(randomString)
    verifyNoMoreInteractions(wallrRepository)
  }

  @Test fun `should return completable on clearImageCaches call success`() {
    `when`(wallrRepository.clearImageCaches()).thenReturn(Completable.complete())

    imageOptionsUseCase.clearCachesCompletable()

    verify(wallrRepository).clearImageCaches()
    verifyNoMoreInteractions(wallrRepository)
  }

  @Test fun `should call cancelImageBitmapFetchingOperation on canImageFetching call success`() {
    imageOptionsUseCase.cancelFetchImageOperation()

    verify(wallrRepository).cancelImageBitmapFetchOperation()
    verifyNoMoreInteractions(wallrRepository)
  }

  private fun stubPostExecutionThreadReturnsIoScheduler() {
    whenever(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

}