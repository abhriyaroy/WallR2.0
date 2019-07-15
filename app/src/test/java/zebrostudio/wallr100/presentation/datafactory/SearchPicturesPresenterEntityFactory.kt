package zebrostudio.wallr100.presentation.datafactory

import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.search.model.UrlPresenterEntity
import zebrostudio.wallr100.presentation.search.model.UserPresenterEntity
import java.util.*
import java.util.UUID.randomUUID

object SearchPicturesPresenterEntityFactory {

  fun getSearchPicturesPresenterEntity() = SearchPicturesPresenterEntity(
    randomUUID().toString(),
    randomUUID().toString(),
    Random().nextInt(),
    Random().nextInt(),
    randomUUID().toString(),
    UserPresenterEntity(randomUUID().toString(),
      randomUUID().toString()),
    Random().nextInt(),
    true,
    UrlPresenterEntity(randomUUID().toString(),
      randomUUID().toString(),
      randomUUID().toString(),
      randomUUID().toString(),
      randomUUID().toString())
  )
}