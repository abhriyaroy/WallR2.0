package zebrostudio.wallr100.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import zebrostudio.wallr100.R
import zebrostudio.wallr100.data.exception.UnableToGetDefaultSolidColorsException

interface SolidColorHelper {
  fun getDefaultColors(): Single<List<String>>
  fun getCustomColors(): Single<List<String>>
}

class SolidColorHelperImpl(
  private val context: Context,
  private val sharedPrefsHelper: SharedPrefsHelper
) : SolidColorHelper {

  override fun getDefaultColors(): Single<List<String>> {
    return Single.create { singleEmitter ->
      sharedPrefsHelper.getString(IMAGE_PREFERENCE_NAME, CUSTOM_SOLID_COLOR_LIST_TAG)
          .let { string ->
            if (string == "") {
              singleEmitter.onError(UnableToGetDefaultSolidColorsException())
            } else {
              object : TypeToken<List<String>>() {}.type.let {
                singleEmitter.onSuccess(Gson().fromJson(string, it))
              }
            }
          }
    }
  }

  override fun getCustomColors(): Single<List<String>> {
    return Single.just(context.resources.getStringArray(R.array.solidColorsArray).toList())
  }

}