package zebrostudio.wallr100.presentation.search.model

import java.io.Serializable

data class UrlPresenterEntity(
  val rawImageLink: String,
  val largeImageLink: String,
  val regularImageLink: String,
  val smallImageLink: String,
  val thumbImageLink: String
) : Serializable