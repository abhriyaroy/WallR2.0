package zebrostudio.wallr100.data.datafactory

import zebrostudio.wallr100.data.datafactory.UnsplashPictureEntityModelFactory.getUnsplashPictureEntityModel
import zebrostudio.wallr100.data.model.unsplashmodel.*
import java.util.*

object UnsplashSearchPicturesEntityModelFactory {

    fun getUnsplashSearchPictureEntityModel(): UnsplashSearchEntity {
        return UnsplashSearchEntity(
            3,
            3,
            listOf(
                getUnsplashPictureEntityModel(),
                getUnsplashPictureEntityModel(),
                getUnsplashPictureEntityModel()
            )
        )
    }

    fun getEmptyUnsplashSearchPictureEntityModel(): UnsplashSearchEntity {
        return UnsplashSearchEntity(
            0,
            0,
            emptyList()
        )
    }

}