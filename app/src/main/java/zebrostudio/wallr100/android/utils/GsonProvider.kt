package zebrostudio.wallr100.android.utils

import com.google.gson.Gson

interface GsonProvider {
  fun getGson(): Gson
}

class GsonProviderImpl : GsonProvider {

  private var gson: Gson? = null

  override fun getGson(): Gson {
    if (gson == null) {
      gson = Gson()
    }
    return gson!!
  }

}

