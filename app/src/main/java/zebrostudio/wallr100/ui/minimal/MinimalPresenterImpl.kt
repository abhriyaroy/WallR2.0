package zebrostudio.wallr100.ui.minimal

import zebrostudio.wallr100.data.DataRepository

class MinimalPresenterImpl
constructor(dataRepository: DataRepository) : MinimalContract.MinimalPresenter {

  private var dataRepository = dataRepository
  private var minimalView: MinimalContract.MinimalView? = null

  override fun attachView(view: MinimalContract.MinimalView) {
    minimalView = view
  }

  override fun detachView() {
    minimalView = null
  }

  override fun updateFragmentName(name: String) {
    dataRepository.updateCurrentFragmentName(name)
  }

}