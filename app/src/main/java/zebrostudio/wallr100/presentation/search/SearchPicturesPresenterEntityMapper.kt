package zebrostudio.wallr100.presentation.search

import zebrostudio.wallr100.domain.model.SearchPicturesModel
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity

class SearchPicturesPresenterEntityMapper {

  fun mapTOPresenterEntity(searchPicturesModel: List<SearchPicturesModel>)
      : List<SearchPicturesPresenterEntity> {
    val list = mutableListOf<SearchPicturesPresenterEntity>()
    for (it in searchPicturesModel) {
      list.add(SearchPicturesPresenterEntity(
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