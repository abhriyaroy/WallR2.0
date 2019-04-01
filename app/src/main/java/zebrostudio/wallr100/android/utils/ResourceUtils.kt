package zebrostudio.wallr100.android.utils

import android.content.Context
import android.support.annotation.DimenRes
import android.support.annotation.StringRes

interface ResourceUtils {
  fun getStringResource(@StringRes id: Int): String
}

class ResourceUtilsImpl(private val context: Context) : ResourceUtils {

  override fun getStringResource(@StringRes id: Int) = context.getString(id)

}