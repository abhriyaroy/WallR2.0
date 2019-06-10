package zebrostudio.wallr100.presentation.datafactory

import zebrostudio.wallr100.presentation.wallpaper.model.ImageAuthorPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImageLinkPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImageResolutionPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImageSizePresenterEntity
import java.util.Random
import java.util.UUID

object ImagePresenterEntityFactory {

  fun getImagePresenterEntity() =
      ImagePresenterEntity(
          ImageLinkPresenterEntity(UUID.randomUUID().toString(),
              UUID.randomUUID().toString(),
              UUID.randomUUID().toString(),
              UUID.randomUUID().toString(),
              UUID.randomUUID().toString()),
          ImageAuthorPresenterEntity(UUID.randomUUID().toString(),
              UUID.randomUUID().toString()),
          ImageResolutionPresenterEntity(UUID.randomUUID().toString(),
              UUID.randomUUID().toString(),
              UUID.randomUUID().toString(),
              UUID.randomUUID().toString(),
              UUID.randomUUID().toString()),
          ImageSizePresenterEntity(Random().nextLong(),
              Random().nextLong(),
              Random().nextLong(),
              Random().nextLong(),
              Random().nextLong()),
          UUID.randomUUID().toString(),
          Random().nextLong(),
          UUID.randomUUID().toString())
}