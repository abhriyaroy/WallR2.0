package zebrostudio.wallr100.android.ui.search

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.view.View
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import dagger.android.AndroidInjection
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.android.synthetic.main.activity_search.SearchActivitySpinkitView
import kotlinx.android.synthetic.main.activity_search.bottomSpinkitView
import kotlinx.android.synthetic.main.activity_search.infoImageView
import kotlinx.android.synthetic.main.activity_search.infoTextFirstLine
import kotlinx.android.synthetic.main.activity_search.infoTextSecondLine
import kotlinx.android.synthetic.main.activity_search.recyclerView
import kotlinx.android.synthetic.main.activity_search.retryButton
import kotlinx.android.synthetic.main.activity_search.searchAppBar
import kotlinx.android.synthetic.main.activity_search.searchView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.adapters.ImageRecyclerviewAdapter
import zebrostudio.wallr100.android.utils.EndlessScrollListener
import zebrostudio.wallr100.android.utils.GridItemDecorator
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.withDelayOnMain
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerviewPresenterImpl.ImageListType.*
import zebrostudio.wallr100.presentation.search.SearchContract
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchActivity : AppCompatActivity(), SearchContract.SearchView {

  @Inject
  internal lateinit var presenter: SearchContract.SearchPresenter
  @Inject
  internal lateinit var imageRecyclerviewPresenter: ImageRecyclerItemContract.ImageRecyclerviewPresenter

  private var appBarCollapsed = false
  private var recyclerviewAdapter: ImageRecyclerviewAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_search)
    presenter.attachView(this)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      overridePendingTransition(R.anim.slide_in_up, 0)
    }
    initAppbar()
    showNoInputView()
    initRecyclerView()
    initRetryClickListener()
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
      val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      if (matches != null && matches.size > 0) {
        val searchWrd = matches[0]
        if (!TextUtils.isEmpty(searchWrd)) {
          searchView.setQuery(searchWrd, false)
        }
      }
    }
  }

  override fun onBackPressed() {
    if (appBarCollapsed) {
      searchAppBar.setExpanded(true, true)
      withDelayOnMain(300, block = {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
          finish()
          overridePendingTransition(0, R.anim.slide_out_down)
        } else {
          appBarCollapsed = false
          val params = searchView?.layoutParams as AppBarLayout.LayoutParams
          params.scrollFlags = 0
          onBackPressed()
        }
      })
    } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      overridePendingTransition(0, R.anim.slide_out_down)
      finish()
    } else super.onBackPressed()
  }

  override fun showLoader() {
    hideAllLoadersAndMessageViews()
    SearchActivitySpinkitView.visibility = View.VISIBLE
  }

  override fun hideLoader() {
    SearchActivitySpinkitView.visibility = View.GONE
  }

  override fun showBottomLoader() {
    bottomSpinkitView.visibility = View.VISIBLE
  }

  override fun hideBottomLoader() {
    bottomSpinkitView.visibility = View.GONE
  }

  override fun showNoInputView() {
    hideAllLoadersAndMessageViews()
    infoImageView.setImageResource(R.drawable.ic_no_input_gray)
    infoImageView.visibility = View.VISIBLE
    infoTextFirstLine.text = getText(R.string.search_type_in_a_query_message)
    infoTextFirstLine.visibility = View.VISIBLE
  }

  override fun showNoResultView(query: String?) {
    hideAllLoadersAndMessageViews()
    infoImageView.setImageResource(R.drawable.ic_no_result_gray)
    infoImageView.visibility = View.VISIBLE
    val noResultText = if (query == null) {
      "${getText(R.string.search_no_result_message_null_query)}"
    } else {
      "${getText(R.string.search_no_result_message)} '$query'"
    }
    infoTextFirstLine.text = noResultText
    infoTextFirstLine.visibility = View.VISIBLE
  }

  override fun hideAllLoadersAndMessageViews() {
    infoTextFirstLine.visibility = View.GONE
    infoTextSecondLine.visibility = View.GONE
    infoImageView.visibility = View.GONE
    retryButton.visibility = View.GONE
    imageRecyclerviewPresenter.clearAll()
    recyclerviewAdapter?.notifyDataSetChanged()
    hideLoader()
    hideBottomLoader()
  }

  override fun showNoInternetView() {
    hideAllLoadersAndMessageViews()
    infoImageView.setImageResource(R.drawable.ic_no_result_gray)
    infoImageView.visibility = View.VISIBLE
    infoTextFirstLine.text = getText(R.string.search_unable_to_search_message)
    infoTextFirstLine.visibility = View.VISIBLE
    infoTextSecondLine.text = getText(R.string.search_no_internet_message)
    infoTextSecondLine.visibility = View.VISIBLE
    retryButton.visibility = View.VISIBLE
  }

  override fun showNoInternetToast() {
    errorToast(getString(R.string.search_no_internet_message))
  }

  override fun showGenericErrorView() {
    hideAllLoadersAndMessageViews()
    infoImageView.setImageResource(R.drawable.ic_no_result_gray)
    infoImageView.visibility = View.VISIBLE
    infoTextFirstLine.text = getText(R.string.search_something_went_wrong_message)
    infoTextFirstLine.visibility = View.VISIBLE
    retryButton.visibility = View.VISIBLE
  }

  override fun showGenericErrorToast() {
    errorToast(stringRes(R.string.search_generic_error_message))
  }

  override fun showInputASearchQueryMessageView() {
    hideAllLoadersAndMessageViews()
    infoImageView.setImageResource(R.drawable.ic_no_result_gray)
    infoImageView.visibility = View.VISIBLE
    infoTextFirstLine.text = getText(R.string.search_input_a_query_and_please_try_again)
    infoTextFirstLine.visibility = View.VISIBLE
    searchView.setQuery("", false)
  }

  override fun showSearchResults(list: List<SearchPicturesPresenterEntity>) {
    imageRecyclerviewPresenter.setSearchResultList(list)
    recyclerviewAdapter?.notifyDataSetChanged()
  }

  override fun appendSearchResults(startPosition: Int, list: List<SearchPicturesPresenterEntity>) {
    imageRecyclerviewPresenter.addToSearchResultList(list)
    recyclerviewAdapter?.notifyItemRangeInserted(startPosition, (list.size - 1))
  }

  override fun getScope(): ScopeProvider {
    return AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
  }

  override fun setEndlessLoadingToFalse() {
    EndlessScrollListener.loading = false
  }

  private fun initAppbar() {
    searchAppBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
      if (Math.abs(verticalOffset) == appBarLayout.totalScrollRange) {
        appBarCollapsed = true
      } else if (verticalOffset == 0) {
        appBarCollapsed = false
      }
    }
    searchView.backButton.setOnClickListener { onBackPressed() }
    searchView.setVoiceSearch(true)
    searchView.showSearch()
    searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        searchView.hideKeyboard(currentFocus)
        presenter.notifyQuerySubmitted(query)
        return true
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        return false
      }
    })
  }

  private fun initRecyclerView() {
    val layoutManager =
        GridLayoutManager(this.baseContext, integerRes(R.integer.recycler_view_span_count))
    recyclerView.layoutManager = layoutManager
    recyclerviewAdapter = ImageRecyclerviewAdapter(imageRecyclerviewPresenter)
    val scaleInAdapter = ScaleInAnimationAdapter(recyclerviewAdapter)
    scaleInAdapter.setDuration(TimeUnit.MILLISECONDS.toMillis(500).toInt())
    recyclerView.addItemDecoration(
        GridItemDecorator(integerRes(R.integer.recycler_view_grid_spacing_px),
            integerRes(R.integer.recycler_view_grid_size)))
    recyclerView.adapter = scaleInAdapter
    imageRecyclerviewPresenter.setListType(SEARCH)
    recyclerView.addOnScrollListener(object : EndlessScrollListener(layoutManager) {
      override fun onLoadMore() {
        presenter.fetchMoreImages()
      }
    })
  }

  private fun initRetryClickListener() {
    retryButton.setOnClickListener {
      presenter.notifyRetryButtonClicked()
    }
  }
}
