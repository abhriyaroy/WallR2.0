package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashPicturesEntity
import zebrostudio.wallr100.domain.model.SearchPicturesModel
import zebrostudio.wallr100.domain.model.UrlModel
import zebrostudio.wallr100.domain.model.UserModel

class PictureEntityMapper {

  fun mapFromEntity(unsplashPicturesEntity: List<UnsplashPicturesEntity>) =
      unsplashPicturesEntity.map {
        SearchPicturesModel(
            it.id,
            it.createdAt,
            it.imageWidth,
            it.imageHeight,
            it.paletteColor,
            UserModel(it.userEntity.name,
                it.userEntity.profileImage.mediumImageUrl),
            it.likes,
            it.likedByUser,
            UrlModel(it.imageQualityUrlEntity.rawImageLink,
                it.imageQualityUrlEntity.largeImageLink,
                it.imageQualityUrlEntity.regularImageLink,
                it.imageQualityUrlEntity.smallImageLink,
                it.imageQualityUrlEntity.thumbImageLink))
      }.toList()
}
