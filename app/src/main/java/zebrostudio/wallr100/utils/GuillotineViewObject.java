package zebrostudio.wallr100.utils;

import android.widget.LinearLayout;

import javax.inject.Inject;

import zebrostudio.wallr100.utils.canaroTextViewUtils.CanaroTextView;

/**
 * Created by royab on 04-02-2018.
 */

public class GuillotineViewObject {

    private LinearLayout mExploreLayout;
    private CanaroTextView mExploreTitleView;
    //@BindView(R.id.top_picks_group)
    private LinearLayout mTopPicksLayout;
    private CanaroTextView mTopPicksTitleView;
    //@BindView(R.id.categories_group)
    private LinearLayout mCategoriesLayout;
    private CanaroTextView mCategoriesTitleView;
    //@BindView(R.id.minimal_group)
    private LinearLayout mMinimalLayout;
    private CanaroTextView mMinimalTitleView;
    //@BindView(R.id.collection_group)
    private LinearLayout mCollectionLayout;
    private CanaroTextView mCollectionTitleView;
    //@BindView(R.id.feedback_group)
    private LinearLayout mFeedBackLayout;
    private CanaroTextView mFeedBackTitleView;
    //@BindView(R.id.buy_pro_group)
    private LinearLayout mBuyProLayout;
    private CanaroTextView mBuyProTitleView;

    @Inject
    GuillotineViewObject() {

    }

    public LinearLayout getmExploreLayout() {
        return mExploreLayout;
    }

    public void setmExploreLayout(LinearLayout mExploreLayout) {
        this.mExploreLayout = mExploreLayout;
    }

    public CanaroTextView getmExploreTitleView() {
        return mExploreTitleView;
    }

    public void setmExploreTitleView(CanaroTextView mExploreTitleView) {
        this.mExploreTitleView = mExploreTitleView;
    }

    public LinearLayout getmTopPicksLayout() {
        return mTopPicksLayout;
    }

    public void setmTopPicksLayout(LinearLayout mTopPicksLayout) {
        this.mTopPicksLayout = mTopPicksLayout;
    }

    public CanaroTextView getmTopPicksTitleView() {
        return mTopPicksTitleView;
    }

    public void setmTopPicksTitleView(CanaroTextView mTopPicksTitleView) {
        this.mTopPicksTitleView = mTopPicksTitleView;
    }

    public LinearLayout getmCategoriesLayout() {
        return mCategoriesLayout;
    }

    public void setmCategoriesLayout(LinearLayout mCategoriesLayout) {
        this.mCategoriesLayout = mCategoriesLayout;
    }

    public CanaroTextView getmCategoriesTitleView() {
        return mCategoriesTitleView;
    }

    public void setmCategoriesTitleView(CanaroTextView mCategoriesTitleView) {
        this.mCategoriesTitleView = mCategoriesTitleView;
    }

    public LinearLayout getmMinimalLayout() {
        return mMinimalLayout;
    }

    public void setmMinimalLayout(LinearLayout mMinimalLayout) {
        this.mMinimalLayout = mMinimalLayout;
    }

    public CanaroTextView getmMinimalTitleView() {
        return mMinimalTitleView;
    }

    public void setmMinimalTitleView(CanaroTextView mMinimalTitleView) {
        this.mMinimalTitleView = mMinimalTitleView;
    }

    public LinearLayout getmCollectionLayout() {
        return mCollectionLayout;
    }

    public void setmCollectionLayout(LinearLayout mCollectionLayout) {
        this.mCollectionLayout = mCollectionLayout;
    }

    public CanaroTextView getmCollectionTitleView() {
        return mCollectionTitleView;
    }

    public void setmCollectionTitleView(CanaroTextView mCollectionTitleView) {
        this.mCollectionTitleView = mCollectionTitleView;
    }

    public LinearLayout getmFeedBackLayout() {
        return mFeedBackLayout;
    }

    public void setmFeedBackLayout(LinearLayout mFeedBackLayout) {
        this.mFeedBackLayout = mFeedBackLayout;
    }

    public CanaroTextView getmFeedBackTitleView() {
        return mFeedBackTitleView;
    }

    public void setmFeedBackTitleView(CanaroTextView mFeedBackTitleView) {
        this.mFeedBackTitleView = mFeedBackTitleView;
    }

    public LinearLayout getmBuyProLayout() {
        return mBuyProLayout;
    }

    public void setmBuyProLayout(LinearLayout mBuyProLayout) {
        this.mBuyProLayout = mBuyProLayout;
    }

    public CanaroTextView getmBuyProTitleView() {
        return mBuyProTitleView;
    }

    public void setmBuyProTitleView(CanaroTextView mBuyProTitleView) {
        this.mBuyProTitleView = mBuyProTitleView;
    }
}
