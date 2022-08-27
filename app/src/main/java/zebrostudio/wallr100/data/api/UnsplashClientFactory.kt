package zebrostudio.wallr100.data.api

import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import zebrostudio.wallr100.data.api.UrlMap.UNSPLASH_BASE_URL
import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashPicturesEntity
import zebrostudio.wallr100.data.model.unsplashmodel.UnsplashSearchEntity

interface UnsplashClientFactory {
  fun getPicturesService(url: String): Single<UnsplashSearchEntity>
}

class UnsplashClientFactoryImpl : UnsplashClientFactory {

  private val headerName = "Authorization"
  private val headerValue =
      "Client-ID API_KEY"
//      "Client-ID $API_KEY"
  private var retrofit: Retrofit? = null
  private var okHttpClient: OkHttpClient? = null

  override fun getPicturesService(url: String): Single<UnsplashSearchEntity> {
    if (okHttpClient == null) {
      val builder = OkHttpClient().newBuilder()
      builder.addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader(headerName, headerValue)
            .build()
        chain.proceed(request)
      }
      val interceptor = HttpLoggingInterceptor()
      interceptor.level = HttpLoggingInterceptor.Level.BODY
      builder.addInterceptor(interceptor).build()

      okHttpClient = builder.build()
    }

    if (retrofit == null) {
      retrofit = Retrofit.Builder()
          .baseUrl(UNSPLASH_BASE_URL)
          .client(okHttpClient!!)
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .build()
    }
    return retrofit!!.create(UnsplashClient::class.java).getPictures(url)

  }
}