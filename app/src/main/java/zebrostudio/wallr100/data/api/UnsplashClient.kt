package zebrostudio.wallr100.data.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Url
import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashPicturesEntity

interface UnsplashClient{

  @GET
  fun getPictures(@Url url: String): Observable<UnsplashPicturesEntity>
}