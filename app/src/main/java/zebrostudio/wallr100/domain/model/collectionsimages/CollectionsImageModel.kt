package zebrostudio.wallr100.domain.model.collectionsimages

data class CollectionsImageModel(
  val uid: Long,
  val name: String,
  val path: String,
  val data: String,
  val type: Int
)