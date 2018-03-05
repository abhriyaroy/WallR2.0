package zebrostudio.wallr100.ui.minimal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.di.ActivityScope;
import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.utils.FragmentTags;
import zebrostudio.wallr100.utils.UiCustomizationHelper;

/**
 *  Displays the Minimal wallpapers.
 */
@ActivityScope
public class MinimalFragment extends DaggerFragment implements MinimalFragmentContract.MinimalView {

    @Inject
    MainActivity mMainActivity;
    @Inject
    MinimalPresenterImpl mMinimalPresenter;
    @Inject
    UiCustomizationHelper mUiCustomizationHelper;

    @Inject
    public MinimalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_minimal, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMinimalPresenter.bindView(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpUi();
        mMinimalPresenter.updateCurrentFragmentTag();
        ((MainActivity)getActivity()).highlightMinimalMenu();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMinimalPresenter.unbindView();
    }

    private void setUpUi() {
        mMainActivity.setTitle(FragmentTags.MINIMAL_FRAGMENT_TAG);
        mMainActivity.setTitlePadding(0,0,0,0);
        mUiCustomizationHelper.hideSearchOption();
        mUiCustomizationHelper.showMultiSelectOption();
        mUiCustomizationHelper.hideSmartTabLayout();
        mUiCustomizationHelper.hideCollectionSwitchLayout();
        mUiCustomizationHelper.hideMinimalBottomPanel();
    }
}
