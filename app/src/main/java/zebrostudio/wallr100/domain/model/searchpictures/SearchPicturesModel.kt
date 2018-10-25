package zebrostudio.wallr100.domain.model.searchpictures

data class SearchPicturesModel(
  val id: String,
  val createdAt: String,
  val imageWidth: Int,
  val imageHeight: Int,
  val paletteColor: String,
  val userModel: UserModel,
  val likes: Int,
  val likedByUser: Boolean,
  val imageQualityUrlModel: UrlModel
)