package zebrostudio.wallr100.domain.datafactory

import zebrostudio.wallr100.domain.model.images.*
import java.util.*
import java.util.UUID.randomUUID

object ImageModelFactory {

  fun getImageModel(): ImageModel {
    return ImageModel(
      ImageLinkModel(randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString()),
      ImageAuthorModel(randomUUID().toString(),
        randomUUID().toString()),
      ImageResolutionModel(randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString(),
        randomUUID().toString()),
      ImageSizeModel(Random().nextLong(),
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