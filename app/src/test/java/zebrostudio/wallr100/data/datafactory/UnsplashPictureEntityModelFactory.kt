package zebrostudio.wallr100.data.datafactory

import zebrostudio.wallr100.data.model.unsplashmodel.ProfileImageEntity
import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashPicturesEntity
import zebrostudio.wallr100.data.model.unsplashmodel.UrlEntity
import zebrostudio.wallr100.data.model.unsplashmodel.UserEntity
import java.util.*
import java.util.UUID.randomUUID

object UnsplashPictureEntityModelFactory {

  fun getUnsplashPictureEntityModel(): UnsplashPicturesEntity {
    return UnsplashPicturesEntity(
      randomUUID().toString(),
      randomUUID().toString(),
      Random().nextInt(),
      Random().nextInt(),
      randomUUID().toString(),
      UserEntity(randomUUID().toString(),
        ProfileImageEntity(randomUUID().toString())),
      Random().nextInt(),
      true,
      UrlEntity(
        randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString()
      )
    )
  }

}