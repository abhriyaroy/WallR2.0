package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase

@RunWith(MockitoJUnitRunner::class)
class UserPremiumStatusUseCaseTest {

  @Mock private lateinit var wallrRepository: WallrRepository
  private lateinit var userPremiumStatusUseCase: UserPremiumStatusUseCase

  @Before fun setup() {
    userPremiumStatusUseCase = UserPremiumStatusUseCase(wallrRepository)
  }

  @Test fun `should call updateUserPurchaseStatus to updateUserPurchaseStatus`() {
    userPremiumStatusUseCase.updateUserPurchaseStatus()

    verify(wallrRepository).updateUserPurchaseStatus()
  }

  @Test fun `should return true on successful updateUserPurchaseStatus`() {
    stubUpdateUserPurchaseStatusReturnsTrue()
    userPremiumStatusUseCase.updateUserPurchaseStatus()

    assert(true)
  }

  @Test fun `should return false on unsuccessful updateUserPurchaseStatus`() {
    stubUpdateUserPurchaseStatusReturnsFalse()
    userPremiumStatusUseCase.updateUserPurchaseStatus()

    assert(false)
  }

  @Test fun `should call isUserPremium to check user status`() {
    userPremiumStatusUseCase.isUserPremium()

    verify(wallrRepository).isUserPremium()
  }

  @Test fun `should return true if user is premium`() {
    stubIsUserPremiumReturnsTrue()
    userPremiumStatusUseCase.isUserPremium()

    assert(true)
  }

  @Test fun `should return false if user not premium`() {
    stubIsUserPremiumReturnsFalse()
    userPremiumStatusUseCase.isUserPremium()

    assert(false)
  }

  private fun stubUpdateUserPurchaseStatusReturnsTrue() {
    whenever(wallrRepository.updateUserPurchaseStatus()).thenReturn(true)
  }

  private fun stubUpdateUserPurchaseStatusReturnsFalse() {
    whenever(wallrRepository.updateUserPurchaseStatus()).thenReturn(false)
  }

  private fun stubIsUserPremiumReturnsTrue() {
    whenever(wallrRepository.isUserPremium()).thenReturn(true)
  }

  private fun stubIsUserPremiumReturnsFalse() {
    whenever(wallrRepository.isUserPremium()).thenReturn(false)
  }

}