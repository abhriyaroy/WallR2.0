package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.database.DatabaseImageType
import zebrostudio.wallr100.domain.model.CollectionsImageModel

object DatabaseImageTypeMapper {

  fun mapToDatabaseImageType(model: CollectionsImageModel): DatabaseImageType {
    return when (model) {
      CollectionsImageModel.WALLPAPER -> DatabaseImageType.WALLPAPER
      CollectionsImageModel.SEARCH -> DatabaseImageType.SEARCH
      CollectionsImageModel.CRYSTALLIZED -> DatabaseImageType.CRYSTALLIZED
      CollectionsImageModel.EDITED -> DatabaseImageType.EDITED
      CollectionsImageModel.MINIMAL_COLOR -> DatabaseImageType.MINIMAL_COLOR
    }
  }
}