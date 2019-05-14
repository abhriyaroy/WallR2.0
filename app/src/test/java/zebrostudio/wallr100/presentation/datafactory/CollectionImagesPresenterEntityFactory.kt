package zebrostudio.wallr100.presentation.datafactory

import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity
import java.util.Random
import java.util.UUID

object CollectionImagesPresenterEntityFactory {
  fun getCollectionImagesPresenterEntityFactory() =
      CollectionsPresenterEntity(
          Random().nextLong(),
          UUID.randomUUID().toString(),
          UUID.randomUUID().toString(),
          UUID.randomUUID().toString(),
          Random().nextInt()
      )
}