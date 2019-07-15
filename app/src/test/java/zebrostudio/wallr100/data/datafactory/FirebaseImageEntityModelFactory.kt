package zebrostudio.wallr100.data.datafactory

import zebrostudio.wallr100.data.model.firebasedatabase.*
import java.util.*
import java.util.UUID.randomUUID

object FirebaseImageEntityModelFactory {

  fun getFirebaseImageEntity(): FirebaseImageEntity {
    return FirebaseImageEntity(
      ImageLinkEntity(randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString()),
      ImageAuthorEntity(randomUUID().toString(),
        randomUUID().toString()),
      ImageResolutionEntity(randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString()),
      ImageSizeEntity(Random().nextLong(),
        Random().nextLong(),
        Random().nextLong(),
        Random().nextLong(),
        Random().nextLong()),
      randomUUID().toString(),
      Random().nextLong(),
      randomUUID().toString()
    )
  }
}