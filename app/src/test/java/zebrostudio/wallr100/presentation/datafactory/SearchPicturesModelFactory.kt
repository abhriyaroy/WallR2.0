package zebrostudio.wallr100.presentation.datafactory

import zebrostudio.wallr100.domain.model.ProfileImage
import zebrostudio.wallr100.domain.model.SearchPicturesModel
import zebrostudio.wallr100.domain.model.Urls
import zebrostudio.wallr100.domain.model.User
import java.util.Random
import java.util.UUID
import java.util.UUID.*

object SearchPicturesModelFactory {

  fun getSearchPicturesModelList(): List<SearchPicturesModel> {
    var searchPicturesModel = SearchPicturesModel(randomUUID().toString(),
        randomUUID().toString(),
        Random().nextInt(),
        Random().nextInt(),
        randomUUID().toString(),
        User(randomUUID().toString(),
            ProfileImage(randomUUID().toString())),
        Random().nextInt(),
        true,
        Urls(randomUUID().toString(),
            randomUUID().toString(),
            randomUUID().toString(),
            randomUUID().toString(),
            randomUUID().toString())
    )
    return listOf(searchPicturesModel)
  }
}