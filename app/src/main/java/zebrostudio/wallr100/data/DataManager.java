package zebrostudio.wallr100.data;


import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.bumptech.glide.request.target.ViewTarget;
import com.onesignal.OneSignal;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.di.ApplicationContext;
import zebrostudio.wallr100.utils.SharedPrefsUtils;

/**
 * Serves as a manager to provide necessary data to the presenters or to perform jobs
 * in the background as requested by the presenters.
 */
@Singleton
public class DataManager {

    private String mCurrentlyInflatedFragmentTag = "";
    private FirebaseManager mFirebaseManager;
    private SharedPrefsUtils mSharedPrefsUtils;
    private CompositeDisposable mCompositeDisposable;
    private boolean mIsIabConfigured;
    private Context mApplicationContext;

    @Inject
    public DataManager(@ApplicationContext Context context,
                       FirebaseManager firebaseManager,
                       SharedPrefsUtils sharedPrefsUtils,
                       CompositeDisposable compositeDisposable) {
        mApplicationContext = context;
        mFirebaseManager = firebaseManager;
        mSharedPrefsUtils = sharedPrefsUtils;
        mCompositeDisposable = compositeDisposable;
    }

    public void requestFirebasePersistenceInitialization(Application application) {
        mCompositeDisposable.add(mFirebaseManager.completableFirebasePersistence(application)
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

    public boolean isIabConfigured() {
        return mIsIabConfigured;
    }

    private Observable<Void> shouldVerifyPurchaseRemote(final PackageManager packageManager) {
        return Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> observableEmitter) throws Exception {
                try {
                    ApplicationInfo appInfo = packageManager
                            .getApplicationInfo("zebrostudio.wallr100", 0);
                    String appFile = appInfo.sourceDir;
                    long installedTimeStamp = new File(appFile).lastModified();
                    long installedTimePeriod = System.currentTimeMillis() - installedTimeStamp;
                    long lastChecked = System.currentTimeMillis()
                            - mSharedPrefsUtils.getLongData("lastCheckedForPro");
                    if (installedTimePeriod >= 345600000 && lastChecked >= 604800000
                            && isIabConfigured()) {
                        observableEmitter.onComplete();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
