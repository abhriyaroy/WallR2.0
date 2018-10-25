package zebrostudio.wallr100.presentation.datafactory

import zebrostudio.wallr100.domain.model.searchpictures.SearchPicturesModel
import zebrostudio.wallr100.domain.model.searchpictures.UrlModel
import zebrostudio.wallr100.domain.model.searchpictures.UserModel
import java.util.Random
import java.util.UUID.*

object SearchPicturesModelFactory {

  fun getSearchPicturesModelList(): List<SearchPicturesModel> {
    var searchPicturesModel = SearchPicturesModel(
        randomUUID().toString(),
        randomUUID().toString(),
        Random().nextInt(),
        Random().nextInt(),
        randomUUID().toString(),
        UserModel(randomUUID().toString(),
            randomUUID().toString()),
        Random().nextInt(),
        true,
        UrlModel(randomUUID().toString(),
            randomUUID().toString(),
            randomUUID().toString(),
            randomUUID().toString(),
            randomUUID().toString())
    )
    return listOf(searchPicturesModel)
  }
}