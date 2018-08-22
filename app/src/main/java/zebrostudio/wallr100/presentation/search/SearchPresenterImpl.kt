package zebrostudio.wallr100.presentation.search

import android.util.Log
import zebrostudio.wallr100.data.customexceptions.NoResultFoundException
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
    retrievePicturesUseCase.buildRetrievePicturesObservable(
        "photos/search?query=$query&per_page=30&page=$queryPage")
        .map {
          searchPicturesPresenterEntityMapper.mapTOPresenterEntity(it)
        }
        .subscribe({

        }, {
          when(it){
            is NoResultFoundException -> searchView?.showNoResultView()
            else -> searchView?.showGenericErrorMeesage()
          }
        })
  }

}