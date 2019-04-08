package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity
import zebrostudio.wallr100.domain.model.images.ImageAuthorModel
import zebrostudio.wallr100.domain.model.images.ImageLinkModel
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.domain.model.images.ImageResolutionModel
import zebrostudio.wallr100.domain.model.images.ImageSizeModel

interface FirebasePictureEntityMapper {
  fun mapFromEntity(firebaseImageEntity: List<FirebaseImageEntity>): List<ImageModel>
}

class FirebasePictureEntityMapperImpl : FirebasePictureEntityMapper {

  override fun mapFromEntity(firebaseImageEntity: List<FirebaseImageEntity>) =
      firebaseImageEntity.map {
        ImageModel(
            ImageLinkModel(it.imageLinks.thumbSmall,
                it.imageLinks.thumb,
                it.imageLinks.medium,
                it.imageLinks.large,
                it.imageLinks.raw),
            ImageAuthorModel(it.authorData.name,
                it.authorData.profileImageUrl),
            ImageResolutionModel(it.imageResolutions.thumbSmallRes,
                it.imageResolutions.thumbRes,
                it.imageResolutions.mediumRes,
                it.imageResolutions.largeRes,
                it.imageResolutions.rawRes),
            ImageSizeModel(it.imageSizes.thumbSmallSize,
                it.imageSizes.thumbSize,
                it.imageSizes.mediumSize,
                it.imageSizes.largeSize,
                it.imageSizes.rawSize),
            it.color,
            it.timeStamp,
            it.referral)
      }.toList()
}