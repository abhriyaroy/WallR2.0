package zebrostudio.wallr100.data.model.firebasedatabase

data class ImageResolutionEntity(
  val thumbSmallRes: String,
  val thumbRes: String,
  val mediumRes: String,
  val largeRes: String,
  val rawRes: String
) {
  constructor() : this("", "", "", "", "")
}