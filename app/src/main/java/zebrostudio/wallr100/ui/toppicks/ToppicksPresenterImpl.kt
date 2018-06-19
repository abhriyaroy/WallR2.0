package zebrostudio.wallr100.ui.toppicks

import zebrostudio.wallr100.data.DataRepository
import javax.inject.Inject

class ToppicksPresenterImpl
@Inject constructor(private var dataRepository: DataRepository) : ToppicksContract.ToppicksPresenter {

  private var toppicksView: ToppicksContract.TopicksView? = null

  override fun attachView(view: ToppicksContract.TopicksView) {
    toppicksView = view
  }

  override fun detachView() {
    toppicksView = null
  }

  override fun updateFragmentName(name: String) {
    dataRepository.updateCurrentFragmentName(name)
  }

}