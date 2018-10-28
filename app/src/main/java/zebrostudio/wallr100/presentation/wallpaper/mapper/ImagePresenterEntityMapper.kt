package zebrostudio.wallr100.presentation.wallpaper.mapper

import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.presentation.wallpaper.model.ImageAuthorPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImageLinkPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImageResolutionPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImageSizePresenterEntity

class ImagePresenterEntityMapper {

  fun mapToPresenterEntity(imageModelList: List<ImageModel>): List<ImagePresenterEntity> =
      imageModelList.map {
        ImagePresenterEntity(
            ImageLinkPresenterEntity(
                it.imageLink.small,
                it.imageLink.thumb,
                it.imageLink.medium,
                it.imageLink.large,
                it.imageLink.raw
            ),
            ImageAuthorPresenterEntity(
                it.imageAuthor.name,
                it.imageAuthor.profileImageLink
            ),
            ImageResolutionPresenterEntity(
                it.imageResolution.small,
                it.imageResolution.thumb,
                it.imageResolution.medium,
                it.imageResolution.large,
                it.imageResolution.raw
            ),
            ImageSizePresenterEntity(
                it.imageSize.small,
                it.imageSize.thumb,
                it.imageSize.medium,
                it.imageSize.large,
                it.imageSize.raw
            ),
            it.color,
            it.timeStamp,
            it.referral)
      }.toList()
}