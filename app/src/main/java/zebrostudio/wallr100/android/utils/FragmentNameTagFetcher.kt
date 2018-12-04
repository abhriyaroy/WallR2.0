package zebrostudio.wallr100.android.utils

import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher.Companion.CATEGORIES_TAG
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher.Companion.EXPLORE_TAG
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher.Companion.MINIMAL_TAG
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher.Companion.TOP_PICKS_TAG

interface FragmentNameTagFetcher {
  fun getFragmentName(fragmentTag: String): String

  companion object {
    const val EXPLORE_TAG = "Explore"
    const val TOP_PICKS_TAG = "Top Picks"
    const val CATEGORIES_TAG = "Categories"
    const val MINIMAL_TAG = "Minimal"
    const val COLLECTIONS_TAG = "Collections"
  }
}

class FragmentNameTagFetcherImpl(private val resourceUtils: ResourceUtils) : FragmentNameTagFetcher {

  override fun getFragmentName(fragmentTag: String): String {
    return when (fragmentTag) {
      EXPLORE_TAG -> resourceUtils.getStringResource(R.string.explore_fragment_tag)
      TOP_PICKS_TAG -> resourceUtils.getStringResource(R.string.top_picks_fragment_tag)
      CATEGORIES_TAG -> resourceUtils.getStringResource(R.string.categories_fragment_tag)
      MINIMAL_TAG -> resourceUtils.getStringResource(R.string.minimal_fragment_tag)
      else -> resourceUtils.getStringResource(R.string.collection_fragment_tag)
    }
  }

}