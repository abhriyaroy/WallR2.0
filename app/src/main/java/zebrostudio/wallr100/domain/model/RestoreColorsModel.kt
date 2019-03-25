package zebrostudio.wallr100.domain.model

data class RestoreColorsModel(
  val colorsList: List<String>,
  val selectedItemsMap: HashMap<Int, String>
)