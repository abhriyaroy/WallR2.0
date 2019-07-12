package zebrostudio.wallr100.dummylists.collectionimages

import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageModel

object MockCollectionsImageModelList {

  fun getList(): List<CollectionsImageModel> {
    return listOf(
        CollectionImagesModel1.getModel(),
        CollectionImagesModel2.getModel(),
        CollectionImagesModel3.getModel(),
        CollectionImagesModel4.getModel(),
        CollectionImagesModel5.getModel(),
        CollectionImagesModel6.getModel()
    )
  }
}