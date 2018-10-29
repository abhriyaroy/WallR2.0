package zebrostudio.wallr100.data.model.firebasedatabase

data class ImageLinkEntity(
  val thumbSmall: String,
  val thumb: String,
  val medium: String,
  val large: String,
  val raw: String
) {
  constructor() : this("", "", "", "", "")
}