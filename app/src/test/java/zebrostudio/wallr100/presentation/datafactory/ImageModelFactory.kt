package zebrostudio.wallr100.presentation.datafactory

import zebrostudio.wallr100.domain.model.images.ImageAuthorModel
import zebrostudio.wallr100.domain.model.images.ImageLinkModel
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.domain.model.images.ImageResolutionModel
import zebrostudio.wallr100.domain.model.images.ImageSizeModel
import java.util.Random
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