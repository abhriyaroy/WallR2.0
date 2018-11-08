package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.ui.wallpaper.WallpaperFragment
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.presentation.datafactory.ImageModelFactory
import zebrostudio.wallr100.presentation.wallpaper.ImageListContract
import zebrostudio.wallr100.presentation.wallpaper.ImageListPresenterImpl
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.util.concurrent.TimeoutException

@RunWith(MockitoJUnitRunner::class)
class ImageListPresenterTest {

  @get:Rule val trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock lateinit var wallpaperImagesUseCase: WallpaperImagesUseCase
  @Mock lateinit var imageListView: ImageListContract.ImageListView
  private lateinit var imagePresenterEntityMapper: ImagePresenterEntityMapper
  private lateinit var imageListPresenter: ImageListPresenterImpl
  private lateinit var testScopeProvider: TestLifecycleScopeProvider
  private val imageListTypes = listOf(
      "EXPLORE",
      "RECENT",
      "POPULAR",
      "STANDOUTS",
      "BUILDINGS",
      "FOOD",
      "NATURE",
      "OBJECTS",
      "PEOPLE",
      "TECHNOLOGY"
  )

  @Before fun setup() {
    imagePresenterEntityMapper = ImagePresenterEntityMapper()
    imageListPresenter = ImageListPresenterImpl(wallpaperImagesUseCase, imagePresenterEntityMapper)
    imageListPresenter.attachView(imageListView)
    testScopeProvider = TestLifecycleScopeProvider.createInitial(
        TestLifecycleScopeProvider.TestLifecycle.STARTED)

    `when`(imageListView.getScope()).thenReturn(testScopeProvider)
  }

  @Test
  fun `should return imagePresenterEntity list of explore images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.getExploreImages()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(WallpaperFragment.EXPLORE_FRAGMENT_TAG, 0)
    imageListPresenter.fetchImages(false)

    assertTrue(imageListTypes[0] == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
  }

  @Test
  fun `should return imagePresenterEntity list of recent images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.getRecentImages()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(WallpaperFragment.TOP_PICKS_FRAGMENT_TAG, 0)
    imageListPresenter.fetchImages(false)

    assertTrue(imageListTypes[1] == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
  }

  @Test
  fun `should return imagePresenterEntity list of popular images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.getPopularImages()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(WallpaperFragment.TOP_PICKS_FRAGMENT_TAG, 1)
    imageListPresenter.fetchImages(false)

    assertTrue(imageListTypes[2] == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
  }

  @Test
  fun `should return imagePresenterEntity list of standout images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.getStandoutImages()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(WallpaperFragment.TOP_PICKS_FRAGMENT_TAG, 2)
    imageListPresenter.fetchImages(false)

    assertTrue(imageListTypes[3] == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
  }

  @Test
  fun `should return imagePresenterEntity list of building images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.getBuildingsImages()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(WallpaperFragment.CATEGORIES_FRAGMENT_TAG, 0)
    imageListPresenter.fetchImages(false)

    assertTrue(imageListTypes[4] == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
  }

  @Test
  fun `should return imagePresenterEntity list of food images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.getFoodImages()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(WallpaperFragment.CATEGORIES_FRAGMENT_TAG, 1)
    imageListPresenter.fetchImages(false)

    assertTrue(imageListTypes[5] == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
  }

  @Test
  fun `should return imagePresenterEntity list of nature images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.getNatureImages()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(WallpaperFragment.CATEGORIES_FRAGMENT_TAG, 2)
    imageListPresenter.fetchImages(false)

    assertTrue(imageListTypes[6] == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
  }

  @Test
  fun `should return imagePresenterEntity list of object images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.getObjectsImages()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(WallpaperFragment.CATEGORIES_FRAGMENT_TAG, 3)
    imageListPresenter.fetchImages(false)

    assertTrue(imageListTypes[7] == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
  }

  @Test
  fun `should return imagePresenterEntity list of people images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.getPeopleImages()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(WallpaperFragment.CATEGORIES_FRAGMENT_TAG, 4)
    imageListPresenter.fetchImages(false)

    assertTrue(imageListTypes[8] == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
  }

  @Test
  fun `should return imagePresenterEntity list of technology images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    `when`(wallpaperImagesUseCase.getTechnologyImages()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(WallpaperFragment.CATEGORIES_FRAGMENT_TAG, 5)
    imageListPresenter.fetchImages(false)

    assertTrue(imageListTypes[9] == imageListPresenter.imageListType)
    `verify imageListView interactions on fetchImages with refresh false is a success`(
        imageModelList)
  }

  @Test
  fun `should return imagePresenterEntity list of technology images when fetchImages call with refresh true is success`() {
    val imageModelList = listOf(ImageModelFactory.getImageModel())
    val mappedPresenterEntityList = imagePresenterEntityMapper.mapToPresenterEntity(imageModelList)
    `when`(wallpaperImagesUseCase.getTechnologyImages()).thenReturn(Single.just(imageModelList))

    imageListPresenter.setImageListType(WallpaperFragment.CATEGORIES_FRAGMENT_TAG, 5)
    imageListPresenter.fetchImages(true)

    assertTrue(imageListTypes[9] == imageListPresenter.imageListType)
    verify(imageListView).hideAllLoadersAndMessageViews()
    verify(imageListView).hideLoader()
    verify(imageListView).showImageList(mappedPresenterEntityList)
    verify(imageListView).hideRefreshing()
    verify(imageListView).getScope()
    verifyNoMoreInteractions(imageListView)
  }

  @Test
  fun `should show no internet message view on fetchImages call without refresh failure due to timeout`() {
    `when`(wallpaperImagesUseCase.getTechnologyImages()).thenReturn(
        Single.error(TimeoutException()))

    imageListPresenter.setImageListType(WallpaperFragment.CATEGORIES_FRAGMENT_TAG, 5)
    imageListPresenter.fetchImages(false)

    verify(imageListView).hideAllLoadersAndMessageViews()
    verify(imageListView).showLoader()
    verify(imageListView).hideLoader()
    verify(imageListView).showNoInternetMessageView()
    verify(imageListView).getScope()
    verifyNoMoreInteractions(imageListView)
  }

  @Test
  fun `should show no internet message view on fetchImages call with refresh failure due to timeout`() {
    `when`(wallpaperImagesUseCase.getTechnologyImages()).thenReturn(
        Single.error(TimeoutException()))

    imageListPresenter.setImageListType(WallpaperFragment.CATEGORIES_FRAGMENT_TAG, 5)
    imageListPresenter.fetchImages(true)

    verify(imageListView).hideAllLoadersAndMessageViews()
    verify(imageListView).hideLoader()
    verify(imageListView).showNoInternetMessageView()
    verify(imageListView).hideRefreshing()
    verify(imageListView).getScope()
    verifyNoMoreInteractions(imageListView)
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
}