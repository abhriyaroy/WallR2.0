package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity.PremiumTransactionType.*
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.buypro.BuyProContract
import zebrostudio.wallr100.presentation.buypro.BuyProPresenterImpl

@RunWith(JUnit4::class)
class BuyProPresenterImplTest {

  private lateinit var buyProView: BuyProContract.BuyProView
  private lateinit var wallrRepository: WallrRepository
  private lateinit var postExecutionThread: PostExecutionThread
  private lateinit var authenticatePurchaseUseCase: AuthenticatePurchaseUseCase
  private lateinit var userPremiumStatusUseCase: UserPremiumStatusUseCase
  private lateinit var buyProPresenterImpl: BuyProPresenterImpl

  @Before
  fun setup() {
    buyProView = mock()
    wallrRepository = mock()
    postExecutionThread = mock()
    authenticatePurchaseUseCase = AuthenticatePurchaseUseCase(wallrRepository, postExecutionThread)
    userPremiumStatusUseCase = UserPremiumStatusUseCase(wallrRepository)
    buyProPresenterImpl = BuyProPresenterImpl(authenticatePurchaseUseCase, userPremiumStatusUseCase)
    buyProPresenterImpl.attachView(buyProView)
  }

  @Test
  fun notifyPurchaseClickedIabNotReady() {
    stubIabNotReady()
    buyProPresenterImpl.notifyPurchaseClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).showGenericVerificationError()
  }

  @Test
  fun notifyPurchaseClickedIabReadyInternetNotAvailable() {
    stubIabReady()
    stubInternetNotAvailable()
    buyProPresenterImpl.notifyPurchaseClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).isInternetAvailable()
    verify(buyProView).showNoInternetErrorMessage(PURCHASE)
  }

  @Test
  fun notifyPurchaseClickedIabReadyInternetAvailable() {
    stubIabReady()
    stubInternetAvailable()
    buyProPresenterImpl.notifyPurchaseClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).isInternetAvailable()
    verify(buyProView).showWaitLoader(PURCHASE)
    verify(buyProView).launchPurchase()
  }

  @Test
  fun notifyRestoreClickedIabNotReady() {
    stubIabNotReady()
    buyProPresenterImpl.notifyRestoreClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).showGenericVerificationError()
  }

  @Test
  fun notifyRestoreClickedIabReadyInternetNotAvailable() {
    stubIabReady()
    stubInternetNotAvailable()
    buyProPresenterImpl.notifyRestoreClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).isInternetAvailable()
    verify(buyProView).showNoInternetErrorMessage(RESTORE)
  }

  @Test
  fun notifyRestoreClickedIabReadyInternetAvailable() {
    stubIabReady()
    stubInternetAvailable()
    buyProPresenterImpl.notifyRestoreClicked()

    verify(buyProView).isIabReady()
    verify(buyProView).isInternetAvailable()
    verify(buyProView).showWaitLoader(RESTORE)
    verify(buyProView).launchRestore()
  }

  @Test
  fun successfulVerificationPurchase() {
    stubSuccessfulUpdateUserPurchaseStatus()
    buyProPresenterImpl.handleSuccessfulVerification(PURCHASE)

    verify(buyProView).showSuccessfulTransactionMessage(PURCHASE)
    verify(buyProView).finishWithResult()
  }

  @Test
  fun unsuccessfulVerificationPurchase() {
    stubUnsuccessfulUpdateUserPurchaseStatus()
    buyProPresenterImpl.handleSuccessfulVerification(PURCHASE)

    verify(buyProView).showGenericVerificationError()
  }

  @Test
  fun successfulVerificationRestore() {
    stubSuccessfulUpdateUserPurchaseStatus()
    buyProPresenterImpl.handleSuccessfulVerification(RESTORE)

    verify(buyProView).showSuccessfulTransactionMessage(RESTORE)
    verify(buyProView).finishWithResult()
  }

  @Test
  fun unsuccessfulVerificationRestore() {
    stubUnsuccessfulUpdateUserPurchaseStatus()
    buyProPresenterImpl.handleSuccessfulVerification(RESTORE)

    verify(buyProView).showGenericVerificationError()
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