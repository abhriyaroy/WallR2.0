package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.database.DatabaseImageType
import zebrostudio.wallr100.domain.model.CollectionsImageModel

interface DatabaseImageTypeMapper {
  fun mapToDatabaseImageType(model: CollectionsImageModel): DatabaseImageType
}

class DatabaseImageTypeMapperImpl : DatabaseImageTypeMapper {

  override fun mapToDatabaseImageType(model: CollectionsImageModel) = when (model) {
    CollectionsImageModel.WALLPAPER -> DatabaseImageType.WALLPAPER
    CollectionsImageModel.SEARCH -> DatabaseImageType.SEARCH
    CollectionsImageModel.CRYSTALLIZED -> DatabaseImageType.CRYSTALLIZED
    CollectionsImageModel.EDITED -> DatabaseImageType.EDITED
    CollectionsImageModel.MINIMAL_COLOR -> DatabaseImageType.MINIMAL_COLOR
  }
}