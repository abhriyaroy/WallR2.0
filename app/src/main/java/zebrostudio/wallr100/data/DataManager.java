package zebrostudio.wallr100.data;


import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;
import com.onesignal.OneSignal;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.disposables.CompositeDisposable;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.utils.SharedPrefsUtils;

@Singleton
public class DataManager {

    private String mCurrentlyInflatedFragmentTag = "";
    private FireBaseManager mFireBaseManager;
    private SharedPrefsUtils mSharedPrefsUtils;
    private CompositeDisposable mCompositeDisposable;

    public DataManager(FireBaseManager fireBaseManager,
                       SharedPrefsUtils sharedPrefsUtils,
                       CompositeDisposable compositeDisposable){
        mFireBaseManager = fireBaseManager;
        mSharedPrefsUtils = sharedPrefsUtils;
        mSharedPrefsUtils.init();
        mCompositeDisposable = compositeDisposable;
    }

    public void requestFirebasePersistenceInitialization(Application application){
        mCompositeDisposable.add(mFireBaseManager.configureFirebasePersistence(application)
                .subscribe());
    }

    public void requestOneSignalSdkInitialization(Application application){
        OneSignal.startInit(application)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    public void requestGlideTagConfiguration() {
        ViewTarget.setTagId(R.id.glide_tag);
    }

    public void requestAutomaticWallpaperChangerJobInitialization(){

    }

    public String getCurrentlyInflatedFragmentTag() {
        return mCurrentlyInflatedFragmentTag;
    }

    public void setCurrentlyInflatedFragmentTag(String mCurrentlyInflatedFragmentTag) {
        this.mCurrentlyInflatedFragmentTag = mCurrentlyInflatedFragmentTag;
    }

    public boolean checkIfProLocal(){
       return mSharedPrefsUtils.getBooleanData("purchased");
    }

    public void disposeObservables(){
        mCompositeDisposable.dispose();
    }
}
