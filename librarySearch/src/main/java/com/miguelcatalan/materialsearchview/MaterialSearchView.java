package com.miguelcatalan.materialsearchview;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.utils.AnimationUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Miguel Catalan Bañuls
 */
public class MaterialSearchView extends FrameLayout implements Filter.FilterListener {
  public static final int REQUEST_VOICE = 9999;

  private MenuItem menuItem;
  private boolean isSearchOpen = false;
  private int animationDuration;
  private boolean clearingFocus;

  //Views
  public ImageButton backButton;
  private View searchLayout;
  private View tintView;
  private ListView suggestionsListView;
  private EditText searchSrcTextView;
  private ImageButton voiceButton;
  private ImageButton emptyButton;
  private RelativeLayout searchTopBar;

  private CharSequence oldQueryText;
  private CharSequence userQuery;

  private OnQueryTextListener onQueryTextChangedListener;
  private SearchViewListener searchViewListener;

  private ListAdapter adapter;

  private SavedState savedState;
  private boolean submit = false;

  private boolean ellipsize = false;

  private boolean allowVoiceSearch;
  private Drawable suggestionIcon;

  private Context context;

  public MaterialSearchView(Context context) {
    this(context, null);
  }

  public MaterialSearchView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MaterialSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs);

    this.context = context;

    initiateView();

    initStyle(attrs, defStyleAttr);
  }

  private void initStyle(AttributeSet attrs, int defStyleAttr) {
    TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.MaterialSearchView, defStyleAttr, 0);

    if (a != null) {
      if (a.hasValue(R.styleable.MaterialSearchView_searchBackground)) {
        setBackground(a.getDrawable(R.styleable.MaterialSearchView_searchBackground));
      }

      if (a.hasValue(R.styleable.MaterialSearchView_android_textColor)) {
        setTextColor(a.getColor(R.styleable.MaterialSearchView_android_textColor, 0));
      }

      if (a.hasValue(R.styleable.MaterialSearchView_android_textColorHint)) {
        setHintTextColor(a.getColor(R.styleable.MaterialSearchView_android_textColorHint, 0));
      }

      if (a.hasValue(R.styleable.MaterialSearchView_android_hint)) {
        setHint(a.getString(R.styleable.MaterialSearchView_android_hint));
      }

      if (a.hasValue(R.styleable.MaterialSearchView_searchVoiceIcon)) {
        setVoiceIcon(a.getDrawable(R.styleable.MaterialSearchView_searchVoiceIcon));
      }

      if (a.hasValue(R.styleable.MaterialSearchView_searchCloseIcon)) {
        setCloseIcon(a.getDrawable(R.styleable.MaterialSearchView_searchCloseIcon));
      }

      if (a.hasValue(R.styleable.MaterialSearchView_searchBackIcon)) {
        setBackIcon(a.getDrawable(R.styleable.MaterialSearchView_searchBackIcon));
      }

      if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionBackground)) {
        setSuggestionBackground(
            a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionBackground));
      }

      if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionIcon)) {
        setSuggestionIcon(a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionIcon));
      }

      a.recycle();
    }
  }

  private void initiateView() {
    LayoutInflater.from(context).inflate(R.layout.search_view, this, true);
    searchLayout = findViewById(R.id.search_layout);

    searchTopBar = (RelativeLayout) searchLayout.findViewById(R.id.search_top_bar);
    suggestionsListView = (ListView) searchLayout.findViewById(R.id.suggestion_list);
    searchSrcTextView = (EditText) searchLayout.findViewById(R.id.searchTextView);
    backButton = (ImageButton) searchLayout.findViewById(R.id.action_up_btn);
    voiceButton = (ImageButton) searchLayout.findViewById(R.id.action_voice_btn);
    emptyButton = (ImageButton) searchLayout.findViewById(R.id.action_empty_btn);
    tintView = searchLayout.findViewById(R.id.transparent_view);

    searchSrcTextView.setOnClickListener(mOnClickListener);
    voiceButton.setOnClickListener(mOnClickListener);
    emptyButton.setOnClickListener(mOnClickListener);
    tintView.setOnClickListener(mOnClickListener);

    allowVoiceSearch = false;

    showVoice(true);

    initSearchView();

    suggestionsListView.setVisibility(GONE);
    setAnimationDuration(AnimationUtil.ANIMATION_DURATION_MEDIUM);
  }

  private void initSearchView() {
    searchSrcTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        onSubmitQuery();
        return true;
      }
    });

    searchSrcTextView.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        userQuery = s;
        startFilter(s);
        MaterialSearchView.this.onTextChanged(s);
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    searchSrcTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          showKeyboard(searchSrcTextView);
          showSuggestions();
        }
      }
    });
  }

  private void startFilter(CharSequence s) {
    if (adapter != null && adapter instanceof Filterable) {
      ((Filterable) adapter).getFilter().filter(s, MaterialSearchView.this);
    }
  }

  private final OnClickListener mOnClickListener = new OnClickListener() {

    public void onClick(View v) {
      if (v == voiceButton) {
        onVoiceClicked();
      } else if (v == emptyButton) {
        searchSrcTextView.setText(null);
      } else if (v == searchSrcTextView) {
        showSuggestions();
      } else if (v == tintView) {
        closeSearch();
      }
    }
  };

  private void onVoiceClicked() {
    try {
      Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
      //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak an item name or number");    // user hint
      intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
          RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);    // setting recognition model, optimized for short phrases – search queries
      intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,
          1);    // quantity of results we want to receive
      if (context instanceof Activity) {
        ((Activity) context).startActivityForResult(intent, REQUEST_VOICE);
      }
    } catch (ActivityNotFoundException e) {
      Toast.makeText(getContext(), R.string.exception_toast, Toast.LENGTH_SHORT).show();
    }
  }

  private void onTextChanged(CharSequence newText) {
    CharSequence text = searchSrcTextView.getText();
    userQuery = text;
    boolean hasText = !TextUtils.isEmpty(text);
    if (hasText) {
      emptyButton.setVisibility(VISIBLE);
      showVoice(false);
    } else {
      emptyButton.setVisibility(GONE);
      showVoice(true);
    }

    if (onQueryTextChangedListener != null && !TextUtils.equals(newText, oldQueryText)) {
      onQueryTextChangedListener.onQueryTextChange(newText.toString());
    }
    oldQueryText = newText.toString();
  }

  private void onSubmitQuery() {
    CharSequence query = searchSrcTextView.getText();
    if (query != null && TextUtils.getTrimmedLength(query) > 0) {
      if (onQueryTextChangedListener == null || !onQueryTextChangedListener.onQueryTextSubmit(
          query.toString())) {
        closeSearch();
        searchSrcTextView.setText(null);
      }
    }
  }

  private boolean isVoiceAvailable() {
    if (isInEditMode()) {
      return true;
    }
    PackageManager pm = getContext().getPackageManager();
    List<ResolveInfo> activities = pm.queryIntentActivities(
        new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
    return activities.size() == 0;
  }

  public void hideKeyboard(View view) {
    InputMethodManager imm =
        (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public void showKeyboard(View view) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view.hasFocus()) {
      view.clearFocus();
    }
    view.requestFocus();
    InputMethodManager imm =
        (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(view, 0);
  }

  //Public Attributes

  @Override
  public void setBackground(Drawable background) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      searchTopBar.setBackground(background);
    } else {
      searchTopBar.setBackgroundDrawable(background);
    }
  }

  @Override
  public void setBackgroundColor(int color) {
    searchTopBar.setBackgroundColor(color);
  }

  public void setTextColor(int color) {
    searchSrcTextView.setTextColor(color);
  }

  public void setHintTextColor(int color) {
    searchSrcTextView.setHintTextColor(color);
  }

  public void setHint(CharSequence hint) {
    searchSrcTextView.setHint(hint);
  }

  public void setVoiceIcon(Drawable drawable) {
    voiceButton.setImageDrawable(drawable);
  }

  public void setCloseIcon(Drawable drawable) {
    emptyButton.setImageDrawable(drawable);
  }

  public void setBackIcon(Drawable drawable) {
    backButton.setImageDrawable(drawable);
  }

  public void setSuggestionIcon(Drawable drawable) {
    suggestionIcon = drawable;
  }

  public void setSuggestionBackground(Drawable background) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      suggestionsListView.setBackground(background);
    } else {
      suggestionsListView.setBackgroundDrawable(background);
    }
  }

  public void setCursorDrawable(int drawable) {
    try {
      // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
      Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
      f.setAccessible(true);
      f.set(searchSrcTextView, drawable);
    } catch (Exception ignored) {
      Log.e("MaterialSearchView", ignored.toString());
    }
  }

  public void setVoiceSearch(boolean voiceSearch) {
    allowVoiceSearch = voiceSearch;
  }

  //Public Methods

  /**
   * Call this method to show suggestions list. This shows up when adapter is set. Call {@link #setAdapter(ListAdapter)} before calling this.
   */
  public void showSuggestions() {
    if (adapter != null
        && adapter.getCount() > 0
        && suggestionsListView.getVisibility() == GONE) {
      suggestionsListView.setVisibility(VISIBLE);
    }
  }

  /**
   * Submit the query as soon as the user clicks the item.
   *
   * @param submit submit state
   */
  public void setSubmitOnClick(boolean submit) {
    this.submit = submit;
  }

  /**
   * Set Suggest List OnItemClickListener
   */
  public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
    suggestionsListView.setOnItemClickListener(listener);
  }

  /**
   * Set Adapter for suggestions list. Should implement Filterable.
   */
  public void setAdapter(ListAdapter adapter) {
    this.adapter = adapter;
    suggestionsListView.setAdapter(adapter);
    startFilter(searchSrcTextView.getText());
  }

  /**
   * Set Adapter for suggestions list with the given suggestion array
   *
   * @param suggestions array of suggestions
   */
  public void setSuggestions(String[] suggestions) {
    if (suggestions != null && suggestions.length > 0) {
      tintView.setVisibility(VISIBLE);
      final SearchAdapter adapter =
          new SearchAdapter(context, suggestions, suggestionIcon, ellipsize);
      setAdapter(adapter);

      setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          setQuery((String) adapter.getItem(position), submit);
        }
      });
    } else {
      tintView.setVisibility(GONE);
    }
  }

  /**
   * Dismiss the suggestions list.
   */
  public void dismissSuggestions() {
    if (suggestionsListView.getVisibility() == VISIBLE) {
      suggestionsListView.setVisibility(GONE);
    }
  }

  /**
   * Calling this will set the query to search text box. if submit is true, it'll submit the query.
   */
  public void setQuery(CharSequence query, boolean submit) {
    searchSrcTextView.setText(query);
    if (query != null) {
      searchSrcTextView.setSelection(searchSrcTextView.length());
      userQuery = query;
    }
    if (submit && !TextUtils.isEmpty(query)) {
      onSubmitQuery();
    }
  }

  /**
   * if show is true, this will enable voice search. If voice is not available on the device, this method call has not effect.
   */
  public void showVoice(boolean show) {
    if (show && isVoiceAvailable() && allowVoiceSearch) {
      voiceButton.setVisibility(VISIBLE);
    } else {
      voiceButton.setVisibility(GONE);
    }
  }

  /**
   * Call this method and pass the menu item so this class can handle click events for the Menu Item.
   */
  public void setMenuItem(MenuItem menuItem) {
    this.menuItem = menuItem;
    this.menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        showSearch();
        return true;
      }
    });
  }

  /**
   * Return true if search is open
   */
  public boolean isSearchOpen() {
    return isSearchOpen;
  }

  /**
   * Sets animation duration. ONLY FOR PRE-LOLLIPOP!!
   *
   * @param duration duration of the animation
   */
  public void setAnimationDuration(int duration) {
    animationDuration = duration;
  }

  /**
   * Open Search View. This will animate the showing of the view.
   */
  public void showSearch() {
    showSearch(true);
  }

  /**
   * Open Search View. If animate is true, Animate the showing of the view.
   *
   * @param animate true for animate
   */
  public void showSearch(boolean animate) {
    if (isSearchOpen()) {
      return;
    }

    //Request Focus
    searchSrcTextView.setText(null);
    searchSrcTextView.requestFocus();
    searchLayout.setVisibility(VISIBLE);
    if (searchViewListener != null) {
      searchViewListener.onSearchViewShown();
    }
    isSearchOpen = true;
  }

  private void setVisibleWithAnimation() {
    AnimationUtil.AnimationListener animationListener = new AnimationUtil.AnimationListener() {
      @Override
      public boolean onAnimationStart(View view) {
        return false;
      }

      @Override
      public boolean onAnimationEnd(View view) {
        if (searchViewListener != null) {
          searchViewListener.onSearchViewShown();
        }
        return false;
      }

      @Override
      public boolean onAnimationCancel(View view) {
        return false;
      }
    };

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      searchLayout.setVisibility(View.VISIBLE);
      AnimationUtil.reveal(searchTopBar, animationListener);
    } else {
      AnimationUtil.fadeInView(searchLayout, animationDuration, animationListener);
    }
  }

  /**
   * Close search view.
   */
  public void closeSearch() {
    if (!isSearchOpen()) {
      return;
    }

    searchSrcTextView.setText(null);
    dismissSuggestions();
    clearFocus();

    searchLayout.setVisibility(GONE);
    if (searchViewListener != null) {
      searchViewListener.onSearchViewClosed();
    }
    isSearchOpen = false;
  }

  /**
   * Set this listener to listen to Query Change events.
   */
  public void setOnQueryTextListener(OnQueryTextListener listener) {
    onQueryTextChangedListener = listener;
  }

  /**
   * Set this listener to listen to Search View open and close events
   */
  public void setOnSearchViewListener(SearchViewListener listener) {
    searchViewListener = listener;
  }

  /**
   * Ellipsize suggestions longer than one line.
   */
  public void setEllipsize(boolean ellipsize) {
    this.ellipsize = ellipsize;
  }

  @Override
  public void onFilterComplete(int count) {
    if (count > 0) {
      showSuggestions();
    } else {
      dismissSuggestions();
    }
  }

  @Override
  public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
    // Don't accept focus if in the middle of clearing focus
    if (clearingFocus) return false;
    // Check if SearchView is focusable.
    if (!isFocusable()) return false;
    return searchSrcTextView.requestFocus(direction, previouslyFocusedRect);
  }

  @Override
  public void clearFocus() {
    clearingFocus = true;
    hideKeyboard(this);
    super.clearFocus();
    searchSrcTextView.clearFocus();
    clearingFocus = false;
  }

  @Override
  public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();

    savedState = new SavedState(superState);
    savedState.query = userQuery != null ? userQuery.toString() : null;
    savedState.isSearchOpen = this.isSearchOpen;

    return savedState;
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }

    savedState = (SavedState) state;

    if (savedState.isSearchOpen) {
      showSearch(false);
      setQuery(savedState.query, false);
    }

    super.onRestoreInstanceState(savedState.getSuperState());
  }

  static class SavedState extends BaseSavedState {
    String query;
    boolean isSearchOpen;

    SavedState(Parcelable superState) {
      super(superState);
    }

    private SavedState(Parcel in) {
      super(in);
      this.query = in.readString();
      this.isSearchOpen = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeString(query);
      out.writeInt(isSearchOpen ? 1 : 0);
    }

    //required field that makes Parcelables from a Parcel
    public static final Creator<SavedState> CREATOR =
        new Creator<SavedState>() {
          public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
          }

          public SavedState[] newArray(int size) {
            return new SavedState[size];
          }
        };
  }

  public interface OnQueryTextListener {

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
    boolean onQueryTextSubmit(String query);

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    boolean onQueryTextChange(String newText);
  }

  public interface SearchViewListener {
    void onSearchViewShown();

    void onSearchViewClosed();
  }
}