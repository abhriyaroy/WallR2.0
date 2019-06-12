package zebrostudio.wallr100.android.utils

import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentTag.CATEGORIES_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.EXPLORE_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.MINIMAL_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.TOP_PICKS_TAG

interface FragmentNameTagFetcher {
  fun getFragmentName(fragmentTag: FragmentTag): String
}

class FragmentNameTagFetcherImpl(private val resourceUtils: ResourceUtils) : FragmentNameTagFetcher {

  override fun getFragmentName(fragmentTag: FragmentTag): String {
    return when (fragmentTag) {
      EXPLORE_TAG -> resourceUtils.getStringResource(R.string.explore_title)
      TOP_PICKS_TAG -> resourceUtils.getStringResource(R.string.top_picks_title)
      CATEGORIES_TAG -> resourceUtils.getStringResource(R.string.categories_title)
      MINIMAL_TAG -> resourceUtils.getStringResource(R.string.minimal_title)
      else -> resourceUtils.getStringResource(R.string.collection_title)
    }
  }
}

enum class FragmentTag {
  EXPLORE_TAG,
  TOP_PICKS_TAG,
  CATEGORIES_TAG,
  MINIMAL_TAG,
  COLLECTIONS_TAG
}