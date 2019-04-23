package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.presentation.datafactory.ImageModelFactory
import zebrostudio.wallr100.presentation.wallpaper.ImageListContract
import zebrostudio.wallr100.presentation.wallpaper.ImageListPresenterImpl
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.util.concurrent.TimeoutException

@RunWith(MockitoJUnitRunner::class)
class ImageListPresenterImplTest {

  @get:Rule val trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock lateinit var postExecutionThread: PostExecutionThread
  @Mock lateinit var wallpaperImagesUseCase: WallpaperImagesUseCase
  @Mock lateinit var imageListView: ImageListContract.ImageListView
  private lateinit var imagePresenterEntityMapper: ImagePresenterEntityMapper
  private lateinit var imageListPresenter: ImageListPresenterImpl
  private lateinit var testScopeProvider: TestLifecycleScopeProvider
  private val exploreTag = "Explore"
  private val topPicksTag = "Top Picks"
  private val categoriesTag = "Categories"

  @Before fun setup() {
    imagePresenterEntityMapper = ImagePresenterEntityMapper()
    imageListPresenter =
        ImageListPresenterImpl(wallpaperImagesUseCase, imagePresenterEntityMapper,
            postExecutionThread)
    imageListPresenter.attachView(imageListView)
    testScopeProvider = TestLifecycleScopeProvider.createInitial(
        TestLifecycleScopeProvider.TestLifecycle.STARTED)

    `when`(imageListView.getScope()).thenReturn(testScopeProvider)
    `when`(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  @Test
  fun `should return imagePresenterEntity list of explore images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.exploreImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(exploreTag, 0)

    imageListPresenter.fetchImages(false)

    assertTrue(0 == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of recent images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.recentImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(topPicksTag, 0)

    imageListPresenter.fetchImages(false)

    assertTrue(1 == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of popular images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.popularImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(topPicksTag, 1)

    imageListPresenter.fetchImages(false)

    assertTrue(2 == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of standout images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.standoutImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(topPicksTag, 2)

    imageListPresenter.fetchImages(false)

    assertTrue(3 == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of building images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.buildingsImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(categoriesTag, 0)

    imageListPresenter.fetchImages(false)

    assertTrue(4 == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of food images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.foodImagesSingle()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(categoriesTag, 1)
    imageListPresenter.fetchImages(false)

    assertTrue(5 == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of nature images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.natureImagesSingle()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(categoriesTag, 2)
    imageListPresenter.fetchImages(false)

    assertTrue(6 == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of object images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.objectsImagesSingle()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(categoriesTag, 3)
    imageListPresenter.fetchImages(false)

    assertTrue(7 == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of people images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.peopleImagesSingle()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(categoriesTag, 4)
    imageListPresenter.fetchImages(false)

    assertTrue(8 == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of technology images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.technologyImagesSingle()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(categoriesTag, 5)
    imageListPresenter.fetchImages(false)

    assertTrue(9 == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of technology images when fetchImages call with refresh true is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    val mappedPresenterEntityList = imagePresenterEntityMapper.mapToPresenterEntity(imageModelList)
    `when`(wallpaperImagesUseCase.technologyImagesSingle()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(categoriesTag, 5)
    imageListPresenter.fetchImages(true)

    assertTrue(9 == imageListPresenter.imageListType)
    verify(imageListView).hideAllLoadersAndMessageViews()
    verify(imageListView).hideLoader()
    verify(imageListView).showImageList(mappedPresenterEntityList)
    verify(imageListView).hideRefreshing()
    verify(imageListView).getScope()
    verifyNoMoreInteractions(imageListView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show no internet message view on fetchImages call without refresh failure due to timeout`() {
    `when`(wallpaperImagesUseCase.technologyImagesSingle()).thenReturn(
        Single.error(TimeoutException()))

    imageListPresenter.setImageListType(categoriesTag, 5)
    imageListPresenter.fetchImages(false)

    verify(imageListView).hideAllLoadersAndMessageViews()
    verify(imageListView).showLoader()
    verify(imageListView).hideLoader()
    verify(imageListView).showNoInternetMessageView()
    verify(imageListView).getScope()
    verifyNoMoreInteractions(imageListView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show no internet message view on fetchImages call with refresh failure due to timeout`() {
    `when`(wallpaperImagesUseCase.technologyImagesSingle()).thenReturn(
        Single.error(TimeoutException()))

    imageListPresenter.setImageListType(categoriesTag, 5)
    imageListPresenter.fetchImages(true)

    verify(imageListView).hideAllLoadersAndMessageViews()
    verify(imageListView).hideLoader()
    verify(imageListView).showNoInternetMessageView()
    verify(imageListView).hideRefreshing()
    verify(imageListView).getScope()
    verifyNoMoreInteractions(imageListView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  private fun `verify imageListView interactions on fetchImages with refresh false is a success`(
    imageModelList: List<ImageModel>
  ) {
    val mappedPresenterEntityList = imagePresenterEntityMapper.mapToPresenterEntity(imageModelList)
    verify(imageListView).hideAllLoadersAndMessageViews()
    verify(imageListView).showLoader()
    verify(imageListView).hideLoader()
    verify(imageListView).showImageList(mappedPresenterEntityList)
    verify(imageListView).getScope()
    verifyNoMoreInteractions(imageListView)
  }

  @After fun tearDown() {
    imageListPresenter.detachView()
  }

  private fun shouldVerifyPostExecutionThreadSchedulerCall() {
    verify(postExecutionThread).scheduler
    verifyNoMoreInteractions(postExecutionThread)
  }
}