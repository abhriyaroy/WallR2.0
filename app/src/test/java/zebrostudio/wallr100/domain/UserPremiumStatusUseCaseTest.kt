package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusInteractor
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase

@RunWith(MockitoJUnitRunner::class)
class UserPremiumStatusUseCaseTest {

  @Mock private lateinit var wallrRepository: WallrRepository

  private lateinit var userPremiumStatusUseCase: UserPremiumStatusUseCase

  @Before fun setup() {
    userPremiumStatusUseCase = UserPremiumStatusInteractor(wallrRepository)
  }

  @Test fun `should return true on updateUserPurchaseStatus call success`() {
    `when`(wallrRepository.updateUserPurchaseStatus()).thenReturn(true)

    assertEquals(true, userPremiumStatusUseCase.updateUserPurchaseStatus())

    verify(wallrRepository).updateUserPurchaseStatus()
  }

  @Test fun `should return false on updateUserPurchaseStatus call failure`() {
    `when`(wallrRepository.updateUserPurchaseStatus()).thenReturn(false)

    assertEquals(false, userPremiumStatusUseCase.updateUserPurchaseStatus())

    verify(wallrRepository).updateUserPurchaseStatus()
  }

  @Test fun `should call isUserPremium to check user status`() {
    userPremiumStatusUseCase.isUserPremium()

    verify(wallrRepository).isUserPremium()
    verifyNoMoreInteractions(wallrRepository)
  }

  @Test fun `should return true if user is premium`() {
    `when`(wallrRepository.isUserPremium()).thenReturn(true)

    assertEquals(true, userPremiumStatusUseCase.isUserPremium())

    verify(wallrRepository).isUserPremium()
  }

  @Test fun `should return false if user not premium`() {
    `when`(wallrRepository.isUserPremium()).thenReturn(false)

    assertEquals(false, userPremiumStatusUseCase.isUserPremium())

    verify(wallrRepository).isUserPremium()
  }

  @After fun tearDown(){
    verifyNoMoreInteractions(wallrRepository)
  }
}