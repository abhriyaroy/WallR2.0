package com.miguelcatalan.materialsearchview

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.miguelcatalan.materialsearchview.utils.AnimationUtil

class MaterialSearchView @JvmOverloads constructor(
  private val mContext: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(mContext, attrs), Filter.FilterListener {

  private var mMenuItem: MenuItem? = null
  /**
   * Return true if search is open
   *
   * @return
   */
  var isSearchOpen = false
  private var animationDuration: Int = 0
  private var clearingFocus: Boolean = false

  //Views
  private var searchLayout: View? = null
  private var tintView: View? = null
  private var suggestionsListView: ListView? = null
  private var searchSrcTextView: EditText? = null
  private var backBtn: ImageButton? = null
  private var voiceBtn: ImageButton? = null
  private var emptyBtn: ImageButton? = null
  private var searchTopBar: RelativeLayout? = null

  private var oldQueryText: CharSequence? = null
  private var userQuery: CharSequence? = null

  private var onQueryChangeListener: OnQueryTextListener? = null
  private var searchViewListener: SearchViewListener? = null

  private var adapter: ListAdapter? = null

  private var savedState: SavedState? = null
  private var submit = false

  private var ellipsize = false

  private var allowVoiceSearch: Boolean = false
  private lateinit var suggestionIcon: Drawable

  private val mOnClickListener = OnClickListener { v ->
    when {
      v === voiceBtn -> onVoiceClicked()
      v === emptyBtn -> searchSrcTextView!!.text = null
      v === searchSrcTextView -> showSuggestions()
      v === tintView -> closeSearch()
    }
  }

  private val isVoiceAvailable: Boolean
    get() {
      if (isInEditMode) {
        return true
      }
      val pm = context.packageManager
      val activities = pm.queryIntentActivities(
          Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0)
      return activities.size == 0
    }

  init {

    initiateView()

    initStyle(attrs, defStyleAttr)
  }

  private fun initStyle(attrs: AttributeSet?, defStyleAttr: Int) {
    val a = mContext.obtainStyledAttributes(attrs, R.styleable.MaterialSearchView, defStyleAttr, 0)

    if (a != null) {
      if (a.hasValue(R.styleable.MaterialSearchView_searchBackground)) {
        background = a.getDrawable(R.styleable.MaterialSearchView_searchBackground)
      }

      if (a.hasValue(R.styleable.MaterialSearchView_android_textColor)) {
        setTextColor(a.getColor(R.styleable.MaterialSearchView_android_textColor, 0))
      }

      if (a.hasValue(R.styleable.MaterialSearchView_android_textColorHint)) {
        setHintTextColor(a.getColor(R.styleable.MaterialSearchView_android_textColorHint, 0))
      }

      if (a.hasValue(R.styleable.MaterialSearchView_android_hint)) {
        setHint(a.getString(R.styleable.MaterialSearchView_android_hint))
      }

      if (a.hasValue(R.styleable.MaterialSearchView_searchVoiceIcon)) {
        setVoiceIcon(a.getDrawable(R.styleable.MaterialSearchView_searchVoiceIcon))
      }

      if (a.hasValue(R.styleable.MaterialSearchView_searchCloseIcon)) {
        setCloseIcon(a.getDrawable(R.styleable.MaterialSearchView_searchCloseIcon))
      }

      if (a.hasValue(R.styleable.MaterialSearchView_searchBackIcon)) {
        setBackIcon(a.getDrawable(R.styleable.MaterialSearchView_searchBackIcon))
      }

      if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionBackground)) {
        setSuggestionBackground(
            a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionBackground))
      }

      if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionIcon)) {
        setSuggestionIcon(a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionIcon))
      }

      a.recycle()
    }
  }

  private fun initiateView() {
    LayoutInflater.from(mContext).inflate(R.layout.search_view, this, true)
    searchLayout = findViewById(R.id.search_layout)

    searchTopBar = searchLayout!!.findViewById<View>(R.id.search_top_bar) as RelativeLayout
    suggestionsListView = searchLayout!!.findViewById<View>(R.id.suggestion_list) as ListView
    searchSrcTextView = searchLayout!!.findViewById<View>(R.id.searchTextView) as EditText
    backBtn = searchLayout!!.findViewById<View>(R.id.action_up_btn) as ImageButton
    voiceBtn = searchLayout!!.findViewById<View>(R.id.action_voice_btn) as ImageButton
    emptyBtn = searchLayout!!.findViewById<View>(R.id.action_empty_btn) as ImageButton
    tintView = searchLayout!!.findViewById(R.id.transparent_view)

    searchSrcTextView!!.setOnClickListener(mOnClickListener)
    voiceBtn!!.setOnClickListener(mOnClickListener)
    emptyBtn!!.setOnClickListener(mOnClickListener)
    tintView!!.setOnClickListener(mOnClickListener)

    allowVoiceSearch = false

    showVoice(true)

    initSearchView()

    suggestionsListView!!.visibility = View.GONE
    setAnimationDuration(AnimationUtil.ANIMATION_DURATION_MEDIUM)
  }

  private fun initSearchView() {
    searchSrcTextView!!.setOnEditorActionListener { v, actionId, event ->
      onSubmitQuery()
      true
    }

    searchSrcTextView!!.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        userQuery = s
        startFilter(s)
        this@MaterialSearchView.onTextChanged(s)
      }

      override fun afterTextChanged(s: Editable) {

      }
    })

    searchSrcTextView!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
      if (hasFocus) {
        showKeyboard(searchSrcTextView)
        showSuggestions()
      }
    }
  }

  private fun startFilter(s: CharSequence) {
    if (adapter != null && adapter is Filterable) {
      (adapter as Filterable).filter.filter(s, this@MaterialSearchView)
    }
  }

  private fun onVoiceClicked() {
    try {
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
      //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak an item name or number");    // user hint
      intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
          RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)    // setting recognition model, optimized for short phrases – search queries
      intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,
          1)    // quantity of results we want to receive
      if (mContext is Activity) {
        mContext.startActivityForResult(intent, REQUEST_VOICE)
      }
    } catch (e: ActivityNotFoundException) {
      Toast.makeText(context, context.getString(R.string.exception_toast), Toast.LENGTH_SHORT)
          .show()
    }

  }

  private fun onTextChanged(newText: CharSequence) {
    val text = searchSrcTextView!!.text
    userQuery = text
    val hasText = !TextUtils.isEmpty(text)
    if (hasText) {
      emptyBtn!!.visibility = View.VISIBLE
      showVoice(false)
    } else {
      emptyBtn!!.visibility = View.GONE
      showVoice(true)
    }

    if (onQueryChangeListener != null && !TextUtils.equals(newText, oldQueryText)) {
      onQueryChangeListener!!.onQueryTextChange(newText.toString())
    }
    oldQueryText = newText.toString()
  }

  private fun onSubmitQuery() {
    val query = searchSrcTextView!!.text
    if (query != null && TextUtils.getTrimmedLength(query) > 0) {
      if (onQueryChangeListener == null || !onQueryChangeListener!!.onQueryTextSubmit(
              query.toString())) {
        closeSearch()
        searchSrcTextView!!.text = null
      }
    }
  }

  fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
  }

  fun showKeyboard(view: View?) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view!!.hasFocus()) {
      view.clearFocus()
    }
    view!!.requestFocus()
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
  }

  //Public Attributes

  override fun setBackground(background: Drawable?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      searchTopBar!!.background = background
    } else {
      searchTopBar!!.setBackgroundDrawable(background)
    }
  }

  override fun setBackgroundColor(color: Int) {
    searchTopBar!!.setBackgroundColor(color)
  }

  fun setTextColor(color: Int) {
    searchSrcTextView!!.setTextColor(color)
  }

  fun setHintTextColor(color: Int) {
    searchSrcTextView!!.setHintTextColor(color)
  }

  fun setHint(hint: CharSequence?) {
    searchSrcTextView!!.hint = hint
  }

  fun setVoiceIcon(drawable: Drawable?) {
    voiceBtn!!.setImageDrawable(drawable)
  }

  fun setCloseIcon(drawable: Drawable?) {
    emptyBtn!!.setImageDrawable(drawable)
  }

  fun setBackIcon(drawable: Drawable?) {
    backBtn?.setImageDrawable(drawable)
  }

  fun setSuggestionIcon(drawable: Drawable) {
    suggestionIcon = drawable
  }

  fun setSuggestionBackground(background: Drawable?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      suggestionsListView!!.background = background
    } else {
      suggestionsListView!!.setBackgroundDrawable(background)
    }
  }

  fun setCursorDrawable(drawable: Int) {
    try {
      // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
      val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
      f.isAccessible = true
      f.set(searchSrcTextView, drawable)
    } catch (ignored: Exception) {
      Log.e("MaterialSearchView", ignored.toString())
    }

  }

  fun setVoiceSearch(voiceSearch: Boolean) {
    allowVoiceSearch = voiceSearch
  }

  //Public Methods

  /**
   * Call this method to show suggestions list. This shows up when adapter is set. Call [.setAdapter] before calling this.
   */
  fun showSuggestions() {
    if (adapter != null && adapter!!.count > 0 && suggestionsListView!!.visibility == View.GONE) {
      suggestionsListView!!.visibility = View.VISIBLE
    }
  }

  /**
   * Submit the query as soon as the user clicks the item.
   *
   * @param submit submit state
   */
  fun setSubmitOnClick(submit: Boolean) {
    this.submit = submit
  }

  /**
   * Set Suggest List OnItemClickListener
   *
   * @param listener
   */
  fun setOnItemClickListener(listener: AdapterView.OnItemClickListener) {
    suggestionsListView!!.onItemClickListener = listener
  }

  /**
   * Set Adapter for suggestions list. Should implement Filterable.
   *
   * @param adapter
   */
  fun setAdapter(adapter: ListAdapter) {
    this.adapter = adapter
    suggestionsListView!!.adapter = adapter
    startFilter(searchSrcTextView!!.text)
  }

  /**
   * Set Adapter for suggestions list with the given suggestion array
   *
   * @param suggestions array of suggestions
   */
  fun setSuggestions(suggestions: Array<String>?) {
    if (suggestions != null && suggestions.isNotEmpty()) {
      tintView!!.visibility = View.VISIBLE
      val adapter = SearchAdapter(mContext, suggestions, suggestionIcon, ellipsize)
      setAdapter(adapter)

      setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
        setQuery(adapter.getItem(position) as String, submit)
      })
    } else {
      tintView!!.visibility = View.GONE
    }
  }

  /**
   * Dismiss the suggestions list.
   */
  fun dismissSuggestions() {
    if (suggestionsListView!!.visibility == View.VISIBLE) {
      suggestionsListView!!.visibility = View.GONE
    }
  }

  /**
   * Calling this will set the query to search text box. if submit is true, it'll submit the query.
   *
   * @param query
   * @param submit
   */
  fun setQuery(query: CharSequence?, submit: Boolean) {
    searchSrcTextView!!.setText(query)
    if (query != null) {
      searchSrcTextView!!.setSelection(searchSrcTextView!!.length())
      userQuery = query
    }
    if (submit && !TextUtils.isEmpty(query)) {
      onSubmitQuery()
    }
  }

  /**
   * if show is true, this will enable voice search. If voice is not available on the device, this method call has not effect.
   *
   * @param show
   */
  fun showVoice(show: Boolean) {
    if (show && isVoiceAvailable && allowVoiceSearch) {
      voiceBtn!!.visibility = View.VISIBLE
    } else {
      voiceBtn!!.visibility = View.GONE
    }
  }

  /**
   * Call this method and pass the menu item so this class can handle click events for the Menu Item.
   *
   * @param menuItem
   */
  fun setMenuItem(menuItem: MenuItem) {
    this.mMenuItem = menuItem
    mMenuItem!!.setOnMenuItemClickListener {
      showSearch()
      true
    }
  }

  /**
   * Sets animation duration. ONLY FOR PRE-LOLLIPOP!!
   *
   * @param duration duration of the animation
   */
  fun setAnimationDuration(duration: Int) {
    animationDuration = duration
  }

  /**
   * Open Search View. If animate is true, Animate the showing of the view.
   *
   * @param animate true for animate
   */
  @JvmOverloads fun showSearch(animate: Boolean = true) {
    if (isSearchOpen) {
      return
    }

    //Request Focus
    searchSrcTextView!!.text = null
    searchSrcTextView!!.requestFocus()
    searchLayout!!.visibility = View.VISIBLE
    if (searchViewListener != null) {
      searchViewListener!!.onSearchViewShown()

    }
    isSearchOpen = true
  }

  private fun setVisibleWithAnimation() {
    val animationListener = object : AnimationUtil.AnimationListener {
      override fun onAnimationStart(view: View): Boolean {
        return false
      }

      override fun onAnimationEnd(view: View): Boolean {
        if (searchViewListener != null) {
          searchViewListener!!.onSearchViewShown()
        }
        return false
      }

      override fun onAnimationCancel(view: View): Boolean {
        return false
      }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      searchLayout!!.visibility = View.VISIBLE
      AnimationUtil.reveal(searchTopBar!!, animationListener)

    } else {
      AnimationUtil.fadeInView(searchLayout!!, animationDuration, animationListener)
    }
  }

  /**
   * Close search view.
   */
  fun closeSearch() {
    if (!isSearchOpen) {
      return
    }

    searchSrcTextView!!.text = null
    dismissSuggestions()
    clearFocus()

    searchLayout!!.visibility = View.GONE
    if (searchViewListener != null) {
      searchViewListener!!.onSearchViewClosed()
    }
    isSearchOpen = false

  }

  /**
   * Set this listener to listen to Query Change events.
   *
   * @param listener
   */
  fun setOnQueryTextListener(listener: OnQueryTextListener) {
    onQueryChangeListener = listener
  }

  /**
   * Set this listener to listen to Search View open and close events
   *
   * @param listener
   */
  fun setOnSearchViewListener(listener: SearchViewListener) {
    searchViewListener = listener
  }

  /**
   * Ellipsize suggestions longer than one line.
   *
   * @param ellipsize
   */
  fun setEllipsize(ellipsize: Boolean) {
    this.ellipsize = ellipsize
  }

  override fun onFilterComplete(count: Int) {
    if (count > 0) {
      showSuggestions()
    } else {
      dismissSuggestions()
    }
  }

  override fun requestFocus(direction: Int, previouslyFocusedRect: Rect): Boolean {
    // Don't accept focus if in the middle of clearing focus
    if (clearingFocus) return false
    // Check if SearchView is focusable.
    return if (!isFocusable) false else searchSrcTextView!!.requestFocus(direction,
        previouslyFocusedRect)
  }

  override fun clearFocus() {
    clearingFocus = true
    hideKeyboard(this)
    super.clearFocus()
    searchSrcTextView!!.clearFocus()
    clearingFocus = false
  }

  public override fun onSaveInstanceState(): Parcelable? {
    val superState = super.onSaveInstanceState()

    savedState = SavedState(superState)
    savedState!!.query = if (userQuery != null) userQuery!!.toString() else null
    savedState!!.isSearchOpen = this.isSearchOpen

    return savedState
  }

  public override fun onRestoreInstanceState(state: Parcelable) {
    if (state !is SavedState) {
      super.onRestoreInstanceState(state)
      return
    }

    savedState = state

    if (savedState!!.isSearchOpen) {
      showSearch(false)
      setQuery(savedState!!.query, false)
    }

    super.onRestoreInstanceState(savedState!!.superState)
  }

  internal class SavedState : View.BaseSavedState {
    var query: String? = null
    var isSearchOpen: Boolean = false

    constructor(superState: Parcelable) : super(superState) {}

    private constructor(`in`: Parcel) : super(`in`) {
      this.query = `in`.readString()
      this.isSearchOpen = `in`.readInt() == 1
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
      super.writeToParcel(out, flags)
      out.writeString(query)
      out.writeInt(if (isSearchOpen) 1 else 0)
    }

    companion object {

      //required field that makes Parcelables from a Parcel
      val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
        override fun createFromParcel(`in`: Parcel): SavedState {
          return SavedState(`in`)
        }

        override fun newArray(size: Int): Array<SavedState?> {
          return arrayOfNulls(size)
        }
      }
    }
  }

  interface OnQueryTextListener {

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    fun onQueryTextSubmit(query: String): Boolean

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    fun onQueryTextChange(newText: String): Boolean
  }

  interface SearchViewListener {
    fun onSearchViewShown()

    fun onSearchViewClosed()
  }

  companion object {
    val REQUEST_VOICE = 9999
  }

}
/**
 * Open Search View. This will animate the showing of the view.
 */