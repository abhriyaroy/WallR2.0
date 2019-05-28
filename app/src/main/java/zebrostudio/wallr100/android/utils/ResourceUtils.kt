package zebrostudio.wallr100.android.utils

import android.content.Context
import android.support.annotation.StringRes

interface ResourceUtils {
  fun getStringResource(@StringRes id: Int): String
  fun getStringResource(@StringRes id: Int, value: String): String
}

class ResourceUtilsImpl(private val context: Context) : ResourceUtils {

  override fun getStringResource(@StringRes id: Int) = context.stringRes(id)
  override fun getStringResource(@StringRes id: Int, value: String) = context.stringRes(id, value)

}