package zebrostudio.wallr100.domain.model

import java.util.*

data class RestoreColorsModel(
  val colorsList: List<String>,
  val selectedItemsMap: TreeMap<Int, String>
)