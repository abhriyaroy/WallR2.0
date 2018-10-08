package zebrostudio.wallr100.data.datafactory

import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashPicturesEntity
import zebrostudio.wallr100.data.model.unsplashmodel.ProfileImage
import zebrostudio.wallr100.data.model.unsplashmodel.UrlEntity
import zebrostudio.wallr100.data.model.unsplashmodel.UserEntity
import java.util.Random
import java.util.UUID

object UnsplashPictureEntityModelFactory {

  fun getUnsplashPictureEntityModel(): UnsplashPicturesEntity {
    return UnsplashPicturesEntity(
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString(),
        Random().nextInt(),
        Random().nextInt(),
        UUID.randomUUID().toString(),
        UserEntity(UUID.randomUUID().toString(),
            ProfileImage(UUID.randomUUID().toString())),
        Random().nextInt(),
        true,
        UrlEntity(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        )
    )
  }

}