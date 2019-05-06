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
      EXPLORE_TAG -> resourceUtils.getStringResource(R.string.explore_fragment_tag)
      TOP_PICKS_TAG -> resourceUtils.getStringResource(R.string.top_picks_fragment_tag)
      CATEGORIES_TAG -> resourceUtils.getStringResource(R.string.categories_fragment_tag)
      MINIMAL_TAG -> resourceUtils.getStringResource(R.string.minimal_fragment_tag)
      else -> resourceUtils.getStringResource(R.string.collection_fragment_tag)
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