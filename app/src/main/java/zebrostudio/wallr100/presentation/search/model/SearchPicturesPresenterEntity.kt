package zebrostudio.wallr100.presentation.search.model

import java.io.Serializable

data class SearchPicturesPresenterEntity(
  val id: String,
  val createdAt: String,
  val imageWidth: Int,
  val imageHeight: Int,
  val paletteColor: String,
  val userPresenterEntity: UserPresenterEntity,
  val likes: Int,
  val likedByUser: Boolean,
  val imageQualityUrlPresenterEntity: UrlPresenterEntity
) : Serializable