package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseInteractor
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.rules.TrampolineSchedulerRule

@RunWith(MockitoJUnitRunner::class)
class AuthenticatePurchaseInteractorTest {

  @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock private lateinit var postExecutionThread: PostExecutionThread
  @Mock private lateinit var wallrRepository: WallrRepository
  private lateinit var authenticatePuchaseInteractor: AuthenticatePurchaseUseCase
  private val packageName = java.util.UUID.randomUUID().toString()
  private val purchaseToken = java.util.UUID.randomUUID().toString()
  private val skuId = java.util.UUID.randomUUID().toString()

  @Before fun setup() {
    authenticatePuchaseInteractor =
        AuthenticatePurchaseInteractor(wallrRepository, postExecutionThread)
    stubPostExecutionThreadReturnsIoScheduler()
  }

  @Test fun `should call authenticatePurchase to verify purchase`() {
    stubAuthenticatePurchaseReturnsSingle()
    authenticatePuchaseInteractor.buildUseCaseCompletable(packageName, skuId, purchaseToken)

    verify(wallrRepository).authenticatePurchase(packageName, skuId, purchaseToken)
  }

  @Test fun `should complete when successful purchase verification`() {
    stubAuthenticatePurchaseReturnsSingle()

    authenticatePuchaseInteractor.buildUseCaseCompletable(packageName, skuId, purchaseToken).test()
        .assertComplete()
  }

  @Test fun `should return invalidPurchaseException when unsuccessful purchase verification`() {
    stubAuthenticatePurchaseReturnsInvalidPurchaseException()
    authenticatePuchaseInteractor.buildUseCaseCompletable(packageName, skuId, purchaseToken).test()
        .assertError(InvalidPurchaseException::class.java)
  }

  @Test fun `should return unableToVerifyPurchaseException when unable to verify purchase`() {
    stubAuthenticatePurchaseReturnsUnableToVerifyPurchaseException()

    authenticatePuchaseInteractor.buildUseCaseCompletable(packageName, skuId, purchaseToken).test()
        .assertError(UnableToVerifyPurchaseException::class.java)
  }

  private fun stubPostExecutionThreadReturnsIoScheduler() {
    `when`(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  private fun stubAuthenticatePurchaseReturnsSingle() {
    `when`(wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)).thenReturn(
        Completable.complete())
  }

  private fun stubAuthenticatePurchaseReturnsInvalidPurchaseException() {
    `when`(wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)).thenReturn(
        Completable.error(InvalidPurchaseException()))
  }

  private fun stubAuthenticatePurchaseReturnsUnableToVerifyPurchaseException() {
    `when`(wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)).thenReturn(
        Completable.error(UnableToVerifyPurchaseException()))
  }

}