package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.datafactory.ImageModelFactory
import zebrostudio.wallr100.domain.interactor.WallpaperImagesInteractor

@RunWith(MockitoJUnitRunner::class)
class WallpaperImagesUseCaseTest {

  @Mock
  private lateinit var wallrRepository: WallrRepository

  private lateinit var wallpaperImagesInteractor: WallpaperImagesInteractor
  private var imageModelList = listOf(ImageModelFactory.getImageModel())

  @Before
  fun setup() {
    wallpaperImagesInteractor = WallpaperImagesInteractor(wallrRepository)
  }

  @Test
  fun `should return single on exploreImagesSingle call success`() {
    `when`(wallrRepository.getExplorePictures()).thenReturn(Single.just(imageModelList))

    val imageList = wallpaperImagesInteractor.exploreImagesSingle().test().values()[0][0]

    assertEquals(imageModelList[0], imageList)
    verify(wallrRepository).getExplorePictures()
  }

  @Test
  fun `should return single on recentImagesSingle call success`() {
    `when`(wallrRepository.getRecentPictures()).thenReturn(Single.just(imageModelList))

    val imageList = wallpaperImagesInteractor.recentImagesSingle().test().values()[0][0]

    assertEquals(imageModelList[0], imageList)
    verify(wallrRepository).getRecentPictures()
  }

  @Test
  fun `should return single on popularImagesSingle call success`() {
    `when`(wallrRepository.getPopularPictures()).thenReturn(Single.just(imageModelList))

    val imageList = wallpaperImagesInteractor.popularImagesSingle().test().values()[0][0]

    assertEquals(imageModelList[0], imageList)
    verify(wallrRepository).getPopularPictures()
  }

  @Test
  fun `should return single on standoutImagesSingle call success`() {
    `when`(wallrRepository.getStandoutPictures()).thenReturn(Single.just(imageModelList))

    val imageList = wallpaperImagesInteractor.standoutImagesSingle().test().values()[0][0]

    assertEquals(imageModelList[0], imageList)
    verify(wallrRepository).getStandoutPictures()
  }

  @Test
  fun `should return single on buildingImagesSingle call success`() {
    `when`(wallrRepository.getBuildingsPictures()).thenReturn(Single.just(imageModelList))

    val imageList = wallpaperImagesInteractor.buildingsImagesSingle().test().values()[0][0]

    assertTrue(imageModelList[0] == imageList)
    verify(wallrRepository).getBuildingsPictures()
  }

  @Test
  fun `should return single on foodImagesSingle call success`() {
    `when`(wallrRepository.getFoodPictures()).thenReturn(Single.just(imageModelList))

    val imageList = wallpaperImagesInteractor.foodImagesSingle().test().values()[0][0]

    assertEquals(imageModelList[0], imageList)
    verify(wallrRepository).getFoodPictures()
  }

  @Test
  fun `should return single on natureImagesSingle call success`() {
    `when`(wallrRepository.getNaturePictures()).thenReturn(Single.just(imageModelList))

    val imageList = wallpaperImagesInteractor.natureImagesSingle().test().values()[0][0]

    assertEquals(imageModelList[0], imageList)
    verify(wallrRepository).getNaturePictures()
  }

  @Test
  fun `should return single on objectImagesSingle call success`() {
    `when`(wallrRepository.getObjectsPictures()).thenReturn(Single.just(imageModelList))

    val imageList = wallpaperImagesInteractor.objectsImagesSingle().test().values()[0][0]

    assertEquals(imageModelList[0], imageList)
    verify(wallrRepository).getObjectsPictures()
  }

  @Test
  fun `should return single on peopleImagesSingle call success`() {
    `when`(wallrRepository.getPeoplePictures()).thenReturn(Single.just(imageModelList))

    val imageList = wallpaperImagesInteractor.peopleImagesSingle().test().values()[0][0]

    assertEquals(imageModelList[0], imageList)
    verify(wallrRepository).getPeoplePictures()
  }

  @Test
  fun `should return single on technologyImagesSingle call success`() {
    `when`(wallrRepository.getTechnologyPictures()).thenReturn(Single.just(imageModelList))

    val imageList = wallpaperImagesInteractor.technologyImagesSingle().test().values()[0][0]

    assertEquals(imageModelList[0], imageList)
    verify(wallrRepository).getTechnologyPictures()
  }

  @After
  fun tearDown() {
    verifyNoMoreInteractions(wallrRepository)
  }

}