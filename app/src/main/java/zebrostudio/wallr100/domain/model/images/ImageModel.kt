package zebrostudio.wallr100.domain.model.images

data class ImageModel(
  val imageLink: ImageLinkModel,
  val imageAuthor: ImageAuthorModel,
  val imageResolution: ImageResolutionModel,
  val imageSize: ImageSizeModel,
  val color: String,
  val timeStamp: Long,
  val referral: String
)