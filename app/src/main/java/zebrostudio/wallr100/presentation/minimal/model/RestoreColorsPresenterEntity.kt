package zebrostudio.wallr100.presentation.minimal.model

data class RestoreColorsPresenterEntity(
  val colorsList: List<String>,
  val selectedItemsMap: HashMap<Int, String>
)