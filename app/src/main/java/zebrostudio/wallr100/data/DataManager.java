package zebrostudio.wallr100.data;


import android.app.Application;
import android.content.Context;

import com.bumptech.glide.request.target.ViewTarget;
import com.onesignal.OneSignal;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.di.ApplicationContext;
import zebrostudio.wallr100.utils.SharedPrefsUtils;

@Singleton
public class DataManager {

    private String mCurrentlyInflatedFragmentTag = "";
    private FireBaseManager mFireBaseManager;
    private IabManager mIabManager;
    private SharedPrefsUtils mSharedPrefsUtils;
    private CompositeDisposable mCompositeDisposable;
    private boolean isIabSetup;
    private Context mApplicationContext;

    @Inject
    public DataManager(@ApplicationContext Context context,
                       FireBaseManager fireBaseManager,
                       IabManager iabManager,
                       SharedPrefsUtils sharedPrefsUtils,
                       CompositeDisposable compositeDisposable) {
        mApplicationContext = context;
        mFireBaseManager = fireBaseManager;
        mIabManager = iabManager;
        mSharedPrefsUtils = sharedPrefsUtils;
        mCompositeDisposable = compositeDisposable;
    }

    public void initResources() {
        mSharedPrefsUtils.init();

    }

    public void requestFirebasePersistenceInitialization(Application application) {
        mCompositeDisposable.add(mFireBaseManager.completableFirebasePersistence(application)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public void requestOneSignalSdkInitialization(Application application) {
        OneSignal.startInit(application)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    public void requestGlideTagConfiguration() {
        ViewTarget.setTagId(R.id.glide_tag);
    }

    public void requestAutomaticWallpaperChangerJobInitialization() {

    }

    public String getCurrentlyInflatedFragmentTag() {
        return mCurrentlyInflatedFragmentTag;
    }

    public void setCurrentlyInflatedFragmentTag(String mCurrentlyInflatedFragmentTag) {
        this.mCurrentlyInflatedFragmentTag = mCurrentlyInflatedFragmentTag;
    }

    public boolean checkIfProLocal() {
        return mSharedPrefsUtils.getBooleanData("purchased");
    }

    public void disposeObservables() {
        mCompositeDisposable.dispose();
    }
}
