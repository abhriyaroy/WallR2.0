package zebrostudio.wallr100.domain.datafactory

import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageModel
import java.util.Random
import java.util.UUID

object CollectionsImageModelFactory {
  fun getCollectionsImageModel(): CollectionsImageModel {
    return CollectionsImageModel(
        Random().nextLong(),
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString(),
        Random().nextInt()
    )
  }
}