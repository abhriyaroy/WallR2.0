package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashPicturesEntity
import zebrostudio.wallr100.domain.model.SearchPicturesModel
import zebrostudio.wallr100.domain.model.Urls
import zebrostudio.wallr100.domain.model.User
import zebrostudio.wallr100.domain.model.ProfileImage

class PictureEntityMapper {

  fun mapFromEntity(unsplashPicturesEntity: List<UnsplashPicturesEntity>): List<SearchPicturesModel> {
    val list = mutableListOf<SearchPicturesModel>()
    for (it in unsplashPicturesEntity) {
      list.add(SearchPicturesModel(
          it.id,
          it.createdAt,
          it.imageWidth,
          it.imageHeight,
          it.paletteColor,
          User(it.user.name,
              ProfileImage(it.user.profileImage.mediumImageUrl)),
          it.likes,
          it.likedByUser,
          Urls(it.imageQualityUrls.rawImageLink,
              it.imageQualityUrls.largeImageLink,
              it.imageQualityUrls.regularImageLink,
              it.imageQualityUrls.smallImageLink,
              it.imageQualityUrls.thumbImageLink),
          it.categories))
    }
    return list
  }
}