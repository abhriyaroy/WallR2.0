package zebrostudio.wallr100.ui.minimal

class MinimalPresenterImpl : MinimalContract.MinimalPresenter {

  private var minimalView: MinimalContract.MinimalView? = null

  override fun attachView(view: MinimalContract.MinimalView) {
    minimalView = view
  }

  override fun detachView() {
    minimalView = null
  }

}