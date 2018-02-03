package zebrostudio.wallr100.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.di.component.ActivityComponent;
import zebrostudio.wallr100.di.component.DaggerActivityComponent;
import zebrostudio.wallr100.di.module.ActivityModule;
import zebrostudio.wallr100.di.module.MainActivityModule;
import zebrostudio.wallr100.utils.GuillotineUtils;
import zebrostudio.wallr100.utils.SharedPrefsUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityComponent mActivityComponent;
    private Unbinder mUnBinder;
    GuillotineAnimation guillotineAnimation;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.root_layout)
    FrameLayout mRootView;
    @Inject
    GuillotineUtils mGuillotineUtils;
    @Inject
    SharedPrefsUtils mSharedPrefsUtils;
    @Inject
    Crashlytics crashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);
        mUnBinder = ButterKnife.bind(this);
        Fabric.with(this, crashlytics);

        setUpToolbar();
        configureNavigationBar();

        mSharedPrefsUtils.initSharedPrefs();

        mGuillotineUtils.inflateAndAddGuillotineMenu(this,mRootView);
        mGuillotineUtils.setToolbar(mToolbar);
        mGuillotineUtils.setGuillotineListener();
        mGuillotineUtils.guillotineAnimationBuilder();
    }

    public ActivityComponent getActivityComponent(){
        if (mActivityComponent == null){
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .mainActivityModule(new MainActivityModule(this))
                    .build();
        }
        return mActivityComponent;
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
