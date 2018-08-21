package zebrostudio.wallr100.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import zebrostudio.wallr100.data.api.UrlMap.Companion.UNSPLASH_BASE_URL

class UnsplashClientFactory {

  private var retrofit: Retrofit? = null
  private var okHttpClient: OkHttpClient? = null

  fun getRemote(): UnsplashClient {
    if (okHttpClient == null) {
      OkHttpClient().newBuilder().addInterceptor(Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization",
                "Client-ID 0ff70d0b9b2ed4f5799f59f282407253b8c86084da7876845fea2c2f4d9b90de")
            .build()
        chain.proceed(request)
      })
    }

    if (retrofit == null) {
      retrofit = Retrofit.Builder()
          .baseUrl(UNSPLASH_BASE_URL)
          .client(okHttpClient)
          .addConverterFactory(GsonConverterFactory.create())
          .build()
    }
    return retrofit!!.create(UnsplashClient::class.java)

  }
}