package zebrostudio.wallr100.presentation.search

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase
import zebrostudio.wallr100.presentation.search.mapper.SearchPicturesPresenterEntityMapper

class SearchPresenterImpl(
  private var searchPicturesUseCase: SearchPicturesUseCase,
  private var searchPicturesPresenterEntityMapper: SearchPicturesPresenterEntityMapper
) :
    SearchContract.SearchPresenter {

  private var searchView: SearchContract.SearchView? = null
  private var queryPage = 1
  private var keyword: String? = null

  override fun attachView(view: SearchContract.SearchView) {
    searchView = view
  }

  override fun detachView() {
    searchView = null
  }

  override fun notifyQuerySubmitted(query: String?) {
    queryPage = 1
    searchView?.hideAll()
    searchView?.showLoader()
    keyword = query
    searchPicturesUseCase.buildUseCaseSingle(getQueryString(keyword))
        .map {
          searchPicturesPresenterEntityMapper.mapToPresenterEntity(it)
        }
        .autoDisposable(searchView?.getScope()!!)
        .subscribe({
          searchView?.hideLoader()
          searchView?.showSearchResults(it)
          queryPage++
        }, {
          when (it) {
            is NoResultFoundException -> searchView?.showNoResultView(keyword)
            else -> {
              if (it.message != null && it.message == "Unable to resolve host \"api.unsplash.com\"" +
                  ": No address associated with hostname") {
                searchView?.showNoInternetView()
              } else {
                searchView?.showGenericErrorView()
              }
            }
          }
        })
  }

  override fun fetchMoreImages() {
    if (queryPage != 0) {
      searchView?.showBottomLoader()
      searchPicturesUseCase.buildUseCaseSingle(getQueryString(keyword))
          .map {
            searchPicturesPresenterEntityMapper.mapToPresenterEntity(it)
          }
          .autoDisposable(searchView?.getScope()!!)
          .subscribe({
            searchView?.hideBottomLoader()
            searchView?.appendSearchResults(((queryPage - 1) * 30), it) // 30 results per page
            queryPage++
          }, {
            searchView?.hideBottomLoader()
            when (it) {
              is NoResultFoundException -> queryPage = 0
              else -> {
                if (it.message != null && it.message == "Unable to resolve host \"api.unsplash.com\"" +
                    ": No address associated with hostname") {
                  searchView?.showNoInternetToast()
                } else {
                  searchView?.showGenericErrorToast()
                }
              }
            }
          })
    }
  }

  fun getQueryString(keyword: String?): String {
    return "photos/search?query=$keyword&per_page=30&page=$queryPage"
  }

}