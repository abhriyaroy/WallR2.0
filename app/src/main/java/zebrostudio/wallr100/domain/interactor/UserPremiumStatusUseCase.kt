package zebrostudio.wallr100.domain.interactor

import zebrostudio.wallr100.domain.WallrRepository

interface UserPremiumStatusUseCase {
  fun updateUserPurchaseStatus(): Boolean
  fun isUserPremium(): Boolean
}

class UserPremiumStatusInteractor(private var wallrRepository: WallrRepository) :
    UserPremiumStatusUseCase {

  override fun updateUserPurchaseStatus() = wallrRepository.updateUserPurchaseStatus()
  override fun isUserPremium() = wallrRepository.isUserPremium()

}