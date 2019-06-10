package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.database.DatabaseImageType
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType

interface DatabaseImageTypeMapper {
  fun mapToDatabaseImageType(imageType: CollectionsImageType): DatabaseImageType
}

class DatabaseImageTypeMapperImpl : DatabaseImageTypeMapper {

  override fun mapToDatabaseImageType(imageType: CollectionsImageType) = when (imageType) {
    CollectionsImageType.WALLPAPER -> DatabaseImageType.WALLPAPER
    CollectionsImageType.SEARCH -> DatabaseImageType.SEARCH
    CollectionsImageType.CRYSTALLIZED -> DatabaseImageType.CRYSTALLIZED
    CollectionsImageType.EDITED -> DatabaseImageType.EDITED
    CollectionsImageType.MINIMAL_COLOR -> DatabaseImageType.MINIMAL_COLOR
    CollectionsImageType.EXTERNAL -> DatabaseImageType.EXTERNAL
  }
}