package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider.createInitial
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.exception.NoResultFoundException
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

  @Before fun setup() {
    searchPicturesPresenterEntityMapper = SearchPicturesPresenterEntityMapper()
    searchPresenterImpl =
        SearchPresenterImpl(searchPicturesUseCase, searchPicturesPresenterEntityMapper)
    searchPresenterImpl.attachView(searchView)
    testLifecycleScopeProvider = createInitial(TestLifecycleScopeProvider.TestLifecycle.STARTED)

    `when`(searchView.getScope()).thenReturn(testLifecycleScopeProvider)
  }

  @Test fun `should return query string`() {
    assertEquals(searchPresenterImpl.getQueryString(randomString),
        "photos/search?query=$randomString&per_page=30&page=1")
  }

  @Test fun `should show no result view on notifyQuerySubmitted call`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        searchPresenterImpl.getQueryString(randomString))).thenReturn(
        Single.error(NoResultFoundException()))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).getScope()
    verify(searchView).showNoResultView(randomString)
    verifyNoMoreInteractions(searchView)
  }

  @Test fun `should show no internet view on notifyQuerySubmitted call`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        searchPresenterImpl.getQueryString(randomString))).thenReturn(
        Single.error(Exception("Unable to resolve host \"api.unsplash.com\"" +
            ": No address associated with hostname")))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).getScope()
    verify(searchView).showNoInternetView()
    verifyNoMoreInteractions(searchView)
  }

  @Test fun `should show generic error view on notifyQuerySubmitted call`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        searchPresenterImpl.getQueryString(randomString))).thenReturn(
        Single.error(Exception()))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).getScope()
    verify(searchView).showGenericErrorView()
    verifyNoMoreInteractions(searchView)
  }

  @Test
  fun `should return list of search pictures presenter entity on notifyQuerySubmitted call`() {
    val searchPicturesModelList =
        SearchPicturesModelFactory.getSearchPicturesModelList()
    val searchPicturesPresenterEntity =
        searchPicturesPresenterEntityMapper.mapToPresenterEntity(searchPicturesModelList)
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        searchPresenterImpl.getQueryString(randomString))).thenReturn(
        Single.just(searchPicturesModelList))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    val argCaptor = argumentCaptor<List<SearchPicturesPresenterEntity>>()
    verify(searchView).hideAllLoadersAndMessageViews()
    verify(searchView).showLoader()
    verify(searchView).hideLoader()
    verify(searchView).getScope()
    verify(searchView).showSearchResults(argCaptor.capture())
    verifyEntityEquality(argCaptor.firstValue, searchPicturesPresenterEntity)
    verifyNoMoreInteractions(searchView)
  }

  @Test fun `should show no internet toast on fetchMoreImages call`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        searchPresenterImpl.getQueryString(null))).thenReturn(
        Single.error(Exception("Unable to resolve host \"api.unsplash.com\"" +
            ": No address associated with hostname")))

    searchPresenterImpl.fetchMoreImages()

    verify(searchView).showBottomLoader()
    verify(searchView).getScope()
    verify(searchView).setEndlessLoadingToFalse()
    verify(searchView).showNoInternetToast()
    verify(searchView).hideBottomLoader()
    verifyNoMoreInteractions(searchView)
  }

  @Test fun `should show generic error toast on fetchMoreImages call`() {
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        searchPresenterImpl.getQueryString(null))).thenReturn(
        Single.error(Exception()))

    searchPresenterImpl.fetchMoreImages()

    verify(searchView).showBottomLoader()
    verify(searchView).getScope()
    verify(searchView).setEndlessLoadingToFalse()
    verify(searchView).showGenericErrorToast()
    verify(searchView).hideBottomLoader()
    verifyNoMoreInteractions(searchView)
  }

  @Test fun `should return list of search pictures presenter entity on fetchMoreImages call`() {
    val searchPicturesModelList =
        SearchPicturesModelFactory.getSearchPicturesModelList()
    val searchPicturesPresenterEntity =
        searchPicturesPresenterEntityMapper.mapToPresenterEntity(searchPicturesModelList)
    `when`(searchPicturesUseCase.buildUseCaseSingle(
        searchPresenterImpl.getQueryString(null))).thenReturn(
        Single.just(searchPicturesModelList))

    searchPresenterImpl.fetchMoreImages()

    val firstArgCaptor = argumentCaptor<Int>()
    val secondArgCaptor = argumentCaptor<List<SearchPicturesPresenterEntity>>()
    verify(searchView).showBottomLoader()
    verify(searchView).hideBottomLoader()
    verify(searchView).getScope()
    verify(searchView).appendSearchResults(firstArgCaptor.capture(), secondArgCaptor.capture())
    assertEquals(firstArgCaptor.firstValue, 0)
    verifyEntityEquality(secondArgCaptor.firstValue, searchPicturesPresenterEntity)
    verifyNoMoreInteractions(searchView)
  }

  @After fun tearDown() {
    searchPresenterImpl.detachView()
  }

  private fun verifyEntityEquality(
    searchPicturesPresenterEntityFirstList: List<SearchPicturesPresenterEntity>,
    searchPicturesPresenterEntitySecondList: List<SearchPicturesPresenterEntity>
  ) {
    assertEquals(searchPicturesPresenterEntityFirstList[0].id,
        searchPicturesPresenterEntitySecondList[0].id)
    assertEquals(searchPicturesPresenterEntityFirstList[0].createdAt,
        searchPicturesPresenterEntitySecondList[0].createdAt)
    assertEquals(searchPicturesPresenterEntityFirstList[0].imageWidth,
        searchPicturesPresenterEntitySecondList[0].imageWidth)
    assertEquals(searchPicturesPresenterEntityFirstList[0].imageHeight,
        searchPicturesPresenterEntitySecondList[0].imageHeight)
    assertEquals(searchPicturesPresenterEntityFirstList[0].paletteColor,
        searchPicturesPresenterEntitySecondList[0].paletteColor)
    assertEquals(searchPicturesPresenterEntityFirstList[0].userPresenterEntity.name,
        searchPicturesPresenterEntitySecondList[0].userPresenterEntity.name)
    assertEquals(searchPicturesPresenterEntityFirstList[0].userPresenterEntity.profileImageLink,
        searchPicturesPresenterEntitySecondList[0].userPresenterEntity.profileImageLink)
    assertEquals(searchPicturesPresenterEntityFirstList[0].likes,
        searchPicturesPresenterEntitySecondList[0].likes)
    assertEquals(searchPicturesPresenterEntityFirstList[0].likedByUser,
        searchPicturesPresenterEntitySecondList[0].likedByUser)
    assertEquals(
        searchPicturesPresenterEntityFirstList[0].imageQualityUrlPresenterEntity.rawImageLink,
        searchPicturesPresenterEntitySecondList[0].imageQualityUrlPresenterEntity.rawImageLink)
    assertEquals(
        searchPicturesPresenterEntityFirstList[0].imageQualityUrlPresenterEntity.largeImageLink,
        searchPicturesPresenterEntitySecondList[0].imageQualityUrlPresenterEntity.largeImageLink)
    assertEquals(
        searchPicturesPresenterEntityFirstList[0].imageQualityUrlPresenterEntity.regularImageLink,
        searchPicturesPresenterEntitySecondList[0].imageQualityUrlPresenterEntity.regularImageLink)
    assertEquals(
        searchPicturesPresenterEntityFirstList[0].imageQualityUrlPresenterEntity.smallImageLink,
        searchPicturesPresenterEntitySecondList[0].imageQualityUrlPresenterEntity.smallImageLink)
    assertEquals(
        searchPicturesPresenterEntityFirstList[0].imageQualityUrlPresenterEntity.thumbImageLink,
        searchPicturesPresenterEntitySecondList[0].imageQualityUrlPresenterEntity.thumbImageLink)
  }

}