package zebrostudio.wallr100.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.Single
import zebrostudio.wallr100.R
import zebrostudio.wallr100.data.exception.UnableToGetMinimalColorsException
import java.util.TreeMap

interface MinimalColorHelper {
  fun getDefaultColors(): Single<List<String>>
  fun getCustomColors(): Single<List<String>>
  fun cacheDeletedItems(map: HashMap<Int, String>): Completable
  fun getDeletedItemsFromCache(): Single<TreeMap<Int, String>>
}

class MinimalColorsHelperImpl(
  private val context: Context,
  private val sharedPrefsHelper: SharedPrefsHelper
) : MinimalColorHelper {

  private var recentlyDeletedColorsMap = TreeMap<Int, String>()

  override fun getDefaultColors(): Single<List<String>> {
    return Single.just(context.resources.getStringArray(R.array.minimalColorsArray).toList())
  }

  override fun getCustomColors(): Single<List<String>> {
    return Single.create { singleEmitter ->
      sharedPrefsHelper.getString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG)
          .let { string ->
            if (string == "") {
              singleEmitter.onError(UnableToGetMinimalColorsException())
            } else {
              object : TypeToken<List<String>>() {}.type.let {
                singleEmitter.onSuccess(Gson().fromJson(string, it))
              }
            }
          }
    }
  }

  override fun cacheDeletedItems(map: HashMap<Int, String>): Completable {
    return Completable.create {
      recentlyDeletedColorsMap.clear()
      recentlyDeletedColorsMap.putAll(map)
      it.onComplete()
    }
  }

  override fun getDeletedItemsFromCache(): Single<TreeMap<Int, String>> {
    return Single.just(recentlyDeletedColorsMap)
  }

}