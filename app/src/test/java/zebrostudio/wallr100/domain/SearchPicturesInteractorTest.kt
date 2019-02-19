package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.domain.datafactory.SearchPicturesModelFactory
import zebrostudio.wallr100.domain.interactor.SearchPicturesInteractor
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class SearchPicturesInteractorTest {

  @get:Rule val trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock lateinit var wallrRepository: WallrRepository
  private lateinit var searchPicturesUseCase: SearchPicturesUseCase
  private val dummyString = randomUUID().toString()

  @Before fun setup() {
    searchPicturesUseCase = SearchPicturesInteractor(wallrRepository)
  }

  @Test
  fun `should return list of search pictures model on buildRetrievePicturesObservable call success`() {
    val searchPicturesModelList = listOf(SearchPicturesModelFactory.getSearchPicturesModel())
    `when`(wallrRepository.getSearchPictures(dummyString)).thenReturn(
        Single.just(searchPicturesModelList))

    val picture = searchPicturesUseCase.buildUseCaseSingle(dummyString)
        .test()
        .values()[0][0]

    assertEquals(picture, searchPicturesModelList[0])

    verify(wallrRepository).getSearchPictures(dummyString)
    verifyNoMoreInteractions(wallrRepository)
  }

  @Test
  fun `should return no result found exception of search pictures model on buildRetrievePicturesObservable call success but with no result`() {
    `when`(wallrRepository.getSearchPictures(dummyString)).thenReturn(
        Single.error(NoResultFoundException()))

    searchPicturesUseCase.buildUseCaseSingle(dummyString)
        .test()
        .assertError(NoResultFoundException::class.java)

    verify(wallrRepository).getSearchPictures(dummyString)
    verifyNoMoreInteractions(wallrRepository)
  }

  @Test
  fun `should return unable to resolve host exception of search pictures model on buildRetrievePicturesObservable call failure`() {
    `when`(wallrRepository.getSearchPictures(dummyString)).thenReturn(
        Single.error(UnableToResolveHostException()))

    searchPicturesUseCase.buildUseCaseSingle(dummyString)
        .test()
        .assertError(UnableToResolveHostException::class.java)

    verify(wallrRepository).getSearchPictures(dummyString)
    verifyNoMoreInteractions(wallrRepository)
  }

}