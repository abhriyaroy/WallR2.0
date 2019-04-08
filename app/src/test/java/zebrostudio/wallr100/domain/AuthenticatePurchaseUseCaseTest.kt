package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseInteractor
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.rules.TrampolineSchedulerRule

@RunWith(MockitoJUnitRunner::class)
class AuthenticatePurchaseUseCaseTest {

  @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock private lateinit var wallrRepository: WallrRepository
  private lateinit var authenticatePurchaseUseCase: AuthenticatePurchaseUseCase
  private val packageName = java.util.UUID.randomUUID().toString()
  private val purchaseToken = java.util.UUID.randomUUID().toString()
  private val skuId = java.util.UUID.randomUUID().toString()

  @Before fun setup() {
    authenticatePurchaseUseCase =
        AuthenticatePurchaseInteractor(wallrRepository)
  }

  @Test fun `should call authenticatePurchase to verify purchase`() {
    stubAuthenticatePurchaseReturnsCompletableSuccess()
    authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId, purchaseToken)

    verify(wallrRepository).authenticatePurchase(packageName, skuId, purchaseToken)
  }

  @Test fun `should complete when successful purchase verification`() {
    stubAuthenticatePurchaseReturnsCompletableSuccess()

    authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId, purchaseToken).test()
        .assertComplete()

    verify(wallrRepository).authenticatePurchase(packageName, skuId, purchaseToken)
  }

  @Test fun `should return invalidPurchaseException when unsuccessful purchase verification`() {
    stubAuthenticatePurchaseReturnsInvalidPurchaseException()

    authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId, purchaseToken)
        .test().assertError(InvalidPurchaseException::class.java)

    verify(wallrRepository).authenticatePurchase(packageName, skuId, purchaseToken)
  }

  @Test fun `should return unableToVerifyPurchaseException when unable to verify purchase`() {
    stubAuthenticatePurchaseReturnsUnableToVerifyPurchaseException()

    authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId, purchaseToken).test()
        .assertError(UnableToVerifyPurchaseException::class.java)

    verify(wallrRepository).authenticatePurchase(packageName, skuId, purchaseToken)
  }

  @After fun tearDown() {
    verifyNoMoreInteractions(wallrRepository)
  }

  private fun stubAuthenticatePurchaseReturnsCompletableSuccess() {
    whenever(wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)).thenReturn(
        Completable.complete())
  }

  private fun stubAuthenticatePurchaseReturnsInvalidPurchaseException() {
    whenever(wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)).thenReturn(
        Completable.error(InvalidPurchaseException()))
  }

  private fun stubAuthenticatePurchaseReturnsUnableToVerifyPurchaseException() {
    whenever(wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)).thenReturn(
        Completable.error(UnableToVerifyPurchaseException()))
  }

}