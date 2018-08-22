package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashPicturesEntity
import zebrostudio.wallr100.domain.model.SearchPicturesModel

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
          it.user,
          it.likes,
          it.likedByUser,
          it.imageQualityUrls,
          it.categories))
    }
    return list
  }
}