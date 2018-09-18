package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.SearchPicturesInteractor
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase
import zebrostudio.wallr100.presentation.search.SearchContract
import zebrostudio.wallr100.presentation.search.SearchPresenterImpl
import zebrostudio.wallr100.presentation.search.mapper.SearchPicturesPresenterEntityMapper
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class SearchPresenterImplTest {

  @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock lateinit var searchView: SearchContract.SearchView
  @Mock lateinit var searchPicturesUseCase: SearchPicturesUseCase
  private lateinit var searchPicturesPresenterEntityMapper: SearchPicturesPresenterEntityMapper
  private lateinit var searchPresenterImpl: SearchPresenterImpl

  private val randomString = UUID.randomUUID().toString()

  @Before fun setup() {
    searchPicturesPresenterEntityMapper = SearchPicturesPresenterEntityMapper()
    searchPresenterImpl =
        SearchPresenterImpl(searchPicturesUseCase, searchPicturesPresenterEntityMapper)
    searchPresenterImpl.attachView(searchView)
  }

  @Test fun `should return query string`() {
    assertEquals(searchPresenterImpl.getQueryString(randomString),
        "photos/search?query=$randomString&per_page=30&page=1")
  }

  @Test fun `should show no internet view on notifyQuerySubmitted call`() {
    whenever(searchPicturesUseCase.buildUseCaseSingle(
        searchPresenterImpl.getQueryString(randomString))).thenReturn(
        Single.error(NoResultFoundException()))

    searchPresenterImpl.notifyQuerySubmitted(randomString)

    verify(searchView).hideAll()
    verify(searchView).showLoader()
    verify(searchView).showNoResultView(randomString)
    verifyNoMoreInteractions(searchView)
  }

  @After fun cleanUp() {
    searchPresenterImpl.detachView()
  }

}