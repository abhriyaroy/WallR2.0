package zebrostudio.wallr100.data;


import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;
import com.onesignal.OneSignal;

import javax.inject.Inject;
import javax.inject.Singleton;

import zebrostudio.wallr100.R;
import zebrostudio.wallr100.utils.SharedPrefsUtils;

@Singleton
public class DataManager {

    private String mCurrentlyInflatedFragmentTag = "";
    private FireBaseManager mFireBaseManager;
    SharedPrefsUtils mSharedPrefsUtils;

    public DataManager(FireBaseManager fireBaseManager, SharedPrefsUtils sharedPrefsUtils){
        mFireBaseManager = fireBaseManager;
        mSharedPrefsUtils = sharedPrefsUtils;
        mSharedPrefsUtils.init();
    }

    public void requestFirebasePersistenceInitialization(Application application){
        mFireBaseManager.configureFirebasePersistence(application).subscribe();
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
}
