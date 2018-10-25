package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.model.firebasedatabase.FirebasePicturesEntity
import zebrostudio.wallr100.domain.model.images.AuthorModel
import zebrostudio.wallr100.domain.model.images.ImageLinkModel
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.domain.model.images.ImageResolutionModel
import zebrostudio.wallr100.domain.model.images.ImageSizeModel

class FirebasePictureEntityMapper {

  fun mapFromEntity(firebasePicturesEntity: List<FirebasePicturesEntity>) =
      firebasePicturesEntity.map {
        ImageModel(
            ImageLinkModel(it.imageLink.small,
                it.imageLink.thumb,
                it.imageLink.medium,
                it.imageLink.large,
                it.imageLink.raw),
            AuthorModel(it.author.name,
                it.author.profileImageLink),
            ImageResolutionModel(it.imageResolution.small,
                it.imageResolution.thumb,
                it.imageResolution.medium,
                it.imageResolution.large,
                it.imageResolution.raw),
            ImageSizeModel(it.imageSize.small,
                it.imageSize.thumb,
                it.imageSize.medium,
                it.imageSize.large,
                it.imageSize.raw),
            it.color,
            it.timeStamp,
            it.referral)
      }.toList()
}