package zebrostudio.wallr100.dummylists.firebaseimage

import zebrostudio.wallr100.domain.model.images.ImageModel

object MockFirebaseImageList {

  fun getList() : List<ImageModel>{
    return listOf(
      getFirstImage()
    )
  }

  private fun getFirstImage() : ImageModel{
    return ImageModel()
  }
}