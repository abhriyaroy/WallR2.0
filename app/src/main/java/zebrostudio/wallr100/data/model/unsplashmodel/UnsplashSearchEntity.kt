package zebrostudio.wallr100.data.model.unsplashmodel

import com.google.gson.annotations.SerializedName

data class UnsplashSearchEntity(
        @SerializedName("total")
        val totalNumberOfImages: Long,
        @SerializedName("totalPages")
        val totalNumberOfPages: Long,
        @SerializedName("results")
        val results: List<UnsplashPicturesEntity>
)