package zebrostudio.wallr100.domain.model.searchpictures

data class UrlModel(
  val rawImageLink: String,
  val largeImageLink: String,
  val regularImageLink: String,
  val smallImageLink: String,
  val thumbImageLink: String
)