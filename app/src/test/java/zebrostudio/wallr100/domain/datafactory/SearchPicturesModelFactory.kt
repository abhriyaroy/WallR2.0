package zebrostudio.wallr100.domain.datafactory

import zebrostudio.wallr100.domain.model.SearchPicturesModel
import zebrostudio.wallr100.domain.model.ProfileImage
import zebrostudio.wallr100.domain.model.Urls
import zebrostudio.wallr100.domain.model.User
import java.util.Random
import java.util.UUID

object SearchPicturesModelFactory {

  fun getSearchPicturesModel(): SearchPicturesModel {
    return SearchPicturesModel(
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString(),
        Random().nextInt(),
        Random().nextInt(),
        UUID.randomUUID().toString(),
        User(UUID.randomUUID().toString(),
            ProfileImage(UUID.randomUUID().toString())),
        Random().nextInt(),
        true,
        Urls(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        )
    )
  }
}