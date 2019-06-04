package zebrostudio.wallr100.android.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class EndlessScrollListener(layoutManager: androidx.recyclerview.widget.GridLayoutManager) : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

  private var visibleThreshold = 5
  private var currentPage = 0
  private var previousTotalItemCount = 0
  private val startingPageIndex = 0

  private var mLayoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager = layoutManager

  init {
    visibleThreshold *= layoutManager.spanCount
  }

  fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
    var maxSize = 0
    for (i in lastVisibleItemPositions.indices) {
      if (i == 0) {
        maxSize = lastVisibleItemPositions[i]
      } else if (lastVisibleItemPositions[i] > maxSize) {
        maxSize = lastVisibleItemPositions[i]
      }
    }
    return maxSize
  }

  override fun onScrolled(view: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
    var lastVisibleItemPosition = 0
    val totalItemCount = mLayoutManager.itemCount

    if (mLayoutManager is androidx.recyclerview.widget.StaggeredGridLayoutManager) {
      val lastVisibleItemPositions =
          (mLayoutManager as androidx.recyclerview.widget.StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
      // get maximum element within the list
      lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
    } else if (mLayoutManager is androidx.recyclerview.widget.LinearLayoutManager) {
      lastVisibleItemPosition =
          (mLayoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition()
    } else if (mLayoutManager is androidx.recyclerview.widget.GridLayoutManager) {
      lastVisibleItemPosition = (mLayoutManager as androidx.recyclerview.widget.GridLayoutManager).findLastVisibleItemPosition()
    }

    if (totalItemCount < previousTotalItemCount) {
      this.currentPage = this.startingPageIndex
      this.previousTotalItemCount = totalItemCount
      if (totalItemCount == 0) {
        loading = true
      }
    }

    if (loading && totalItemCount > previousTotalItemCount) {
      loading = false
      previousTotalItemCount = totalItemCount
    }

    if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
      currentPage++
      onLoadMore()
      loading = true
    }
  }

  abstract fun onLoadMore()

  companion object {
    var loading = true
  }

}