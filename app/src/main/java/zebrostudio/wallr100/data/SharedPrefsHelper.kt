package zebrostudio.wallr100.data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

interface SharedPrefsHelper {

  fun getBoolean(preferenceName: String, key: String, defaultValue: Boolean = false): Boolean
  fun setBoolean(preferenceName: String, key: String, value: Boolean = false): Boolean
}

class SharedPrefsHelperImpl(private var context: Context) : SharedPrefsHelper {

  override fun getBoolean(preferenceName: String, key: String, defaultValue: Boolean): Boolean {
    return getPreference(preferenceName).getBoolean(key, defaultValue)
  }

  override fun setBoolean(preferenceName: String, key: String, value: Boolean): Boolean {
    return getPreferenceEditor(preferenceName).putBoolean(key, value).commit()
  }

  private fun getPreference(preferenceName: String): SharedPreferences {
    return context.applicationContext.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE)
  }

  private fun getPreferenceEditor(preferenceName: String): SharedPreferences.Editor {
    return getPreference(preferenceName).edit()
  }

}