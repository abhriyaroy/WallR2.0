package zebrostudio.wallr100.ui.top_picks;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.di.ActivityScope;
import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.ui.top_picks.popular.PopularWallpapersFragment;
import zebrostudio.wallr100.ui.top_picks.recents.RecentWallpapersFragment;
import zebrostudio.wallr100.ui.top_picks.standouts.StandoutWallpaperFragment;
import zebrostudio.wallr100.utils.FragmentTags;
import zebrostudio.wallr100.utils.UiCustomizationHelper;

@ActivityScope
public class TopPicksFragment extends DaggerFragment {

    private Unbinder mUnbinder;

    @Inject
    MainActivity mMainActivity;
    @Inject
    UiCustomizationHelper mUiCustomizationHelper;

    @BindView(R.id.tab_viewpager)
    ViewPager mViewPager;

    @Inject
    public TopPicksFragment() {
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
        View view = inflater.inflate(R.layout.fragment_top_picks, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentPagerItemAdapter fragmentPagerItemAdapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getContext())
                .add("Recent", RecentWallpapersFragment.class)
                .add("Popular", PopularWallpapersFragment.class)
                .add("Standouts", StandoutWallpaperFragment.class)
                .create());
        mViewPager.setAdapter(fragmentPagerItemAdapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) getActivity().findViewById(R.id.tab_layout);
        viewPagerTab.setViewPager(mViewPager);
        viewPagerTab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpUi();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    private void setUpUi() {
        mMainActivity.setTitle(FragmentTags.TOP_PICKS_FRAGMENT_TAG);
        mMainActivity.setTitlePadding(0, 0, 0, 0);
        mUiCustomizationHelper.showSearchOption();
        mUiCustomizationHelper.hideMultiSelectOption();
        mUiCustomizationHelper.showSmartTabLayout();
        mUiCustomizationHelper.hideCollectionSwitchLayout();
        mUiCustomizationHelper.hideMinimalBottomPanel();
    }

}
