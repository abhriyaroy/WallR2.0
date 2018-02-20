package zebrostudio.wallr100.ui.explore;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.di.ActivityScope;
import zebrostudio.wallr100.di.ApplicationContext;
import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.utils.FragmentTags;
import zebrostudio.wallr100.utils.UiCustomizationHelper;

@ActivityScope
public class ExploreFragment extends DaggerFragment implements ExploreFragmentContract.ExploreView {

    @Inject
    MainActivity mMainActivity;
    @Inject
    ExplorePresenterImpl mExplorePresenter;
    @Inject
    UiCustomizationHelper mUiCustomizationHelper;
    @ApplicationContext
    @Inject
    Context mApplicationContext;

    @Inject
    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mExplorePresenter.bindView(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpUi();
        Log.d(FragmentTags.EXPLORE_FRAGMENT_TAG,"onresume");
        mExplorePresenter.updateCurrentFragmentTag();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mExplorePresenter.unbindView();
    }

    private void setUpUi() {
        mMainActivity.setTitle(FragmentTags.EXPLORE_FRAGMENT_TAG);
        mMainActivity.setTitlePadding(0, 0, 0, 0);
        mUiCustomizationHelper.showSearchOption();
        mUiCustomizationHelper.hideMultiSelectOption();
        mUiCustomizationHelper.hideSmartTabLayout();
        mUiCustomizationHelper.hideCollectionSwitchLayout();
        mUiCustomizationHelper.hideMinimalBottomPanel();
    }

}
