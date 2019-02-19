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

  @Mock private lateinit var postExecutionThread: PostExecutionThread
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

  @Test fun `should showGenericVerificationError if iab not ready and purchase clicked`() {
    stubIabNotReady()
    buyProPresenterImpl.handlePurchaseClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).showGenericVerificationError()
    verifyNoMoreInteractions(buyProView)
  }

  @Test
  fun `should showNoInternetErrorMessage if iab ready and purchase clicked but no internet`() {
    stubIabReady()
    stubInternetNotAvailable()
    buyProPresenterImpl.handlePurchaseClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).internetAvailability()
    verify(buyProView).showNoInternetErrorMessage(PURCHASE)
    verifyNoMoreInteractions(buyProView)
  }

  @Test
  fun `should launchPurchase if iab ready and purchase clicked and internet available`() {
    stubIabReady()
    stubInternetAvailable()
    buyProPresenterImpl.handlePurchaseClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).internetAvailability()
    verify(buyProView).showWaitLoader(PURCHASE)
    verify(buyProView).launchPurchase()
    verifyNoMoreInteractions(buyProView)
  }

  @Test fun `should showGenericVerificationError if iab not ready and restore clicked`() {
    stubIabNotReady()
    buyProPresenterImpl.handleRestoreClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).showGenericVerificationError()
    verifyNoMoreInteractions(buyProView)
  }

  @Test fun `should showNoInternetErrorMessage if iab ready and restore clicked but no internet`() {
    stubIabReady()
    stubInternetNotAvailable()
    buyProPresenterImpl.handleRestoreClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).internetAvailability()
    verify(buyProView).showNoInternetErrorMessage(RESTORE)
    verifyNoMoreInteractions(buyProView)
  }

  @Test
  fun `should launchRestore if iab ready and restore clicked and internet available`() {
    stubIabReady()
    stubInternetAvailable()
    buyProPresenterImpl.handleRestoreClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).internetAvailability()
    verify(buyProView).showWaitLoader(RESTORE)
    verify(buyProView).launchRestore()
    verifyNoMoreInteractions(buyProView)
  }

  @Test
  fun `should showSuccessfulTransactionMessage and finishWithResult on purchase verification success`() {
    stubSuccessfulUpdateUserPurchaseStatus()
    buyProPresenterImpl.handleSuccessfulVerification(PURCHASE)

    verify(buyProView).showSuccessfulTransactionMessage(PURCHASE)
    verify(buyProView).finishWithResult()
    verifyNoMoreInteractions(buyProView)
  }

  @Test
  fun `should showGenericVerificationError on unsuccessful purchase verification`() {
    stubUnsuccessfulUpdateUserPurchaseStatus()
    buyProPresenterImpl.handleSuccessfulVerification(PURCHASE)

    verify(buyProView).showGenericVerificationError()
    verifyNoMoreInteractions(buyProView)
  }

  @Test
  fun `should showSuccessfulTransactionMessage and finishWithResult on restore verification success`() {
    stubSuccessfulUpdateUserPurchaseStatus()
    buyProPresenterImpl.handleSuccessfulVerification(RESTORE)

    verify(buyProView).showSuccessfulTransactionMessage(RESTORE)
    verify(buyProView).finishWithResult()
    verifyNoMoreInteractions(buyProView)
  }

  @Test fun `should showGenericVerificationError on unsuccessful restore verification`() {
    stubUnsuccessfulUpdateUserPurchaseStatus()
    buyProPresenterImpl.handleSuccessfulVerification(RESTORE)

    verify(buyProView).showGenericVerificationError()
    verifyNoMoreInteractions(buyProView)
  }

  @Test
  fun `should show invalid purchase exception when verifyPurchase call succeeds but purchase is invalid`() {
    `when`(authenticatePurchaseUseCase.buildUseCaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.error(InvalidPurchaseException()))

    buyProPresenterImpl.verifyPurchase(packageName, skuId, purchaseToken, PURCHASE)

    verify(buyProView).getScope()
    verify(buyProView).showInvalidPurchaseError()
    verify(buyProView).dismissWaitLoader()
    verifyNoMoreInteractions(buyProView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show unable to verify purchase error on verifyPurchase call failure with UnableToVerifyPurchaseException`() {
    `when`(authenticatePurchaseUseCase.buildUseCaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.error(UnableToVerifyPurchaseException()))

    buyProPresenterImpl.verifyPurchase(packageName, skuId, purchaseToken, PURCHASE)

    verify(buyProView).getScope()
    verify(buyProView).showUnableToVerifyPurchaseError()
    verify(buyProView).dismissWaitLoader()
    verifyNoMoreInteractions(buyProView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic purchase error on verifyPurchase call failure with generic exception`() {
    `when`(authenticatePurchaseUseCase.buildUseCaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.error(Exception()))

    buyProPresenterImpl.verifyPurchase(packageName, skuId, purchaseToken, PURCHASE)

    verify(buyProView).getScope()
    verify(buyProView).showGenericVerificationError()
    verify(buyProView).dismissWaitLoader()
    verifyNoMoreInteractions(buyProView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should call handle successful verification on successful verification of purchase`() {
    `when`(authenticatePurchaseUseCase.buildUseCaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.complete())
    `when`(userPremiumStatusUseCase.updateUserPurchaseStatus()).thenReturn(true)

    buyProPresenterImpl.verifyPurchase(packageName, skuId, purchaseToken, PURCHASE)

    verify(buyProView).getScope()
    verify(buyProView).dismissWaitLoader()
    verify(buyProView).showSuccessfulTransactionMessage(PURCHASE)
    verify(buyProView).finishWithResult()
    verifyNoMoreInteractions(buyProView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should call handle successful verification on successfully verification of purchase restoration`() {
    `when`(authenticatePurchaseUseCase.buildUseCaseCompletable(packageName, skuId,
        purchaseToken)).thenReturn(Completable.complete())
    `when`(userPremiumStatusUseCase.updateUserPurchaseStatus()).thenReturn(true)

    buyProPresenterImpl.verifyPurchase(packageName, skuId, purchaseToken, RESTORE)

    verify(buyProView).getScope()
    verify(buyProView).dismissWaitLoader()
    verify(buyProView).showSuccessfulTransactionMessage(RESTORE)
    verify(buyProView).finishWithResult()
    verifyNoMoreInteractions(buyProView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @After fun tearDown() {
    buyProPresenterImpl.detachView()
  }

  private fun stubIabReady() {
    `when`(buyProView.isIabReady()).thenReturn(true)
  }

  private fun stubIabNotReady() {
    `when`(buyProView.isIabReady()).thenReturn(false)
  }

  private fun stubInternetAvailable() {
    `when`(buyProView.internetAvailability()).thenReturn(true)
  }

  private fun stubInternetNotAvailable() {
    `when`(buyProView.internetAvailability()).thenReturn(false)
  }

  private fun stubSuccessfulUpdateUserPurchaseStatus() {
    `when`(userPremiumStatusUseCase.updateUserPurchaseStatus()).thenReturn(true)
  }

  private fun stubUnsuccessfulUpdateUserPurchaseStatus() {
    `when`(userPremiumStatusUseCase.updateUserPurchaseStatus()).thenReturn(false)
  }

  private fun stubPostExecutionThreadReturnsIoScheduler() {
    whenever(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  private fun shouldVerifyPostExecutionThreadSchedulerCall() {
    verify(postExecutionThread).scheduler
    verifyNoMoreInteractions(postExecutionThread)
  }
}