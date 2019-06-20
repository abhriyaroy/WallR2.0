package zebrostudio.wallr100.presentation.detail.images.mapper

import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel
import zebrostudio.wallr100.presentation.detail.images.model.ImageDownloadPresenterEntity

class ImageDownloadPresenterEntityMapper {

  fun mapToPresenterEntity(imageDownloadModel: ImageDownloadModel): ImageDownloadPresenterEntity {
    return ImageDownloadPresenterEntity(
      imageDownloadModel.progress,
      imageDownloadModel.imageBitmap)
  }
}