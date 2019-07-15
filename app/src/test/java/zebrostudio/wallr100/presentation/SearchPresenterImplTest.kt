package zebrostudio.wallr100.presentation

import android.app.Activity
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider.TestLifecycle.STARTED
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider.createInitial
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.api.UrlMap.getQueryString
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.domain.datafactory.SearchPicturesModelFactory.getSearchPicturesModel
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase
import zebrostudio.wallr100.presentation.search.SearchContract
import zebrostudio.wallr100.presentation.search.SearchPresenterImpl
import zebrostudio.wallr100.presentation.search.mapper.SearchPicturesPresenterEntityMapper
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class SearchPresenterImplTest {

  @Mock
  lateinit var postExecutionThread: PostExecutionThread
  @Mock
  lateinit var searchView: SearchContract.SearchView
  @Mock
  lateinit var searchPicturesUseCase: SearchPicturesUseCase
  private lateinit var searchPicturesPresenterEntityMapper: SearchPicturesPresenterEntityMapper
  private lateinit var searchPresenterImpl: SearchPresenterImpl

  private val randomString = randomUUID().toString()
  private val queryPage = 1

  @Before
  fun setup() {
    searchPicturesPresenterEntityMapper = SearchPicturesPresenterEntityMapper()
    searchPresenterImpl =
        SearchPresenterImpl(searchPicturesUseCase, searchPicturesPresenterEntityMapper,
          postExecutionThread)
    searchPresenterImpl.attachView(searchView)

    `when`(searchView.getScope()).thenReturn(createInitial(STARTED))
    `when`(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  @Test
  fun `should return query string on getQueryStringCallSuccess`() {
    assertEquals(getQueryString(randomString, queryPage),
      "photos/search?query=$randomString&per_page=30&page=1")
  }

  @Test
  fun `should show no result view when notifyQuerySubmitted call success but no result is found`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(getQueryString(randomString, queryPage)))
        .thenReturn(Single.error(NoResultFoundException()))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    verify(searchPicturesUseCase).buildUseCaseSingle(getQueryString(randomString, queryPage))
    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).getScope()
    verify(searchView).showNoResultView(randomString)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show no internet view on notifyQuerySubmitted call failure due to no internet connection`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(getQueryString(randomString, queryPage)))
        .thenReturn(Single.error(UnableToResolveHostException()))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    verify(searchPicturesUseCase).buildUseCaseSingle(getQueryString(randomString, queryPage))
    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).getScope()
    verify(searchView).showNoInternetView()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error view on notifyQuerySubmitted call failure`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(getQueryString(randomString, queryPage)))
        .thenReturn(Single.error(Exception()))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    verify(searchPicturesUseCase).buildUseCaseSingle(getQueryString(randomString, queryPage))
    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).getScope()
    verify(searchView).showGenericErrorView()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return list of searchPicturesPresenterEntity on notifyQuerySubmitted call success`() {
    val searchPicturesModelList = listOf(getSearchPicturesModel())
    val searchPicturesPresenterEntity =
        searchPicturesPresenterEntityMapper.mapToPresenterEntity(searchPicturesModelList)
    `when`(searchPicturesUseCase.buildUseCaseSingle(getQueryString(randomString, queryPage)))
        .thenReturn(Single.just(searchPicturesModelList))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    verify(searchPicturesUseCase).buildUseCaseSingle(getQueryString(randomString, queryPage))
    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).hideLoader()
    verify(searchView).getScope()
    verify(searchView).showSearchResults(searchPicturesPresenterEntity)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show no internet toast on fetchMoreImages call failure due to no internet connection`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(getQueryString("", queryPage)))
        .thenReturn(Single.error(UnableToResolveHostException()))

    searchPresenterImpl.fetchMoreImages()

    verify(searchPicturesUseCase).buildUseCaseSingle(getQueryString("", queryPage))
    verify(searchView).showBottomLoader()
    verify(searchView).getScope()
    verify(searchView).showNoInternetToast()
    verify(searchView).setEndlessLoadingToFalse()
    verify(searchView).hideBottomLoader()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error toast on fetchMoreImages call failure`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(getQueryString("", queryPage)))
        .thenReturn(Single.error(Exception()))

    searchPresenterImpl.fetchMoreImages()

    verify(searchPicturesUseCase).buildUseCaseSingle(getQueryString("", queryPage))
    verify(searchView).showBottomLoader()
    verify(searchView).getScope()
    verify(searchView).setEndlessLoadingToFalse()
    verify(searchView).showGenericErrorToast()
    verify(searchView).hideBottomLoader()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return list of search pictures presenter entity on fetchMoreImages call success`() {
    val searchPicturesModelList = listOf(getSearchPicturesModel())
    val searchPicturesPresenterEntity =
        searchPicturesPresenterEntityMapper.mapToPresenterEntity(searchPicturesModelList)
    `when`(searchPicturesUseCase.buildUseCaseSingle(getQueryString("", queryPage)))
        .thenReturn(Single.just(searchPicturesModelList))

    searchPresenterImpl.fetchMoreImages()

    verify(searchPicturesUseCase).buildUseCaseSingle(getQueryString("", queryPage))
    verify(searchView).showBottomLoader()
    verify(searchView).hideBottomLoader()
    verify(searchView).getScope()
    verify(searchView).appendSearchResults(0, searchPicturesPresenterEntity)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should call showInputASearchQueryMessageView on notifyRetryButtonClicked call failure due to empty keyword`() {
    searchPresenterImpl.notifyRetryButtonClicked()

    verify(searchView).showInputASearchQueryMessageView()
  }

  @Test
  fun `should show no internet toast on notifyRetryButtonClicked call failure due to no internet connection`() {
    searchPresenterImpl.keyword = randomString
    `when`(searchPicturesUseCase.buildUseCaseSingle(getQueryString(randomString, queryPage)))
        .thenReturn(Single.error(UnableToResolveHostException()))

    searchPresenterImpl.notifyRetryButtonClicked()

    verify(searchPicturesUseCase).buildUseCaseSingle(getQueryString(randomString, queryPage))
    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).getScope()
    verify(searchView).showNoInternetView()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error toast on notifyRetryButtonClicked call failure`() {
    searchPresenterImpl.keyword = randomString
    `when`(searchPicturesUseCase.buildUseCaseSingle(getQueryString(randomString, queryPage)))
        .thenReturn(Single.error(Exception()))

    searchPresenterImpl.notifyRetryButtonClicked()

    verify(searchPicturesUseCase).buildUseCaseSingle(getQueryString(randomString, queryPage))
    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).getScope()
    verify(searchView).showGenericErrorView()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return list of searchPicturesPresenterEntity when retry button is pressed and search keyword is valid and notifyQuerySubmitted call succeeds`() {
    val searchPicturesModelList = listOf(getSearchPicturesModel())
    val searchPicturesPresenterEntity =
        searchPicturesPresenterEntityMapper.mapToPresenterEntity(searchPicturesModelList)
    searchPresenterImpl.keyword = randomString
    `when`(searchPicturesUseCase.buildUseCaseSingle(getQueryString(randomString, queryPage)))
        .thenReturn(Single.just(searchPicturesModelList))

    searchPresenterImpl.notifyRetryButtonClicked()

    verify(searchPicturesUseCase).buildUseCaseSingle(getQueryString(randomString, queryPage))
    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).hideLoader()
    verify(searchView).getScope()
    verify(searchView).showSearchResults(searchPicturesPresenterEntity)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set search query without submitting when notifyActivityResult is called with valid requestCode, resultCode and data`() {
    val wordsArrayList = arrayListOf(randomString)
    `when`(searchView.recognizeWordsFromSpeech()).thenReturn(wordsArrayList)

    searchPresenterImpl.notifyActivityResult(
      MaterialSearchView.REQUEST_VOICE, Activity.RESULT_OK)

    verify(searchView).recognizeWordsFromSpeech()
    verify(searchView).setSearchQueryWithoutSubmitting(randomString)
  }

  @After
  fun tearDown() {
    verifyNoMoreInteractions(postExecutionThread, searchPicturesUseCase, searchView)
    searchPresenterImpl.detachView()
  }

  private fun verifyPostExecutionThreadSchedulerCall() {
    verify(postExecutionThread).scheduler
  }

}