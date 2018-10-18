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
import zebrostudio.wallr100.android.ui.adapters.ImageAdapter
import zebrostudio.wallr100.android.utils.EndlessScrollListener
import zebrostudio.wallr100.android.utils.GridItemDecorator
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.android.utils.withDelayOnMain
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.*
import zebrostudio.wallr100.presentation.search.SearchContract
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchActivity : AppCompatActivity(), SearchContract.SearchView {

  @Inject
  internal lateinit var presenter: SearchContract.SearchPresenter
  @Inject
  internal lateinit var imageRecyclerViewPresenter: ImageRecyclerItemContract.ImageRecyclerViewPresenter

  private var appBarCollapsed = false
  private var recyclerviewAdapter: ImageAdapter? = null

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
        val searchWord = matches[0]
        if (searchWord.isEmpty()) {
          searchView.setQuery(searchWord, false)
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
    SearchActivitySpinkitView.visible()
  }

  override fun hideLoader() {
    SearchActivitySpinkitView.gone()
  }

  override fun showBottomLoader() {
    bottomSpinkitView.visible()
  }

  override fun hideBottomLoader() {
    bottomSpinkitView.gone()
  }

  override fun showNoInputView() {
    hideAllLoadersAndMessageViews()
    infoImageView.setImageResource(R.drawable.ic_no_input_gray)
    infoImageView.visible()
    infoTextFirstLine.text = getText(R.string.search_type_in_a_query_message)
    infoTextFirstLine.visible()
  }

  override fun showNoResultView(query: String) {
    hideAllLoadersAndMessageViews()
    infoImageView.setImageResource(R.drawable.ic_no_result_gray)
    infoImageView.visible()
    val noResultText = "${getText(R.string.search_no_result_message)} '$query'"
    infoTextFirstLine.text = noResultText
    infoTextFirstLine.visible()
  }

  override fun hideAllLoadersAndMessageViews() {
    infoTextFirstLine.gone()
    infoTextSecondLine.gone()
    infoImageView.gone()
    retryButton.gone()
    imageRecyclerViewPresenter.clearAll()
    recyclerviewAdapter?.notifyDataSetChanged()
    hideLoader()
    hideBottomLoader()
  }

  override fun showNoInternetView() {
    hideAllLoadersAndMessageViews()
    infoImageView.setImageResource(R.drawable.ic_no_result_gray)
    infoImageView.visible()
    infoTextFirstLine.text = getText(R.string.search_unable_to_search_message)
    infoTextFirstLine.visible()
    infoTextSecondLine.text = getText(R.string.search_no_internet_message)
    infoTextSecondLine.visible()
    retryButton.visible()
  }

  override fun showNoInternetToast() {
    errorToast(getString(R.string.search_no_internet_message))
  }

  override fun showGenericErrorView() {
    hideAllLoadersAndMessageViews()
    infoImageView.setImageResource(R.drawable.ic_no_result_gray)
    infoImageView.visible()
    infoTextFirstLine.text = getText(R.string.search_something_went_wrong_message)
    infoTextFirstLine.visible()
    retryButton.visible()
  }

  override fun showGenericErrorToast() {
    errorToast(stringRes(R.string.search_generic_error_message))
  }

  override fun showInputASearchQueryMessageView() {
    hideAllLoadersAndMessageViews()
    infoImageView.setImageResource(R.drawable.ic_no_result_gray)
    infoImageView.visible()
    infoTextFirstLine.text = getText(R.string.search_input_a_query_and_please_try_again)
    infoTextFirstLine.visible()
    searchView.setQuery("", false)
  }

  override fun showSearchResults(list: List<SearchPicturesPresenterEntity>) {
    imageRecyclerViewPresenter.setSearchResultList(list)
    recyclerviewAdapter?.notifyDataSetChanged()
  }

  override fun appendSearchResults(startPosition: Int, list: List<SearchPicturesPresenterEntity>) {
    imageRecyclerViewPresenter.addToSearchResultList(list)
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
      override fun onQueryTextSubmit(query: String): Boolean {
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
    recyclerviewAdapter = ImageAdapter(imageRecyclerViewPresenter)
    val scaleInAdapter = ScaleInAnimationAdapter(recyclerviewAdapter)
    scaleInAdapter.setDuration(TimeUnit.MILLISECONDS.toMillis(500).toInt())
    recyclerView.addItemDecoration(
        GridItemDecorator(integerRes(R.integer.recycler_view_grid_spacing_px),
            integerRes(R.integer.recycler_view_grid_size)))
    recyclerView.adapter = scaleInAdapter
    imageRecyclerViewPresenter.setListType(SEARCH)
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
