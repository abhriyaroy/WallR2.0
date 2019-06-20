package zebrostudio.wallr100.presentation.minimal.model

import java.util.*

data class RestoreColorsPresenterEntity(
  val colorsList: List<String>,
  val selectedItemsMap: TreeMap<Int, String>
)