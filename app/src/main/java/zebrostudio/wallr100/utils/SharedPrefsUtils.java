package zebrostudio.wallr100.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import zebrostudio.wallr100.di.ApplicationContext;

@Singleton
public class SharedPrefsUtils {

    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor mSharedPreferencesEditor;
    private Context mContext;

    @Inject
    public SharedPrefsUtils(@ApplicationContext Context context){
        mContext = context;
        init();
    }

    public void init(){
        mSharedPreference = mContext.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreference.edit();
    }

    public boolean setIntData(String keyName, int value){
        return mSharedPreferencesEditor.putInt(keyName,value).commit();
    }

    public int getIntData(String keyName){
        return mSharedPreference.getInt(keyName,0);
    }

    public boolean setLongData(String keyName, long value){
        return mSharedPreferencesEditor.putLong(keyName,value).commit();
    }

    public long getLongData(String keyName){
        return mSharedPreference.getLong(keyName,0);
    }

    public boolean setStringData(String keyName, String value){
        return mSharedPreferencesEditor.putString(keyName,value).commit();
    }

    public String getStringData(String keyName){
        return mSharedPreference.getString(keyName,null);
    }

    public boolean setBooleanData(String keyName, boolean value){
        return mSharedPreferencesEditor.putBoolean(keyName,value).commit();
    }

    public boolean getBooleanData(String keyName){
        return mSharedPreference.getBoolean(keyName,false);
    }

}
