package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider.TestLifecycle.STARTED
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType.PURCHASE
import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType.RESTORE
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.buypro.BuyProContract
import zebrostudio.wallr100.presentation.buypro.BuyProPresenterImpl
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class BuyProPresenterImplTest {

  @Mock lateinit var postExecutionThread: PostExecutionThread
  @Mock lateinit var buyProView: BuyProContract.BuyProView
  @Mock lateinit var authenticatePurchaseUseCase: AuthenticatePurchaseUseCase
  @Mock lateinit var userPremiumStatusUseCase: UserPremiumStatusUseCase
  private lateinit var buyProPresenterImpl: BuyProPresenterImpl
  private lateinit var testScopeProvider: TestLifecycleScopeProvider
  private val packageName = randomUUID().toString()
  private val skuId = randomUUID().toString()
  private val purchaseToken = randomUUID().toString()

  @Before fun setup() {
    buyProPresenterImpl = BuyProPresenterImpl(
        authenticatePurchaseUseCase, userPremiumStatusUseCase, postExecutionThread)
    buyProPresenterImpl.attachView(buyProView)
    testScopeProvider = TestLifecycleScopeProvider.createInitial(STARTED)

    `when`(buyProView.getScope()).thenReturn(testScopeProvider)
    stubPostExecutionThreadReturnsIoScheduler()
  }

  @Test
  fun `should show generic verification error message on handlePurchaseClicked call failure due to iab not ready`() {
    `when`(buyProView.isIabReady()).thenReturn(false)
    buyProPresenterImpl.handlePurchaseClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).showGenericVerificationError()
  }

  @Test
  fun `should show no internet error message on handlePurchaseClicked call failure on no internet`() {
    `when`(buyProView.isIabReady()).thenReturn(true)
    `when`(buyProView.internetAvailability()).thenReturn(false)
    buyProPresenterImpl.handlePurchaseClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).internetAvailability()
    verify(buyProView).showNoInternetErrorMessage(PURCHASE)
  }

  @Test
  fun `should launchPurchase on on handlePurchaseClicked call success`() {
    `when`(buyProView.isIabReady()).thenReturn(true)
    `when`(buyProView.internetAvailability()).thenReturn(true)

    buyProPresenterImpl.handlePurchaseClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).internetAvailability()
    verify(buyProView).showWaitLoader(PURCHASE)
    verify(buyProView).launchPurchase()
  }

  @Test
  fun `should show generic verification error message on handleRestoreClicked call failure due to iab not ready`() {
    `when`(buyProView.isIabReady()).thenReturn(false)

    buyProPresenterImpl.handleRestoreClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).showGenericVerificationError()
  }

  @Test
  fun `should show no internet error message on handleRestoreClicked call failure due to no internet`() {
    `when`(buyProView.isIabReady()).thenReturn(true)
    `when`(buyProView.internetAvailability()).thenReturn(false)

    buyProPresenterImpl.handleRestoreClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).internetAvailability()
    verify(buyProView).showNoInternetErrorMessage(RESTORE)
  }

  @Test
  fun `should launchRestore on handleRestoreClicked call success`() {
    `when`(buyProView.isIabReady()).thenReturn(true)
    `when`(buyProView.internetAvailability()).thenReturn(true)

    buyProPresenterImpl.handleRestoreClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).internetAvailability()
    verify(buyProView).showWaitLoader(RESTORE)
    verify(buyProView).launchRestore()
  }

  @Test
  fun `should show invalid purchase exception on verifyPurchase call failure with type Purchase`() {
    `when`(authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.error(InvalidPurchaseException()))

    buyProPresenterImpl.verifyTransaction(packageName, skuId, purchaseToken, PURCHASE)

    verify(authenticatePurchaseUseCase).authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)
    verify(buyProView).getScope()
    verify(buyProView).showInvalidPurchaseError()
    verify(buyProView).dismissWaitLoader()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show unable to verify purchase error on verifyPurchase call failure due to UnableToVerifyPurchaseException with type Purchase`() {
    `when`(authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.error(UnableToVerifyPurchaseException()))

    buyProPresenterImpl.verifyTransaction(packageName, skuId, purchaseToken, PURCHASE)

    verify(authenticatePurchaseUseCase).authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)
    verify(buyProView).getScope()
    verify(buyProView).showUnableToVerifyPurchaseError()
    verify(buyProView).dismissWaitLoader()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic purchase error on verifyPurchase call failure with type Purchase`() {
    `when`(authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.error(Exception()))

    buyProPresenterImpl.verifyTransaction(packageName, skuId, purchaseToken, PURCHASE)

    verify(authenticatePurchaseUseCase).authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)
    verify(buyProView).getScope()
    verify(buyProView).showGenericVerificationError()
    verify(buyProView).dismissWaitLoader()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show successful transaction message and finish with result on verifyPurchase call success with type Purchase`() {
    `when`(authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.complete())
    `when`(userPremiumStatusUseCase.updateUserPurchaseStatus()).thenReturn(true)

    buyProPresenterImpl.verifyTransaction(packageName, skuId, purchaseToken, PURCHASE)

    verify(authenticatePurchaseUseCase).authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)
    verify(userPremiumStatusUseCase).updateUserPurchaseStatus()
    verify(buyProView).getScope()
    verify(buyProView).showSuccessfulTransactionMessage(PURCHASE)
    verify(buyProView).finishWithResult()
    verify(buyProView).dismissWaitLoader()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show invalid purchase exception on verifyPurchase call failure with type Restore`() {
    `when`(authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.error(InvalidPurchaseException()))

    buyProPresenterImpl.verifyTransaction(packageName, skuId, purchaseToken, RESTORE)

    verify(authenticatePurchaseUseCase).authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)
    verify(buyProView).getScope()
    verify(buyProView).showInvalidPurchaseError()
    verify(buyProView).dismissWaitLoader()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show unable to verify purchase error on verifyPurchase call failure due to UnableToVerifyPurchaseException with type Restore`() {
    `when`(authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.error(UnableToVerifyPurchaseException()))

    buyProPresenterImpl.verifyTransaction(packageName, skuId, purchaseToken, RESTORE)

    verify(authenticatePurchaseUseCase).authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)
    verify(buyProView).getScope()
    verify(buyProView).showUnableToVerifyPurchaseError()
    verify(buyProView).dismissWaitLoader()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic purchase error on verifyPurchase call failure with type Restore`() {
    `when`(authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.error(Exception()))

    buyProPresenterImpl.verifyTransaction(packageName, skuId, purchaseToken, RESTORE)

    verify(authenticatePurchaseUseCase).authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)
    verify(buyProView).getScope()
    verify(buyProView).showGenericVerificationError()
    verify(buyProView).dismissWaitLoader()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show successful transaction message and finish with result on verifyPurchase call success with type Restore`() {
    `when`(authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.complete())
    `when`(userPremiumStatusUseCase.updateUserPurchaseStatus()).thenReturn(true)

    buyProPresenterImpl.verifyTransaction(packageName, skuId, purchaseToken, RESTORE)

    verify(authenticatePurchaseUseCase).authenticatePurchaseCompletable(packageName, skuId,
        purchaseToken)
    verify(userPremiumStatusUseCase).updateUserPurchaseStatus()
    verify(buyProView).getScope()
    verify(buyProView).showSuccessfulTransactionMessage(RESTORE)
    verify(buyProView).finishWithResult()
    verify(buyProView).dismissWaitLoader()
    verifyPostExecutionThreadSchedulerCall()
  }

  @After fun tearDown() {
    verifyNoMoreInteractions(postExecutionThread, userPremiumStatusUseCase,
        authenticatePurchaseUseCase, buyProView)
    buyProPresenterImpl.detachView()
  }

  private fun stubPostExecutionThreadReturnsIoScheduler() {
    whenever(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  private fun verifyPostExecutionThreadSchedulerCall() {
    verify(postExecutionThread).scheduler
  }
}