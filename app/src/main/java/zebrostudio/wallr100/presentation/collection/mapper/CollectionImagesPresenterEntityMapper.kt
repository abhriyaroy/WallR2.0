package zebrostudio.wallr100.presentation.collection.mapper

import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageModel
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity

interface CollectionImagesPresenterEntityMapper {
  fun mapToPresenterEntity(modelList: List<CollectionsImageModel>): List<CollectionsPresenterEntity>
  fun mapFromPresenterEntity(presenterEntityList: List<CollectionsPresenterEntity>): List<CollectionsImageModel>
}

class CollectionsImagesPresenterEntityMapperImpl : CollectionImagesPresenterEntityMapper {
  override fun mapToPresenterEntity(modelList: List<CollectionsImageModel>): List<CollectionsPresenterEntity> {
    return modelList.map {
      CollectionsPresenterEntity(
          it.uid,
          it.name,
          it.path,
          it.data,
          it.type
      )
    }.toList()
  }

  override fun mapFromPresenterEntity(presenterEntityList: List<CollectionsPresenterEntity>): List<CollectionsImageModel> {
    return presenterEntityList.map {
      CollectionsImageModel(
          it.uid,
          it.name,
          it.path,
          it.data,
          it.type
      )
    }.toList()
  }
}