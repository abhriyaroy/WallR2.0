package zebrostudio.wallr100.data

import android.app.Activity
import android.content.Context

interface SharedPrefsHelper {

  fun getBoolean(preferenceName: String, key: String, defaultValue: Boolean = false): Boolean
  fun setBoolean(preferenceName: String, key: String, value: Boolean): Boolean
  fun getString(preferenceName: String, key: String, defaultValue: String = ""): String
  fun setString(preferenceName: String, key: String, value: String): Boolean
  fun getLong(preferenceName: String, key: String, value: Long = 3600): Long
  fun setLong(preferenceName: String, key: String, value: Long): Boolean
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

  override fun getString(
    preferenceName: String,
    key: String,
    defaultValue: String
  ) = getPreference(preferenceName).getString(key, defaultValue)?: ""

  override fun setString(
    preferenceName: String,
    key: String,
    value: String
  ) = getPreferenceEditor(preferenceName).putString(key, value).commit()

  override fun getLong(preferenceName: String, key: String, value: Long) = getPreference(
    preferenceName).getLong(key, value)

  override fun setLong(preferenceName: String, key: String, value: Long) = getPreferenceEditor(
    preferenceName).putLong(key, value).commit()

  private fun getPreference(preferenceName: String) =
      context.applicationContext.getSharedPreferences(
        preferenceName, Activity.MODE_PRIVATE)

  private fun getPreferenceEditor(preferenceName: String) = getPreference(preferenceName).edit()

}