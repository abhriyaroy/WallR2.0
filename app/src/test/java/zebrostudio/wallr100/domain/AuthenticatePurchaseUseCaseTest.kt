package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase

@RunWith(MockitoJUnitRunner::class)
class AuthenticatePurchaseUseCaseTest {

  @Mock private lateinit var postExecutionThread: PostExecutionThread
  @Mock private lateinit var wallrRepository: WallrRepository
  private lateinit var authenticatePurchaseUseCase: AuthenticatePurchaseUseCase
  private val packageName = java.util.UUID.randomUUID().toString()
  private val purchaseToken = java.util.UUID.randomUUID().toString()
  private val skuId = java.util.UUID.randomUUID().toString()

  @Before fun setup() {
    authenticatePurchaseUseCase = AuthenticatePurchaseUseCase(wallrRepository, postExecutionThread)
    stubPostExecutionThreadReturnsIoScheduler()
  }

  @Test fun `should call authenticatePurchase to verify purchase`() {
    stubAuthenticatePurchaseReturnsSingle()
    authenticatePurchaseUseCase.buildUseCaseSingle(packageName, skuId, purchaseToken)

    verify(wallrRepository).authenticatePurchase(packageName, skuId, purchaseToken)
  }

  @Test fun `should complete when successful purchase verification`() {
    stubAuthenticatePurchaseReturnsSingle()
    val testObservable =
        authenticatePurchaseUseCase.buildUseCaseSingle(packageName, skuId, purchaseToken).test()
    testObservable.await()

    testObservable.assertComplete()
  }

  @Test fun `should return invalidPurchaseException when unsuccessful purchase verification`() {
    stubAuthenticatePurchaseReturnsInvalidPurchaseException()
    val testObservable =
        authenticatePurchaseUseCase.buildUseCaseSingle(packageName, skuId, purchaseToken).test()
    testObservable.await()

    testObservable.assertError(InvalidPurchaseException::class.java)
  }

  @Test fun `should return unableToVerifyPurchaseException when unable to verify purchase`() {
    stubAuthenticatePurchaseReturnsUnableToVerifyPurchaseException()
    val testObservable =
        authenticatePurchaseUseCase.buildUseCaseSingle(packageName, skuId, purchaseToken).test()
    testObservable.await()

    testObservable.assertError(UnableToVerifyPurchaseException::class.java)
  }

  private fun stubPostExecutionThreadReturnsIoScheduler() {
    whenever(postExecutionThread.scheduler).thenReturn(Schedulers.io())
  }

  private fun stubAuthenticatePurchaseReturnsSingle() {
    whenever(wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)).thenReturn(
        Single.just(true))
  }

  private fun stubAuthenticatePurchaseReturnsInvalidPurchaseException() {
    whenever(wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)).thenReturn(
        Single.error(InvalidPurchaseException()))
  }

  private fun stubAuthenticatePurchaseReturnsUnableToVerifyPurchaseException() {
    whenever(wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)).thenReturn(
        Single.error(UnableToVerifyPurchaseException()))
  }

}