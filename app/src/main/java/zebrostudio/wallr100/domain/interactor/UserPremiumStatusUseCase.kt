package zebrostudio.wallr100.domain.interactor

import zebrostudio.wallr100.domain.WallrRepository

class UserPremiumStatusUseCase(private var wallrRepository: WallrRepository) {

  fun updateUserPurchaseStatus() = wallrRepository.updateUserPurchaseStatus()
  fun isUserPremium() = wallrRepository.isUserPremium()

}