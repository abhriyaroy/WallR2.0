package zebrostudio.wallr100.ui.categories;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.utils.FragmentTags;
import zebrostudio.wallr100.utils.UiCustomizationHelper;

public class CategoriesFragment extends DaggerFragment {

    @Inject
    MainActivity mMainActivity;
    @Inject
    UiCustomizationHelper mUiCustomizationHelper;

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
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private void setUpUi() {
        mMainActivity.setTitle(FragmentTags.CATEGORIES_FRAGMENT_TAG);
        mMainActivity.setTitlePadding(0,0,0,0);
        mUiCustomizationHelper.showSearchOption();
        mUiCustomizationHelper.hideMultiSelectOption();
        mUiCustomizationHelper.showSmartTabLayout();
        mUiCustomizationHelper.hideCollectionSwitchLayout();
        mUiCustomizationHelper.hideMinimalBottomPanel();
    }


}
