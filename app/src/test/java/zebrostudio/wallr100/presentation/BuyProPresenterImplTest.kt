package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity.Companion.ProTransactionType.*
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity.PremiumTransactionType.*
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.buypro.BuyProContract
import zebrostudio.wallr100.presentation.buypro.BuyProPresenterImpl

@RunWith(MockitoJUnitRunner::class)
class BuyProPresenterImplTest {

  @Mock private lateinit var buyProView: BuyProContract.BuyProView
  @Mock private lateinit var wallrRepository: WallrRepository
  @Mock private lateinit var postExecutionThread: PostExecutionThread
  private lateinit var authenticatePurchaseUseCase: AuthenticatePurchaseUseCase
  private lateinit var userPremiumStatusUseCase: UserPremiumStatusUseCase
  private lateinit var buyProPresenterImpl: BuyProPresenterImpl

  @Before fun setup() {
    authenticatePurchaseUseCase = AuthenticatePurchaseUseCase(wallrRepository, postExecutionThread)
    userPremiumStatusUseCase = UserPremiumStatusUseCase(wallrRepository)
    buyProPresenterImpl = BuyProPresenterImpl(authenticatePurchaseUseCase, userPremiumStatusUseCase)
    buyProPresenterImpl.attachView(buyProView)
  }

  @Test fun `should showGenericVerificationError if iab not ready and purchase clicked`() {
    stubIabNotReady()
    buyProPresenterImpl.notifyPurchaseClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).showGenericVerificationError()
    verifyNoMoreInteractions(buyProView)
  }

  @Test
  fun `should showNoInternetErrorMessage if iab ready and purchase clicked but no internet`() {
    stubIabReady()
    stubInternetNotAvailable()
    buyProPresenterImpl.notifyPurchaseClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).isInternetAvailable()
    verify(buyProView).showNoInternetErrorMessage(PURCHASE)
    verifyNoMoreInteractions(buyProView)
  }

  @Test
  fun `should launchPurchase if iab ready and purchase clicked and internet available`() {
    stubIabReady()
    stubInternetAvailable()
    buyProPresenterImpl.notifyPurchaseClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).isInternetAvailable()
    verify(buyProView).showWaitLoader(PURCHASE)
    verify(buyProView).launchPurchase()
    verifyNoMoreInteractions(buyProView)
  }

  @Test fun `should showGenericVerificationError if iab not ready and restore clicked`() {
    stubIabNotReady()
    buyProPresenterImpl.notifyRestoreClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).showGenericVerificationError()
    verifyNoMoreInteractions(buyProView)
  }

  @Test fun `should showNoInternetErrorMessage if iab ready and restore clicked but no internet`() {
    stubIabReady()
    stubInternetNotAvailable()
    buyProPresenterImpl.notifyRestoreClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).isInternetAvailable()
    verify(buyProView).showNoInternetErrorMessage(RESTORE)
    verifyNoMoreInteractions(buyProView)
  }

  @Test
  fun `should launchRestore if iab ready and restore clicked and internet available`() {
    stubIabReady()
    stubInternetAvailable()
    buyProPresenterImpl.notifyRestoreClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).isInternetAvailable()
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

  @After fun tearDown() {
    buyProPresenterImpl.detachView()
  }

  private fun stubIabReady() {
    whenever(buyProView.isIabReady()).thenReturn(true)
  }

  private fun stubIabNotReady() {
    whenever(buyProView.isIabReady()).thenReturn(false)
  }

  private fun stubInternetAvailable() {
    whenever(buyProView.isInternetAvailable()).thenReturn(true)
  }

  private fun stubInternetNotAvailable() {
    whenever(buyProView.isInternetAvailable()).thenReturn(false)
  }

  private fun stubSuccessfulUpdateUserPurchaseStatus() {
    whenever(userPremiumStatusUseCase.updateUserPurchaseStatus()).thenReturn(true)
  }

  private fun stubUnsuccessfulUpdateUserPurchaseStatus() {
    whenever(userPremiumStatusUseCase.updateUserPurchaseStatus()).thenReturn(false)
  }

}