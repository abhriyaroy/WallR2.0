package zebrostudio.wallr100.presentation.minimal

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.data.exception.UnableToGetSolidColorsException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.MinimalImagesUseCase
import zebrostudio.wallr100.presentation.adapters.MinimalRecyclerItemContract.MinimalRecyclerViewPresenter
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalView

class MinimalPresenterImpl(
  private val minimalImagesUseCase: MinimalImagesUseCase,
  private val postExecutionThread: PostExecutionThread
) : MinimalContract.MinimalPresenter {

  private lateinit var colorList: List<String>
  private var minimalView: MinimalView? = null
  private var recyclerPresenter: MinimalRecyclerViewPresenter? = null

  override fun attachView(view: MinimalView) {
    minimalView = view
  }

  override fun detachView() {
    minimalView = null
  }

  override fun attachMinimalImageRecyclerViewPresenter(presenter: MinimalRecyclerViewPresenter) {
    recyclerPresenter = presenter
  }

  override fun detachMinimalImageRecyclerViewPresenter() {
    recyclerPresenter = null
  }

  override fun handleViewCreated() {
    if (minimalImagesUseCase.isCustomColorListPresent()) {
      minimalImagesUseCase.getCustomColors()
    } else {
      minimalImagesUseCase.getDefaultColors()
    }.observeOn(postExecutionThread.scheduler)
        .autoDisposable(minimalView?.getScope()!!)
        .subscribe({
          colorList = it
          recyclerPresenter?.appendList(it)
          minimalView?.showColors()
        }, {
          if (it is UnableToGetSolidColorsException) {
            minimalView?.showUnableToGetColorsErrorMessage()
          } else {
            minimalView?.showGenericErrorMessage()
          }
        })
  }

}