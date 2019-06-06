package zebrostudio.wallr100.presentation.mappertests

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import zebrostudio.wallr100.domain.model.searchpictures.SearchPicturesModel
import zebrostudio.wallr100.domain.model.searchpictures.UrlModel
import zebrostudio.wallr100.domain.model.searchpictures.UserModel
import zebrostudio.wallr100.presentation.search.mapper.SearchPicturesPresenterEntityMapper
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.search.model.UrlPresenterEntity
import zebrostudio.wallr100.presentation.search.model.UserPresenterEntity
import java.util.UUID

class CollectionPicturesPresenterEntityMapperTest {

  private lateinit var searchPicturesPresenterEntityMapper: SearchPicturesPresenterEntityMapper
  @Before fun setup() {
    searchPicturesPresenterEntityMapper = SearchPicturesPresenterEntityMapper()
  }

  @Test
  fun `should return list of SearchPicturesPresenterEntity on mapToPresenterEntity call success`() {
    val id = UUID.randomUUID().toString()
    val createdAt = UUID.randomUUID().toString()
    val imageWidth = 1
    val imageHeight = 2
    val likes = 3
    val likedByUser = false
    val imageSmallLink = UUID.randomUUID().toString()
    val imageThumbLink = UUID.randomUUID().toString()
    val imageMediumLink = UUID.randomUUID().toString()
    val imageLargeLink = UUID.randomUUID().toString()
    val imageRawLink = UUID.randomUUID().toString()
    val authorName = UUID.randomUUID().toString()
    val authorProfileImageUrl = UUID.randomUUID().toString()
    val color = UUID.randomUUID().toString()

    val searchPicturesModel = SearchPicturesModel(id,
        createdAt,
        imageWidth,
        imageHeight,
        color,
        UserModel(authorName, authorProfileImageUrl),
        likes,
        likedByUser,
        UrlModel(imageRawLink, imageLargeLink, imageMediumLink, imageSmallLink, imageThumbLink))

    val searchPicturesPresenterEntity = SearchPicturesPresenterEntity(
        id,
        createdAt,
        imageWidth,
        imageHeight,
        color,
        UserPresenterEntity(authorName, authorProfileImageUrl),
        likes,
        likedByUser,
        UrlPresenterEntity(imageRawLink, imageLargeLink, imageMediumLink, imageSmallLink,
            imageThumbLink))

    assertEquals(listOf(searchPicturesPresenterEntity),
        searchPicturesPresenterEntityMapper.mapToPresenterEntity(listOf(searchPicturesModel)))
  }
}