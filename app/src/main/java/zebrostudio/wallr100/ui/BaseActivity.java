package zebrostudio.wallr100.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import butterknife.Unbinder;
import zebrostudio.wallr100.di.component.ActivityComponent;
import zebrostudio.wallr100.di.component.DaggerActivityComponent;

/**
 * Created by royab on 31-01-2018.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private ActivityComponent mActivityComponent;
    private Unbinder mUnBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ActivityComponent getActivityComponent(){
        if (mActivityComponent == null){
            mActivityComponent = DaggerActivityComponent.builder()
                    .build();
        }
        return mActivityComponent;
    }

    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }

    public SharedPreferences getSharedPreferences(){
        return getSharedPreferences("preferences", Activity.MODE_PRIVATE);
    }

}

