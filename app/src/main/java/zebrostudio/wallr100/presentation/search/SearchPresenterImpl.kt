package zebrostudio.wallr100.presentation.search

import android.util.Log
import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.data.api.UrlMap.Companion.getQueryString
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase
import zebrostudio.wallr100.presentation.search.mapper.SearchPicturesPresenterEntityMapper

class SearchPresenterImpl(
  private var searchPicturesUseCase: SearchPicturesUseCase,
  private var searchPicturesPresenterEntityMapper: SearchPicturesPresenterEntityMapper
) :
    SearchContract.SearchPresenter {

  private var searchView: SearchContract.SearchView? = null
  private var queryPage = 1
  private var keyword: String = ""

  override fun attachView(view: SearchContract.SearchView) {
    searchView = view
  }

  override fun detachView() {
    searchView = null
  }

  override fun notifyQuerySubmitted(query: String) {
    queryPage = 1
    searchView?.hideAllLoadersAndMessageViews()
    searchView?.showLoader()
    keyword = query
    searchPicturesUseCase.buildUseCaseSingle(getQueryString(keyword, queryPage))
        .map {
          searchPicturesPresenterEntityMapper.mapToPresenterEntity(it)
        }
        .autoDisposable(searchView?.getScope()!!)
        .subscribe({
          searchView?.hideLoader()
          searchView?.showSearchResults(it)
          queryPage++
        }, {
          System.out.println(it.message)
          when (it) {
            is NoResultFoundException -> searchView?.showNoResultView(keyword)
            is UnableToResolveHostException -> searchView?.showNoInternetView()
            else -> {
              searchView?.showGenericErrorView()
            }
          }
        })
  }

  override fun fetchMoreImages() {
    System.out.println("keyword $keyword")
    if (queryPage != 0) {
      searchView?.showBottomLoader()
      searchPicturesUseCase.buildUseCaseSingle(getQueryString(keyword, queryPage))
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
              is UnableToResolveHostException -> searchView?.showNoInternetToast()
              else -> {
                searchView?.setEndlessLoadingToFalse()
                searchView?.showGenericErrorToast()
              }
            }
          })
    }
  }

  override fun notifyRetryButtonClicked() {
    if (keyword != null) {
      notifyQuerySubmitted(keyword)
    } else {
      searchView?.showInputASearchQueryMessageView()
    }
  }

}