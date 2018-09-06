package zebrostudio.wallr100.presentation.search

import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase

class SearchPresenterImpl(
  private var retrievePicturesUseCase: SearchPicturesUseCase,
  private var searchPicturesPresenterEntityMapper: SearchPicturesPresenterEntityMapper
) :
    SearchContract.SearchPresenter {

  private var searchView: SearchContract.SearchView? = null
  private var queryPage = 1

  override fun attachView(view: SearchContract.SearchView) {
    searchView = view
  }

  override fun detachView() {
    searchView = null
  }

  override fun notifyQuerySubmitted(query: String?) {
    queryPage = 1
    searchView?.showLoader()
    retrievePicturesUseCase.buildRetrievePicturesSingle(
        "photos/search?query=$query&per_page=30&page=$queryPage")
        .map {
          searchPicturesPresenterEntityMapper.mapTOPresenterEntity(it)
        }
        .subscribe({
          searchView?.hideLoader()
        }, {
          when (it) {
            is NoResultFoundException -> searchView?.showNoResultView(query)
            else -> {
              if (it.message != null && it.message == "Unable to resolve host \"api.unsplash.com\"" +
                  ": No address associated with hostname") {
                searchView?.showNoInternetView()
              } else {
                searchView?.showGenericErrorMessage()
              }
            }
          }
        })
  }

}