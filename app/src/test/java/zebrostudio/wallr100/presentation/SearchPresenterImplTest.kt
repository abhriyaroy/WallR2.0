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
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class SearchPresenterImplTest {

  @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock lateinit var searchView: SearchContract.SearchView
  @Mock lateinit var searchPicturesUseCase: SearchPicturesUseCase
  private lateinit var searchPicturesPresenterEntityMapper: SearchPicturesPresenterEntityMapper
  private lateinit var searchPresenterImpl: SearchPresenterImpl
  private lateinit var testLifecycleScopeProvider: TestLifecycleScopeProvider

  private val randomString = UUID.randomUUID().toString()

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

    verify(searchView).hideAll()
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

    verify(searchView).hideAll()
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

    verify(searchView).hideAll()
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
    verify(searchView).hideAll()
    verify(searchView).showLoader()
    verify(searchView).hideLoader()
    verify(searchView).getScope()
    verify(searchView).showSearchResults(argCaptor.capture())
    assertEquals(argCaptor.firstValue[0].id, searchPicturesPresenterEntity[0].id)
    assertEquals(argCaptor.firstValue[0].createdAt,
        searchPicturesPresenterEntity[0].createdAt)
    assertEquals(argCaptor.firstValue[0].imageWidth,
        searchPicturesPresenterEntity[0].imageWidth)
    assertEquals(argCaptor.firstValue[0].imageHeight,
        searchPicturesPresenterEntity[0].imageHeight)
    assertEquals(argCaptor.firstValue[0].paletteColor,
        searchPicturesPresenterEntity[0].paletteColor)
    assertEquals(argCaptor.firstValue[0].user.name,
        searchPicturesPresenterEntity[0].user.name)
    assertEquals(argCaptor.firstValue[0].user.profileImage.mediumImageUrl,
        searchPicturesPresenterEntity[0].user.profileImage.mediumImageUrl)
    assertEquals(argCaptor.firstValue[0].likes, searchPicturesPresenterEntity[0].likes)
    assertEquals(argCaptor.firstValue[0].likedByUser,
        searchPicturesPresenterEntity[0].likedByUser)
    assertEquals(argCaptor.firstValue[0].imageQualityUrls.rawImageLink,
        searchPicturesPresenterEntity[0].imageQualityUrls.rawImageLink)
    assertEquals(argCaptor.firstValue[0].imageQualityUrls.largeImageLink,
        searchPicturesPresenterEntity[0].imageQualityUrls.largeImageLink)
    assertEquals(argCaptor.firstValue[0].imageQualityUrls.regularImageLink,
        searchPicturesPresenterEntity[0].imageQualityUrls.regularImageLink)
    assertEquals(argCaptor.firstValue[0].imageQualityUrls.smallImageLink,
        searchPicturesPresenterEntity[0].imageQualityUrls.smallImageLink)
    assertEquals(argCaptor.firstValue[0].imageQualityUrls.thumbImageLink,
        searchPicturesPresenterEntity[0].imageQualityUrls.thumbImageLink)
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
    assertEquals(secondArgCaptor.firstValue[0].id, searchPicturesPresenterEntity[0].id)
    assertEquals(secondArgCaptor.firstValue[0].createdAt,
        searchPicturesPresenterEntity[0].createdAt)
    assertEquals(secondArgCaptor.firstValue[0].imageWidth,
        searchPicturesPresenterEntity[0].imageWidth)
    assertEquals(secondArgCaptor.firstValue[0].imageHeight,
        searchPicturesPresenterEntity[0].imageHeight)
    assertEquals(secondArgCaptor.firstValue[0].paletteColor,
        searchPicturesPresenterEntity[0].paletteColor)
    assertEquals(secondArgCaptor.firstValue[0].user.name,
        searchPicturesPresenterEntity[0].user.name)
    assertEquals(secondArgCaptor.firstValue[0].user.profileImage.mediumImageUrl,
        searchPicturesPresenterEntity[0].user.profileImage.mediumImageUrl)
    assertEquals(secondArgCaptor.firstValue[0].likes, searchPicturesPresenterEntity[0].likes)
    assertEquals(secondArgCaptor.firstValue[0].likedByUser,
        searchPicturesPresenterEntity[0].likedByUser)
    assertEquals(secondArgCaptor.firstValue[0].imageQualityUrls.rawImageLink,
        searchPicturesPresenterEntity[0].imageQualityUrls.rawImageLink)
    assertEquals(secondArgCaptor.firstValue[0].imageQualityUrls.largeImageLink,
        searchPicturesPresenterEntity[0].imageQualityUrls.largeImageLink)
    assertEquals(secondArgCaptor.firstValue[0].imageQualityUrls.regularImageLink,
        searchPicturesPresenterEntity[0].imageQualityUrls.regularImageLink)
    assertEquals(secondArgCaptor.firstValue[0].imageQualityUrls.smallImageLink,
        searchPicturesPresenterEntity[0].imageQualityUrls.smallImageLink)
    assertEquals(secondArgCaptor.firstValue[0].imageQualityUrls.thumbImageLink,
        searchPicturesPresenterEntity[0].imageQualityUrls.thumbImageLink)
    verifyNoMoreInteractions(searchView)
  }

  @After fun tearDown() {
    searchPresenterImpl.detachView()
  }

}