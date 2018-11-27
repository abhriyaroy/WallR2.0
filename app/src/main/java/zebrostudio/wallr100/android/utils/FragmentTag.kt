package zebrostudio.wallr100.android.utils

import android.support.annotation.StringRes

class FragmentTag(private val resourceUtils: ResourceUtils) {

  fun getTag(@StringRes id: Int) = resourceUtils.getStringResource(id)
}