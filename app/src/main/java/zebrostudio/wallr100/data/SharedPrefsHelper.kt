package zebrostudio.wallr100.data

import android.app.Activity
import android.content.Context

interface SharedPrefsHelper {

  fun getBoolean(preferenceName: String, key: String, defaultValue: Boolean = false): Boolean
  fun setBoolean(preferenceName: String, key: String, value: Boolean = false): Boolean
}

class SharedPrefsHelperImpl(private val context: Context) : SharedPrefsHelper {

  override fun getBoolean(
    preferenceName: String,
    key: String,
    defaultValue: Boolean
  ) = getPreference(preferenceName).getBoolean(key, defaultValue)

  override fun setBoolean(
    preferenceName: String,
    key: String,
    value: Boolean
  ) = getPreferenceEditor(preferenceName).putBoolean(key, value).commit()

  private fun getPreference(preferenceName: String) = context.applicationContext.getSharedPreferences(
      preferenceName, Activity.MODE_PRIVATE)

  private fun getPreferenceEditor(preferenceName: String) = getPreference(preferenceName).edit()

}