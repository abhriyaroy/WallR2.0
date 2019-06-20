package zebrostudio.wallr100.presentation.minimal.mapper

import zebrostudio.wallr100.domain.model.RestoreColorsModel
import zebrostudio.wallr100.presentation.minimal.model.RestoreColorsPresenterEntity
import java.util.*

class RestoreColorsPresenterEntityMapper {

  fun mapToPresenterEntity(restoreColorsModel: RestoreColorsModel): RestoreColorsPresenterEntity {
    return RestoreColorsPresenterEntity(
      restoreColorsModel.colorsList.toList(),
      restoreColorsModel.selectedItemsMap.clone() as TreeMap<Int, String>
    )
  }

}