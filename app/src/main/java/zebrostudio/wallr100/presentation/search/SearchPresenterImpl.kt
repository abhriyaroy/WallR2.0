package zebrostudio.wallr100.presentation.search

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.data.api.UrlMap.getQueryString
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase
import zebrostudio.wallr100.presentation.search.mapper.SearchPicturesPresenterEntityMapper

class SearchPresenterImpl(
  private var searchPicturesUseCase: SearchPicturesUseCase,
  private var searchPicturesPresenterEntityMapper: SearchPicturesPresenterEntityMapper,
  private var postExecutionThread: PostExecutionThread
) : SearchContract.SearchPresenter {

  private var searchView: SearchContract.SearchView? = null
  private var queryPage = 1
  internal var keyword: String = ""

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
        .observeOn(postExecutionThread.scheduler)
        .autoDisposable(searchView?.getScope()!!)
        .subscribe({
          searchView?.hideLoader()
          searchView?.showSearchResults(it)
          queryPage++
        }, {
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
    if (queryPage != 0) {
      searchView?.showBottomLoader()
      searchPicturesUseCase.buildUseCaseSingle(getQueryString(keyword, queryPage))
          .map {
            searchPicturesPresenterEntityMapper.mapToPresenterEntity(it)
          }
          .observeOn(postExecutionThread.scheduler)
          .autoDisposable(searchView?.getScope()!!)
          .subscribe({
            searchView?.hideBottomLoader()
            searchView?.appendSearchResults(((queryPage - 1) * 30), it) // 30 results per page
            queryPage++
          }, {
            searchView?.hideBottomLoader()
            when (it) {
              is NoResultFoundException -> queryPage = 0
              is UnableToResolveHostException -> {
                searchView?.setEndlessLoadingToFalse()
                searchView?.showNoInternetToast()
              }
              else -> {
                searchView?.setEndlessLoadingToFalse()
                searchView?.showGenericErrorToast()
              }
            }
          })
    }
  }

  override fun notifyRetryButtonClicked() {
    if (keyword != "") {
      notifyQuerySubmitted(keyword)
    } else {
      searchView?.showInputASearchQueryMessageView()
    }
  }

  override fun notifyActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
      val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      if (matches != null && matches.size > 0) {
        val searchWord = matches[0]
        if (!searchWord.isEmpty()) {
          searchView?.setSearchQueryWithoutSubmitting(searchWord)
        }
      }
    }
  }

}