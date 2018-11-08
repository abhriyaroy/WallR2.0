package zebrostudio.wallr100.data.datafactory

import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity
import zebrostudio.wallr100.data.model.firebasedatabase.ImageAuthorEntity
import zebrostudio.wallr100.data.model.firebasedatabase.ImageLinkEntity
import zebrostudio.wallr100.data.model.firebasedatabase.ImageResolutionEntity
import zebrostudio.wallr100.data.model.firebasedatabase.ImageSizeEntity
import java.util.Random
import java.util.UUID.*

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