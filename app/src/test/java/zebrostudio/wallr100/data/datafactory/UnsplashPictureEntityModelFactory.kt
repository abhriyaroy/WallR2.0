package zebrostudio.wallr100.data.datafactory

import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashPicturesEntity
import zebrostudio.wallr100.presentation.search.model.ProfileImage
import zebrostudio.wallr100.presentation.search.model.Urls
import zebrostudio.wallr100.presentation.search.model.User
import java.util.Random

object UnsplashPictureEntityModelFactory {

  fun getUnsplashPictureEntityModel(): UnsplashPicturesEntity {
    return UnsplashPicturesEntity(
        java.util.UUID.randomUUID().toString(),
        java.util.UUID.randomUUID().toString(),
        Random().nextInt(),
        Random().nextInt(),
        java.util.UUID.randomUUID().toString(),
        User(java.util.UUID.randomUUID().toString(),
            ProfileImage(java.util.UUID.randomUUID().toString())),
        Random().nextInt(),
        true,
        Urls(
            java.util.UUID.randomUUID().toString(),
            java.util.UUID.randomUUID().toString(),
            java.util.UUID.randomUUID().toString(),
            java.util.UUID.randomUUID().toString(),
            java.util.UUID.randomUUID().toString()
        ),
        ArrayList(listOf(java.util.UUID.randomUUID().toString(),
            java.util.UUID.randomUUID().toString()))
    )
  }

}