package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashPicturesEntity
import zebrostudio.wallr100.domain.model.PicturesModel

class PictureEntityMapper {

  fun mapFromEntity(unsplashPicturesEntity: UnsplashPicturesEntity): PicturesModel {
    return PicturesModel(
        unsplashPicturesEntity.id,
        unsplashPicturesEntity.createdAt,
        unsplashPicturesEntity.imageWidth,
        unsplashPicturesEntity.imageHeight,
        unsplashPicturesEntity.paletteColor,
        unsplashPicturesEntity.user,
        unsplashPicturesEntity.likes,
        unsplashPicturesEntity.likedByUser,
        unsplashPicturesEntity.imageQualityUrls,
        unsplashPicturesEntity.categories)
  }
}