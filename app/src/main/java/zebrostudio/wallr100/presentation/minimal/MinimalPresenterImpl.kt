package zebrostudio.wallr100.presentation.minimal

import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalView

class MinimalPresenterImpl(
  wallrRepository: WallrRepository,
  postExecutionThread: PostExecutionThread
) : MinimalContract.MinimalPresenter {

  private lateinit var colorList: List<String>
  private var minimalView: MinimalView? = null

  override fun attachView(view: MinimalView) {
    minimalView = view
  }

  override fun detachView() {
    minimalView = null
  }

}