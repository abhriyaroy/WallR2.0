package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract.ImageRecyclerItemView
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.SEARCH
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.WALLPAPERS
import zebrostudio.wallr100.presentation.datafactory.ImagePresenterEntityFactory
import zebrostudio.wallr100.presentation.datafactory.SearchPicturesPresenterEntityFactory
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

@RunWith(MockitoJUnitRunner::class)
class ImageRecyclerViewPresenterImplTest {

  @Mock lateinit var imageRecyclerItemView: ImageRecyclerItemView
  private lateinit var imageRecyclerViewPresenterImpl: ImageRecyclerViewPresenterImpl

  @Before
  fun setup() {
    imageRecyclerViewPresenterImpl = ImageRecyclerViewPresenterImpl()
  }

  @Test fun `should set list type to search on setListType call success`() {
    imageRecyclerViewPresenterImpl.setListType(SEARCH)

    assertEquals(SEARCH, imageRecyclerViewPresenterImpl.imageType)
  }

  @Test fun `should set list type to wallpapers on setListType call success`() {
    imageRecyclerViewPresenterImpl.setListType(WALLPAPERS)

    assertEquals(WALLPAPERS, imageRecyclerViewPresenterImpl.imageType)
  }

  @Test fun `should set list to search result list on setSearchResultList call success`() {
    val list = mutableListOf<SearchPicturesPresenterEntity>()
    list.add(SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity())
    imageRecyclerViewPresenterImpl.setSearchResultList(list)

    assertEquals(list, imageRecyclerViewPresenterImpl.searchResultList)
  }

  @Test fun `should set list to wallpapers image list on setWallpaperImageList call success`() {
    val list = mutableListOf<ImagePresenterEntity>()
    list.add(ImagePresenterEntityFactory.getImagePresenterEntity())
    imageRecyclerViewPresenterImpl.setWallpaperImageList(list)

    assertEquals(list, imageRecyclerViewPresenterImpl.wallpaperImageList)
  }

  @Test fun `should add item to search result list on setSearchResultList call success`() {
    val list =
        mutableListOf(SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity())
    val resultList = mutableListOf<SearchPicturesPresenterEntity>()
    resultList.addAll(list)
    resultList.addAll(list)
    imageRecyclerViewPresenterImpl.setSearchResultList(list)
    imageRecyclerViewPresenterImpl.addToSearchResultList(list)

    assertEquals(resultList, imageRecyclerViewPresenterImpl.searchResultList)
  }

  @Test
  fun `should set search image on onBindRepositoryRowViewAtPosition call success with image type as search`() {
    val position = 0
    val searchPicturesList =
        mutableListOf(SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity())
    imageRecyclerViewPresenterImpl.imageType = SEARCH
    imageRecyclerViewPresenterImpl.setSearchResultList(searchPicturesList)

    imageRecyclerViewPresenterImpl.onBindRepositoryRowViewAtPosition(position,
        imageRecyclerItemView)

    verify(imageRecyclerItemView).setImageViewBackgroundAndAttachClickListener(
        searchPicturesList[position].paletteColor)
    verify(imageRecyclerItemView).setSearchImage(
        searchPicturesList[position].imageQualityUrlPresenterEntity.smallImageLink)
  }

  @Test
  fun `should set wallpaper image on onBindRepositoryRowViewAtPosition call success with image type as wallpaper`() {
    val position = 0
    val wallpaperImagesList =
        mutableListOf(ImagePresenterEntityFactory.getImagePresenterEntity())
    imageRecyclerViewPresenterImpl.imageType = WALLPAPERS
    imageRecyclerViewPresenterImpl.setWallpaperImageList(wallpaperImagesList)

    imageRecyclerViewPresenterImpl.onBindRepositoryRowViewAtPosition(position,
        imageRecyclerItemView)

    verify(imageRecyclerItemView).setImageViewBackgroundAndAttachClickListener(
        wallpaperImagesList[position].color)
    verify(imageRecyclerItemView).setWallpaperImage(wallpaperImagesList[position].imageLink.thumb)
  }

  @Test
  fun `should return search pictures list size on getItemCount call success with image type as search`() {
    val searchPicturesList =
        mutableListOf(SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity())
    imageRecyclerViewPresenterImpl.imageType = SEARCH
    imageRecyclerViewPresenterImpl.setSearchResultList(searchPicturesList)

    assertEquals(searchPicturesList.size, imageRecyclerViewPresenterImpl.getItemCount())
  }

  @Test
  fun `should return wallpaper images list size on getItemCount call success with image type as wallpaper`() {
    val wallpaperImagesList =
        mutableListOf(ImagePresenterEntityFactory.getImagePresenterEntity())
    imageRecyclerViewPresenterImpl.imageType = WALLPAPERS
    imageRecyclerViewPresenterImpl.setWallpaperImageList(wallpaperImagesList)

    assertEquals(wallpaperImagesList.size, imageRecyclerViewPresenterImpl.getItemCount())
  }

  @Test fun `should clear all search results on clearAllSearchResults call success`() {
    val searchPicturesList =
        mutableListOf(SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity())
    imageRecyclerViewPresenterImpl.setSearchResultList(searchPicturesList)

    imageRecyclerViewPresenterImpl.clearAllSearchResults()

    assertEquals(0, imageRecyclerViewPresenterImpl.searchResultList.size)
  }

  @Test
  fun `should show search pictures details size on handleImageClicked call success with image type as search`() {
    val position = 0
    val searchPicturesList =
        mutableListOf(SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity())
    imageRecyclerViewPresenterImpl.imageType = SEARCH
    imageRecyclerViewPresenterImpl.setSearchResultList(searchPicturesList)

    imageRecyclerViewPresenterImpl.handleImageClicked(position, imageRecyclerItemView)

    verify(imageRecyclerItemView).showSearchImageDetails(searchPicturesList[position])
  }

  @Test
  fun `should show wallpaper images details size on handleImageClicked call success with image type as wallpaper`() {
    val position = 0
    val wallpaperImagesList =
        mutableListOf(ImagePresenterEntityFactory.getImagePresenterEntity())
    imageRecyclerViewPresenterImpl.imageType = WALLPAPERS
    imageRecyclerViewPresenterImpl.setWallpaperImageList(wallpaperImagesList)

    imageRecyclerViewPresenterImpl.handleImageClicked(position, imageRecyclerItemView)

    verify(imageRecyclerItemView).showWallpaperImageDetails(wallpaperImagesList[position])
  }

  @After fun tearDown() {
    verifyNoMoreInteractions(imageRecyclerItemView)
  }
}