package zebrostudio.wallr100.presentation.minimal

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.data.exception.UnableToGetSolidColorsException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.MinimalImagesUseCase
import zebrostudio.wallr100.presentation.adapters.MinimalRecyclerItemContract.MinimalRecyclerViewPresenter
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalView

const val MINIMUM_SCROLL_DIST = 25

class MinimalPresenterImpl(
  private val minimalImagesUseCase: MinimalImagesUseCase,
  private val postExecutionThread: PostExecutionThread
) : MinimalContract.MinimalPresenter {

  private lateinit var colorList: List<String>
  private var isBottomPanelEnabled = false
  private var selectionSize = 0
  private var minimalView: MinimalView? = null
  private var recyclerPresenter: MinimalRecyclerViewPresenter? = null
  private var forceSmoothScroll: Boolean = false

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
        .autoDisposable(minimalView!!.getScope())
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

  override fun updateSelectionChange(index: Int, size: Int) {
    selectionSize = size
    minimalView?.updateViewItem(index)
    if (size == 1) {
      minimalView?.showCab(size)
      if (isBottomPanelEnabled){
        minimalView?.hideBottomLayoutWithAnimation()
      }
    } else if (size > 1 && !isBottomPanelEnabled) {
      isBottomPanelEnabled = true
      minimalView?.showBottomPanelWithAnimation()
      minimalView?.showCab(size)
    } else if (size == 0) {
      isBottomPanelEnabled = false
      minimalView?.hideBottomLayoutWithAnimation()
      minimalView?.hideCab()
    }
  }

  override fun handleItemLongClick(position: Int) {
    minimalView?.startSelection(position)
  }

  override fun handleScroll(yAxisMovement: Int) {
    if (isBottomPanelEnabled && yAxisMovement > MINIMUM_SCROLL_DIST && !forceSmoothScroll) {
      minimalView?.hideBottomLayoutWithAnimation()
      isBottomPanelEnabled = false
    } else if (!isBottomPanelEnabled && yAxisMovement < -MINIMUM_SCROLL_DIST) {
      if (selectionSize > 1) {
        minimalView?.showBottomPanelWithAnimation()
        isBottomPanelEnabled = true
      }
    }
  }

  override fun handleDeleteMenuItemClick() {

  }

  override fun handleCabDestroyed() {

  }

}