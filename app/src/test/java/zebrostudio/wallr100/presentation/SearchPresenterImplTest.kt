package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider.createInitial
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.api.UrlMap.Companion.getQueryString
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase
import zebrostudio.wallr100.presentation.datafactory.SearchPicturesModelFactory
import zebrostudio.wallr100.presentation.search.SearchContract
import zebrostudio.wallr100.presentation.search.SearchPresenterImpl
import zebrostudio.wallr100.presentation.search.mapper.SearchPicturesPresenterEntityMapper
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.lang.Exception
import java.util.UUID.*

@RunWith(MockitoJUnitRunner::class)
class SearchPresenterImplTest {

  @get:Rule val trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock lateinit var searchView: SearchContract.SearchView
  @Mock lateinit var searchPicturesUseCase: SearchPicturesUseCase
  private lateinit var searchPicturesPresenterEntityMapper: SearchPicturesPresenterEntityMapper
  private lateinit var searchPresenterImpl: SearchPresenterImpl
  private lateinit var testLifecycleScopeProvider: TestLifecycleScopeProvider

  private val randomString = randomUUID().toString()
  private val queryPage = 1

  @Before fun setup() {
    searchPicturesPresenterEntityMapper = SearchPicturesPresenterEntityMapper()
    searchPresenterImpl =
        SearchPresenterImpl(searchPicturesUseCase, searchPicturesPresenterEntityMapper)
    searchPresenterImpl.attachView(searchView)
    testLifecycleScopeProvider = createInitial(TestLifecycleScopeProvider.TestLifecycle.STARTED)

    `when`(searchView.getScope()).thenReturn(testLifecycleScopeProvider)
  }

  @Test fun `should return query string`() {
    assertEquals(getQueryString(randomString, queryPage),
        "photos/search?query=$randomString&per_page=30&page=1")
  }

  @Test
  fun `should show no result view when notifyQuerySubmitted call succeeds but no result is found`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        getQueryString(randomString, queryPage))).thenReturn(
        Single.error(NoResultFoundException()))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).getScope()
    verify(searchView).showNoResultView(randomString)
    verifyNoMoreInteractions(searchView)
  }

  @Test
  fun `should show no internet view on notifyQuerySubmitted call failure due to no internet connection`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        getQueryString(randomString, queryPage))).thenReturn(
        Single.error(UnableToResolveHostException()))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).getScope()
    verify(searchView).showNoInternetView()
    verifyNoMoreInteractions(searchView)
  }

  @Test fun `should show generic error view on notifyQuerySubmitted call failure`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        getQueryString(randomString, queryPage))).thenReturn(
        Single.error(Exception()))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).getScope()
    verify(searchView).showGenericErrorView()
    verifyNoMoreInteractions(searchView)
  }

  @Test
  fun `should return list of searchPicturesPresenterEntity on notifyQuerySubmitted call success`() {
    val searchPicturesModelList =
        SearchPicturesModelFactory.getSearchPicturesModelList()
    val searchPicturesPresenterEntity =
        searchPicturesPresenterEntityMapper.mapToPresenterEntity(searchPicturesModelList)
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        getQueryString(randomString, queryPage))).thenReturn(
        Single.just(searchPicturesModelList))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    val argCaptor = argumentCaptor<List<SearchPicturesPresenterEntity>>()
    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).hideLoader()
    verify(searchView).getScope()
    verify(searchView).showSearchResults(argCaptor.capture())
    assertTrue(argCaptor.firstValue == searchPicturesPresenterEntity)
    verifyNoMoreInteractions(searchView)
  }

  @Test
  fun `should show no internet toast on fetchMoreImages call failure due to no internet connection`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        getQueryString("", queryPage))).thenReturn(
        Single.error(UnableToResolveHostException()))

    searchPresenterImpl.fetchMoreImages()

    verify(searchView).showBottomLoader()
    verify(searchView).getScope()
    verify(searchView).showNoInternetToast()
    verify(searchView).hideBottomLoader()
    verifyNoMoreInteractions(searchView)
  }

  @Test fun `should show generic error toast on fetchMoreImages call failure`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        getQueryString("", queryPage))).thenReturn(
        Single.error(Exception()))

    searchPresenterImpl.fetchMoreImages()

    verify(searchView).showBottomLoader()
    verify(searchView).getScope()
    verify(searchView).setEndlessLoadingToFalse()
    verify(searchView).showGenericErrorToast()
    verify(searchView).hideBottomLoader()
    verifyNoMoreInteractions(searchView)
  }

  @Test
  fun `should return list of search pictures presenter entity on fetchMoreImages call success`() {
    val searchPicturesModelList =
        SearchPicturesModelFactory.getSearchPicturesModelList()
    val searchPicturesPresenterEntity =
        searchPicturesPresenterEntityMapper.mapToPresenterEntity(searchPicturesModelList)
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        getQueryString("", queryPage))).thenReturn(
        Single.just(searchPicturesModelList))

    searchPresenterImpl.fetchMoreImages()

    val firstArgCaptor = argumentCaptor<Int>()
    val secondArgCaptor = argumentCaptor<List<SearchPicturesPresenterEntity>>()
    verify(searchView).showBottomLoader()
    verify(searchView).hideBottomLoader()
    verify(searchView).getScope()
    verify(searchView).appendSearchResults(firstArgCaptor.capture(), secondArgCaptor.capture())
    assertEquals(firstArgCaptor.firstValue, 0)
    assertTrue(secondArgCaptor.firstValue == searchPicturesPresenterEntity)
    verifyNoMoreInteractions(searchView)
  }

  @After fun tearDown() {
    searchPresenterImpl.detachView()
  }

}