package zebrostudio.wallr100.presentation.detail.colors

import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailPresenter
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailView

class ColorsDetailPresenterImpl(private val isUserPremiumStatusUseCase: UserPremiumStatusUseCase) :
    ColorsDetailPresenter {

  private var view: ColorsDetailView? = null

  override fun attachView(view: ColorsDetailView) {
    this.view = view
  }

  override fun detachView() {
    view = null
  }

}