package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.domain.datafactory.SearchPicturesModelFactory
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class SearchPicturesUseCaseTest {

  @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock lateinit var wallrRepository: WallrRepository
  @Mock lateinit var postExecutionThread: PostExecutionThread
  lateinit var searchPicturesUseCase: SearchPicturesUseCase
  private val dummyString = UUID.randomUUID().toString()

  @Before fun setup() {
    searchPicturesUseCase = SearchPicturesUseCase(wallrRepository, postExecutionThread)

    whenever(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  @Test fun `should return list of search pictures model on buildRetrievePicturesObservable`() {
    val searchPicturesModelList = listOf(SearchPicturesModelFactory.getSearchPicturesModel())
    whenever(wallrRepository.getPictures(dummyString)).thenReturn(
        Single.just(searchPicturesModelList))

    val picture = searchPicturesUseCase.buildRetrievePicturesSingle(dummyString)
        .test()
        .values()[0][0]

    Assert.assertEquals(searchPicturesModelList[0].id, picture.id)
    Assert.assertEquals(searchPicturesModelList[0].createdAt, picture.createdAt)
    Assert.assertEquals(searchPicturesModelList[0].imageWidth, picture.imageWidth)
    Assert.assertEquals(searchPicturesModelList[0].imageHeight, picture.imageHeight)
    Assert.assertEquals(searchPicturesModelList[0].paletteColor, picture.paletteColor)
    Assert.assertEquals(searchPicturesModelList[0].user, picture.user)
    Assert.assertEquals(searchPicturesModelList[0].likes, picture.likes)
    Assert.assertEquals(searchPicturesModelList[0].likedByUser, picture.likedByUser)
    Assert.assertEquals(searchPicturesModelList[0].imageQualityUrls, picture.imageQualityUrls)
    Assert.assertEquals(searchPicturesModelList[0].categories, picture.categories)
  }

  @Test
  fun `should return no result found exception of search pictures model on buildRetrievePicturesObservable`() {
    whenever(wallrRepository.getPictures(dummyString)).thenReturn(
        Single.error(NoResultFoundException()))

    searchPicturesUseCase.buildRetrievePicturesSingle(dummyString)
        .test()
        .assertError(NoResultFoundException::class.java)
  }
}