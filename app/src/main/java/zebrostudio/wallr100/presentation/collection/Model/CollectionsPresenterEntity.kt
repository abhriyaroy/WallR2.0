package zebrostudio.wallr100.presentation.collection.Model

data class CollectionsPresenterEntity(
  val uid: Long,
  val name: String,
  val path: String,
  val data: String,
  val type: Int
)