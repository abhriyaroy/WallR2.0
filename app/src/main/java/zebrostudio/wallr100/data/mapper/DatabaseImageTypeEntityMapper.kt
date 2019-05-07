package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.database.DatabaseImageType
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageTypeModel

interface DatabaseImageTypeEntityMapper {
  fun mapToDatabaseImageType(model: CollectionsImageTypeModel): DatabaseImageType
}

class DatabaseImageTypeEntityMapperImpl : DatabaseImageTypeEntityMapper {

  override fun mapToDatabaseImageType(model: CollectionsImageTypeModel) = when (model) {
    CollectionsImageTypeModel.WALLPAPER -> DatabaseImageType.WALLPAPER
    CollectionsImageTypeModel.SEARCH -> DatabaseImageType.SEARCH
    CollectionsImageTypeModel.CRYSTALLIZED -> DatabaseImageType.CRYSTALLIZED
    CollectionsImageTypeModel.EDITED -> DatabaseImageType.EDITED
    CollectionsImageTypeModel.MINIMAL_COLOR -> DatabaseImageType.MINIMAL_COLOR
  }
}