package zebrostudio.wallr100.data.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url
import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashPicturesEntity
import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashSearchEntity

interface UnsplashClient {

  @GET
  fun getPictures(@Url url: String): Single<UnsplashSearchEntity>
}