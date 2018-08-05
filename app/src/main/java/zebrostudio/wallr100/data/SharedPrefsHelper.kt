package zebrostudio.wallr100.data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPrefsHelper(context: Context) {

  private var sharedPrefs: SharedPreferences =
      context.getSharedPreferences("Preferences", Activity.MODE_PRIVATE)
  private var editorSharedPrefsHelper: SharedPreferences.Editor

  init {
    editorSharedPrefsHelper = sharedPrefs.edit()
  }

  fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
    return sharedPrefs.getBoolean(key, defaultValue)
  }

  fun setBoolean(key: String, value: Boolean): Boolean {
    return editorSharedPrefsHelper.putBoolean(key, value).commit()
  }

}