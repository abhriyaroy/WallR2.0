package zebrostudio.wallr100.data.mappertests

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.mapper.UnsplashPictureEntityMapper
import zebrostudio.wallr100.data.mapper.UnsplashPictureEntityMapperImpl
import zebrostudio.wallr100.data.model.unsplashmodel.ProfileImage
import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashPicturesEntity
import zebrostudio.wallr100.data.model.unsplashmodel.UrlEntity
import zebrostudio.wallr100.data.model.unsplashmodel.UserEntity
import zebrostudio.wallr100.domain.model.searchpictures.SearchPicturesModel
import zebrostudio.wallr100.domain.model.searchpictures.UrlModel
import zebrostudio.wallr100.domain.model.searchpictures.UserModel
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class UnsplashPictureEntityMapperTest {

  private lateinit var unsplashPictureEntityMapper: UnsplashPictureEntityMapper

  @Before fun setup() {
    unsplashPictureEntityMapper = UnsplashPictureEntityMapperImpl()
  }

  @Test
  fun `should return list of ImageModel on mapFromEntity call success with input of FirebaseImageEntity list`() {
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

    val unspalshPicturesEntity = UnsplashPicturesEntity(
        id,
        createdAt,
        imageWidth,
        imageHeight,
        color,
        UserEntity(authorName, ProfileImage(authorProfileImageUrl)),
        likes,
        likedByUser,
        UrlEntity(imageRawLink, imageLargeLink, imageMediumLink, imageSmallLink, imageThumbLink))

    val searchPicturesModel = SearchPicturesModel(id,
        createdAt,
        imageWidth,
        imageHeight,
        color,
        UserModel(authorName, authorProfileImageUrl),
        likes,
        likedByUser,
        UrlModel(imageRawLink, imageLargeLink, imageMediumLink, imageSmallLink, imageThumbLink))

    Assert.assertEquals(listOf(searchPicturesModel),
        unsplashPictureEntityMapper.mapFromEntity(listOf(unspalshPicturesEntity)))
  }

}