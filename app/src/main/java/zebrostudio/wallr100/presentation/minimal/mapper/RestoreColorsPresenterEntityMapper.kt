package zebrostudio.wallr100.presentation.minimal.mapper

import zebrostudio.wallr100.domain.model.RestoreColorsModel
import zebrostudio.wallr100.presentation.minimal.model.RestoreColorsPresenterEntity

class RestoreColorsPresenterEntityMapper {

  fun mapToPresnterEntity(restoreColorsModel: RestoreColorsModel): RestoreColorsPresenterEntity {
    return RestoreColorsPresenterEntity(
        restoreColorsModel.colorsList.toList(),
        restoreColorsModel.selectedItemsMap.clone() as HashMap<Int, String>
    )
  }

}