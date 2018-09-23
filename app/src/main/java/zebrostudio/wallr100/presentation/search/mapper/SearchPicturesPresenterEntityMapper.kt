package zebrostudio.wallr100.presentation.search.mapper

import zebrostudio.wallr100.domain.model.SearchPicturesModel
import zebrostudio.wallr100.presentation.search.model.ProfileImage
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.search.model.Urls
import zebrostudio.wallr100.presentation.search.model.User

class SearchPicturesPresenterEntityMapper {

  fun mapToPresenterEntity(searchPicturesModel: List<SearchPicturesModel>)
      : List<SearchPicturesPresenterEntity> {
    val list = mutableListOf<SearchPicturesPresenterEntity>()
    for (it in searchPicturesModel) {
      list.add(SearchPicturesPresenterEntity(
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
              it.imageQualityUrls.thumbImageLink))
      )
    }
    return list
  }

}