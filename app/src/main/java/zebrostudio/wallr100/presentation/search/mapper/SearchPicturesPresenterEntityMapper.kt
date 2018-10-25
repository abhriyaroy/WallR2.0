package zebrostudio.wallr100.presentation.search.mapper

import zebrostudio.wallr100.domain.model.searchpictures.SearchPicturesModel
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.search.model.UrlPresenterEntity
import zebrostudio.wallr100.presentation.search.model.UserPresenterEntity

class SearchPicturesPresenterEntityMapper {

  fun mapToPresenterEntity(searchPicturesModel: List<SearchPicturesModel>) =
      searchPicturesModel.map {
        SearchPicturesPresenterEntity(
            it.id,
            it.createdAt,
            it.imageWidth,
            it.imageHeight,
            it.paletteColor,
            UserPresenterEntity(it.userModel.name,
                it.userModel.profileImageLink),
            it.likes,
            it.likedByUser,
            UrlPresenterEntity(it.imageQualityUrlModel.rawImageLink,
                it.imageQualityUrlModel.largeImageLink,
                it.imageQualityUrlModel.regularImageLink,
                it.imageQualityUrlModel.smallImageLink,
                it.imageQualityUrlModel.thumbImageLink)
        )
      }.toList()

}