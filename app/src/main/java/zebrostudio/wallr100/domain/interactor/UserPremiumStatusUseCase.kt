package zebrostudio.wallr100.domain.interactor

import zebrostudio.wallr100.domain.WallrRepository

class UserPremiumStatusUseCase(private var wallrRepository: WallrRepository) {

  fun saveUserAsPro() = wallrRepository.saveUserAsPro()
  fun checkIfUserIsPro() = wallrRepository.getUserProStatus()

}