package zebrostudio.wallr100.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import butterknife.ButterKnife;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.di.component.ActivityComponent;

public class MainActivity extends BaseActivity {

    private SharedPreferences mSharedPrefernce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        mSharedPrefernce = getSharedPreferences();
    }

}
