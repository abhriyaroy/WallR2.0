package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.database.entity.CollectionDatabaseImageEntity
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageModel

interface CollectionsDatabaseImageEntityMapper {
  fun mapFromEntity(
    collectionDatabaseImageEntityList: List<CollectionDatabaseImageEntity>
  ): List<CollectionsImageModel>

  fun mapToEntity(
    collectionsImageModelList: List<CollectionsImageModel>
  ): List<CollectionDatabaseImageEntity>
}

class CollectionsDatabaseImageEntityMapperImpl : CollectionsDatabaseImageEntityMapper {

  override fun mapFromEntity(
    collectionDatabaseImageEntityList: List<CollectionDatabaseImageEntity>
  ): List<CollectionsImageModel> {
    return collectionDatabaseImageEntityList.map {
      CollectionsImageModel(it.uid, it.name, it.path, it.data, it.type)
    }.toList()
  }

  override fun mapToEntity(
    collectionsImageModelList: List<CollectionsImageModel>
  ): List<CollectionDatabaseImageEntity> {
    return collectionsImageModelList.map {
      CollectionDatabaseImageEntity(it.uid, it.name, it.path, it.data, it.type)
    }.toList()
  }

}