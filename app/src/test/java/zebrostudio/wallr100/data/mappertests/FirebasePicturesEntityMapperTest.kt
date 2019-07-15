package zebrostudio.wallr100.data.mappertests

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapper
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapperImpl
import zebrostudio.wallr100.data.model.firebasedatabase.*
import zebrostudio.wallr100.domain.model.images.*
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class FirebasePicturesEntityMapperTest {

  private lateinit var firebasePictureEntityMapper: FirebasePictureEntityMapper

  @Before
  fun setup() {
    firebasePictureEntityMapper = FirebasePictureEntityMapperImpl()
  }

  @Test
  fun `should return list of ImageModel on mapFromEntity call success with input of FirebaseImageEntity list`() {
    val imageSmallLink = randomUUID().toString()
    val imageThumbLink = randomUUID().toString()
    val imageMediumLink = randomUUID().toString()
    val imageLargeLink = randomUUID().toString()
    val imageRawLink = randomUUID().toString()
    val authorName = randomUUID().toString()
    val authorProfileImageUrl = randomUUID().toString()
    val imageSmallResolution = randomUUID().toString()
    val imageThumbResolution = randomUUID().toString()
    val imageMediumResolution = randomUUID().toString()
    val imageLargeResolution = randomUUID().toString()
    val imageRawResolution = randomUUID().toString()
    val imageSmallSize: Long = 1
    val imageThumbSize: Long = 2
    val imageRegularSize: Long = 3
    val imageLargeSize: Long = 4
    val imageRawSize: Long = 5
    val color = randomUUID().toString()
    val timestamp = Long.MAX_VALUE
    val referral = randomUUID().toString()

    val firebaseImageEntity = FirebaseImageEntity(
      ImageLinkEntity(imageSmallLink, imageThumbLink, imageMediumLink, imageLargeLink,
        imageRawLink),
      ImageAuthorEntity(authorName, authorProfileImageUrl),
      ImageResolutionEntity(imageSmallResolution, imageThumbResolution, imageMediumResolution,
        imageLargeResolution, imageRawResolution),
      ImageSizeEntity(imageSmallSize, imageThumbSize, imageRegularSize, imageLargeSize,
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

    assertEquals(listOf(imageModel),
      firebasePictureEntityMapper.mapFromEntity(listOf(firebaseImageEntity)))
  }

}