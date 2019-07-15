package zebrostudio.wallr100.presentation.mappertests

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.model.images.*
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper
import zebrostudio.wallr100.presentation.wallpaper.model.*
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class ImagePresenterEntityMapperTest {
  private lateinit var imagePresenterEntityMapper: ImagePresenterEntityMapper

  @Before
  fun setup() {
    imagePresenterEntityMapper = ImagePresenterEntityMapper()
  }

  @Test
  fun `should return list of ImagePresenterEntity on mapToPresenterEntity call success`() {
    val imageSmallLink = UUID.randomUUID().toString()
    val imageThumbLink = UUID.randomUUID().toString()
    val imageMediumLink = UUID.randomUUID().toString()
    val imageLargeLink = UUID.randomUUID().toString()
    val imageRawLink = UUID.randomUUID().toString()
    val authorName = UUID.randomUUID().toString()
    val authorProfileImageUrl = UUID.randomUUID().toString()
    val imageSmallResolution = UUID.randomUUID().toString()
    val imageThumbResolution = UUID.randomUUID().toString()
    val imageMediumResolution = UUID.randomUUID().toString()
    val imageLargeResolution = UUID.randomUUID().toString()
    val imageRawResolution = UUID.randomUUID().toString()
    val imageSmallSize: Long = 1
    val imageThumbSize: Long = 2
    val imageRegularSize: Long = 3
    val imageLargeSize: Long = 4
    val imageRawSize: Long = 5
    val color = UUID.randomUUID().toString()
    val timestamp = Long.MAX_VALUE
    val referral = UUID.randomUUID().toString()

    val imagePresenterEntity = ImagePresenterEntity(
      ImageLinkPresenterEntity(imageSmallLink, imageThumbLink, imageMediumLink, imageLargeLink,
        imageRawLink),
      ImageAuthorPresenterEntity(authorName, authorProfileImageUrl),
      ImageResolutionPresenterEntity(imageSmallResolution, imageThumbResolution,
        imageMediumResolution,
        imageLargeResolution, imageRawResolution),
      ImageSizePresenterEntity(imageSmallSize, imageThumbSize, imageRegularSize, imageLargeSize,
        imageRawSize),
      color, timestamp, referral)

    val imageModel = ImageModel(
      ImageLinkModel(imageSmallLink, imageThumbLink, imageMediumLink, imageLargeLink,
        imageRawLink),
      ImageAuthorModel(authorName, authorProfileImageUrl),
      ImageResolutionModel(imageSmallResolution, imageThumbResolution, imageMediumResolution,
        imageLargeResolution, imageRawResolution),
      ImageSizeModel(imageSmallSize, imageThumbSize, imageRegularSize, imageLargeSize,
        imageRawSize),
      color, timestamp, referral)

    Assert.assertEquals(listOf(imagePresenterEntity),
      imagePresenterEntityMapper.mapToPresenterEntity(listOf(imageModel)))
  }
}