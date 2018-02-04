package zebrostudio.wallr100.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerAppCompatActivity;
import io.fabric.sdk.android.Fabric;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.utils.GuillotineUtils;
import zebrostudio.wallr100.utils.GuillotineViewObject;
import zebrostudio.wallr100.utils.canaroTextViewUtils.CanaroTextView;

public class MainActivity extends DaggerAppCompatActivity implements MainActivityContract.View {

    private Unbinder mUnBinder;

    @Inject
    Crashlytics crashlytics;
    @Inject
    GuillotineUtils mGuillotineUtils;
    @Inject
    GuillotineViewObject mGuillotineViewObject;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.root_layout)
    FrameLayout mRootView;
    @BindView(R.id.content_hamburger)
    View mHamburgerIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUnBinder = ButterKnife.bind(this);
        Fabric.with(this, crashlytics);

        setUpToolbar();
        configureNavigationBar();

        mGuillotineUtils.init(this,
                mRootView,
                mToolbar,
                mHamburgerIcon);
        initGuillotineViewObject();
        mGuillotineUtils.takeViewObject(mGuillotineViewObject);

    }

    private void initGuillotineViewObject() {

        mGuillotineViewObject.setmExploreLayout((LinearLayout) findViewById(R.id.explore_group));
        mGuillotineViewObject.setmExploreTitleView((CanaroTextView) findViewById(R.id.explore_textview));
        mGuillotineViewObject.setmTopPicksLayout((LinearLayout) findViewById(R.id.top_picks_group));
        mGuillotineViewObject.setmTopPicksTitleView((CanaroTextView) findViewById(R.id.top_picks_textview));
        mGuillotineViewObject.setmCategoriesLayout((LinearLayout) findViewById(R.id.categories_group));
        mGuillotineViewObject.setmCategoriesTitleView((CanaroTextView) findViewById(R.id.categories_textview));
        mGuillotineViewObject.setmMinimalLayout((LinearLayout) findViewById(R.id.minimal_group));
        mGuillotineViewObject.setmMinimalTitleView((CanaroTextView) findViewById(R.id.minimal_textview));
        mGuillotineViewObject.setmCollectionLayout((LinearLayout) findViewById(R.id.collection_group));
        mGuillotineViewObject.setmCollectionTitleView((CanaroTextView) findViewById(R.id.collection_textview));
        mGuillotineViewObject.setmFeedBackLayout((LinearLayout) findViewById(R.id.feedback_group));
        mGuillotineViewObject.setmBuyProLayout((LinearLayout) findViewById(R.id.buy_pro_group));
        mGuillotineViewObject.setmBuyProTitleView((CanaroTextView) findViewById(R.id.buy_pro_textview));

    }

    private void setUpToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(null);
        }
    }

    private void configureNavigationBar() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_primary_dark));
        }
    }


}
