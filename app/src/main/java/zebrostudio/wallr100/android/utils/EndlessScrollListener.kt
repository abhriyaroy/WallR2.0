package zebrostudio.wallr100.android.utils

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

abstract class EndlessScrollListener(layoutManager: GridLayoutManager) : RecyclerView.OnScrollListener() {

  private var visibleThreshold = 5
  private var currentPage = 0
  private var previousTotalItemCount = 0
  private var loading = true
  private val startingPageIndex = 0

  private var mLayoutManager: RecyclerView.LayoutManager = layoutManager

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

  override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
    var lastVisibleItemPosition = 0
    val totalItemCount = mLayoutManager.itemCount

    if (mLayoutManager is StaggeredGridLayoutManager) {
      val lastVisibleItemPositions =
          (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
      // get maximum element within the list
      lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
    } else if (mLayoutManager is LinearLayoutManager) {
      lastVisibleItemPosition =
          (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
    } else if (mLayoutManager is GridLayoutManager) {
      lastVisibleItemPosition = (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
    }

    if (totalItemCount < previousTotalItemCount) {
      this.currentPage = this.startingPageIndex
      this.previousTotalItemCount = totalItemCount
      if (totalItemCount == 0) {
        this.loading = true
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

}