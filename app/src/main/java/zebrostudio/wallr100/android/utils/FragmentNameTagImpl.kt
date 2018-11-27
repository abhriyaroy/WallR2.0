package zebrostudio.wallr100.android.utils

import android.support.annotation.StringRes

interface FragmentNameTag {
  fun getTag(id: Int): String
}

class FragmentNameTagImpl(private val resourceUtils: ResourceUtils) : FragmentNameTag {

  override fun getTag(@StringRes id: Int) = resourceUtils.getStringResource(id)
}