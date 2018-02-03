package zebrostudio.wallr100.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import zebrostudio.wallr100.ui.MainActivity;

public class SharedPrefsUtils {

    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor mSharedPreferencesEditor;
    private Context mContext;

    @Inject
    public SharedPrefsUtils(MainActivity context){
        mContext = context;
    }

    public void initSharedPrefs(){
        mSharedPreference = mContext.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreference.edit();
    }

    public void writeData(){

    }

    public int readIntData(){
        return 1;
    }

}
