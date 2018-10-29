package zebrostudio.wallr100.data.model.firebasedatabase

data class FirebaseImageEntity(
  val imageLinks: ImageLinkEntity,
  val authorData: ImageAuthorEntity,
  val imageResolutions: ImageResolutionEntity,
  val imageSizes: ImageSizeEntity,
  val color: String,
  val timeStamp: Long,
  val referral: String
) {
  constructor() : this(
      ImageLinkEntity("", "", "", "", ""),
      ImageAuthorEntity("", ""),
      ImageResolutionEntity("", "", "", "", ""),
      ImageSizeEntity(0, 0, 0, 0, 0),
      "",
      0,
      "")
}