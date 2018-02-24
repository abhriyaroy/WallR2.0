package zebrostudio.wallr100.ui.categories;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import zebrostudio.wallr100.ui.categories.buildings.BuildingWallpapersFragment;
import zebrostudio.wallr100.ui.categories.food.FoodWallpapersFragment;
import zebrostudio.wallr100.ui.categories.nature.NatureWallpapersFragment;
import zebrostudio.wallr100.ui.categories.objects.ObjectWallpapersFragment;
import zebrostudio.wallr100.ui.categories.people.PeopleWallpapersFragment;
import zebrostudio.wallr100.ui.categories.technology.TechnologyWallpapersFragment;
import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.utils.FragmentTags;
import zebrostudio.wallr100.utils.UiCustomizationHelper;

/**
 *  Displays Food, Nature, Objects, People, Technology wallpaper fragments.
 */
@ActivityScope
public class CategoriesFragment extends DaggerFragment
        implements CategoriesFragmentContract.CategoriesView {

    private Unbinder mUnbinder;

    @Inject
    MainActivity mMainActivity;
    @Inject
    CategoriesPresenterImpl mCategoriesPresenter;
    @Inject
    UiCustomizationHelper mUiCustomizationHelper;

    @BindView(R.id.tab_viewpager)
    ViewPager mViewPager;

    @Inject
    public CategoriesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCategoriesPresenter.bindView(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentPagerItemAdapter fragmentPagerItemAdapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getContext())
                .add("Buildings", BuildingWallpapersFragment.class)
                .add("Food", FoodWallpapersFragment.class)
                .add("Nature", NatureWallpapersFragment.class)
                .add("Objects", ObjectWallpapersFragment.class)
                .add("People", PeopleWallpapersFragment.class)
                .add("Technology", TechnologyWallpapersFragment.class)
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
        mCategoriesPresenter.updateCurrentFragmentTag();
        ((MainActivity)getActivity()).highlightCategoriesMenu();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCategoriesPresenter.unbindView();
    }

    private void setUpUi() {
        mMainActivity.setTitle(FragmentTags.CATEGORIES_FRAGMENT_TAG);
        mMainActivity.setTitlePadding(0, 0, 0, 0);
        mUiCustomizationHelper.showSearchOption();
        mUiCustomizationHelper.hideMultiSelectOption();
        mUiCustomizationHelper.showSmartTabLayout();
        mUiCustomizationHelper.hideCollectionSwitchLayout();
        mUiCustomizationHelper.hideMinimalBottomPanel();
    }


}
