package zebrostudio.wallr100.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import zebrostudio.wallr100.di.ApplicationScope;

@Singleton
public class SharedPrefsUtils {

    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor mSharedPreferencesEditor;
    private Context mContext;

    @Inject
    public SharedPrefsUtils(@ApplicationScope Application context){
        mContext = context;
    }

    public void init(){
        mSharedPreference = mContext.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreference.edit();
    }

    public boolean writeIntData(String keyName, int value){
        return mSharedPreferencesEditor.putInt(keyName,value).commit();
    }

    public int readIntData(String keyName){
        return mSharedPreference.getInt(keyName,0);
    }

    public boolean writeStringData(String keyName, String value){
        return mSharedPreferencesEditor.putString(keyName,value).commit();
    }

    public String readStringData(String keyName){
        return mSharedPreference.getString(keyName,null);
    }

    public boolean writeBooleanData(String keyName, boolean value){
        return mSharedPreferencesEditor.putBoolean(keyName,value).commit();
    }

    public boolean readBooleanData(String keyName){
        return mSharedPreference.getBoolean(keyName,false);
    }

}
