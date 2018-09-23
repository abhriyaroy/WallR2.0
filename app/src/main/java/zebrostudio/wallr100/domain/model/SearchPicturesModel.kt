package zebrostudio.wallr100.domain.model

import java.util.ArrayList

class SearchPicturesModel(
  val id: String,
  val createdAt: String,
  val imageWidth: Int,
  val imageHeight: Int,
  val paletteColor: String,
  val user: User,
  val likes: Int,
  val likedByUser: Boolean,
  val imageQualityUrls: Urls
)