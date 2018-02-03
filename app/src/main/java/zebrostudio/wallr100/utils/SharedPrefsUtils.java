package zebrostudio.wallr100.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import zebrostudio.wallr100.di.ActivityContext;

public class SharedPrefsUtils {

    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor mSharedPreferencesEditor;
    private Context mContext;

    @Inject
    public SharedPrefsUtils(@ActivityContext Activity context){
        mContext = context;
    }

    public void initSharedPrefs(){
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
