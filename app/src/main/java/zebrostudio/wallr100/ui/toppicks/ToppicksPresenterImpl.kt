package zebrostudio.wallr100.ui.toppicks

import zebrostudio.wallr100.data.DataRepository

class ToppicksPresenterImpl
constructor(dataRepository: DataRepository) : ToppicksContract.ToppicksPresenter {

  private val TOPPICKS_FRAGMENT_TAG = "Top picks"
  private var dataRepository = dataRepository
  private var toppicksView: ToppicksContract.TopicksView? = null

  override fun updateFragmentName() {
    dataRepository.updateCurrentFragmentName(TOPPICKS_FRAGMENT_TAG)
  }

  override fun attachView(view: ToppicksContract.TopicksView) {
    toppicksView = view
  }

  override fun detachView() {
    toppicksView = null
  }

}