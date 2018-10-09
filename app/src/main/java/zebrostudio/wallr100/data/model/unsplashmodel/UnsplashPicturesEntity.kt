package zebrostudio.wallr100.data.model.unsplashmodel

import com.google.gson.annotations.SerializedName

class UnsplashPicturesEntity(
  @SerializedName("id") val id: String,
  @SerializedName("created_at") val createdAt: String,
  @SerializedName("width") val imageWidth: Int,
  @SerializedName("height") val imageHeight: Int,
  @SerializedName("color") val paletteColor: String,
  @SerializedName("user") val userEntity: UserEntity,
  @SerializedName("likes") val likes: Int,
  @SerializedName("liked_by_user") val likedByUser: Boolean,
  @SerializedName("urls") val imageQualityUrlEntity: UrlEntity
)