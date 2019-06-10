package zebrostudio.wallr100.domain.model

import java.util.TreeMap

data class RestoreColorsModel(
  val colorsList: List<String>,
  val selectedItemsMap: TreeMap<Int, String>
)