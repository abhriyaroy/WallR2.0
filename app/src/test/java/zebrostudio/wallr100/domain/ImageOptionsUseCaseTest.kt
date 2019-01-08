package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsInteractor
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.util.UUID.*

@RunWith(MockitoJUnitRunner::class)
class ImageOptionsUseCaseTest {

  @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock private lateinit var postExecutionThread: PostExecutionThread
  @Mock private lateinit var wallrRepository: WallrRepository
  private lateinit var imageOptionsUseCase: ImageOptionsUseCase
  private var randomString = randomUUID().toString()

  @Before
  fun setup() {
    imageOptionsUseCase = ImageOptionsInteractor(wallrRepository, postExecutionThread)
    stubPostExecutionThreadReturnsIoScheduler()
  }

  @Test fun `should call getShareableImageLink and return single of shareable link on success`() {
    stubGetShareableLinkReturnsSingle()
    imageOptionsUseCase.getImageShareableLinkSingle(randomString)

    verify(wallrRepository).getShortImageLink(randomString)
    verifyNoMoreInteractions(wallrRepository)
  }

  private fun stubPostExecutionThreadReturnsIoScheduler() {
    whenever(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  private fun stubGetShareableLinkReturnsSingle() {
    whenever(wallrRepository.getShortImageLink(randomString)).thenReturn(
        Single.just(randomString))
  }
}