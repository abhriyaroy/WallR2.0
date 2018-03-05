package zebrostudio.wallr100.utils;

import android.view.View;

import javax.inject.Inject;

import zebrostudio.wallr100.ui.main.MainActivity;

public class UiCustomizationHelper {

    MainActivity mMainActivity;

    @Inject
    UiCustomizationHelper(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public void hideSearchOption() {
        mMainActivity.mSearchIcon.setVisibility(View.GONE);
    }

    public void showSearchOption() {
        mMainActivity.mSearchIcon.setVisibility(View.VISIBLE);
    }

    public void hideMultiSelectOption() {
        mMainActivity.mMultiSelectIcon.setVisibility(View.GONE);
    }

    public void showMultiSelectOption() {
        mMainActivity.mMultiSelectIcon.setVisibility(View.VISIBLE);
    }

    public void hideSmartTabLayout(){
        mMainActivity.mSmartTabLayout.setVisibility(View.GONE);
    }

    public void showSmartTabLayout(){
        mMainActivity.mSmartTabLayout.setVisibility(View.VISIBLE);
    }


    public void hideCollectionSwitchLayout(){
        mMainActivity.mCollectionSwitchLayout.setVisibility(View.GONE);
    }

    public void showCollectionSwitchLayout(){
        mMainActivity.mCollectionSwitchLayout.setVisibility(View.VISIBLE);
    }

    public void hideMinimalBottomPanel(){
        mMainActivity.mMinimalFragmentBottomLayout.setVisibility(View.GONE);
    }

    public void showMinimalBottomPanel(){
        mMainActivity.mMinimalFragmentBottomLayout.setVisibility(View.VISIBLE);
    }

}
