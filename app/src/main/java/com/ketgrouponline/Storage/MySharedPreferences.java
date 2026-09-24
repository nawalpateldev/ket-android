package com.ketgrouponline.Storage;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    private static SharedPreferences mSharedPreference;
    private static MySharedPreferences mInstance = null;
    private static Context mContext;
    private String KEY_DEFAULT = null;

    private MySharedPreferences() {
        String KEY_PREFERENCE_NAME = "travier";
        mSharedPreference = mContext.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static MySharedPreferences getInstance(Context context) {
        if (context != null) {
            mContext = context.getApplicationContext();
        }
        if (mInstance == null) {
            mInstance = new MySharedPreferences();
        }
        return mInstance;
    }

    public boolean setKey(String keyname, String mobile) {
        mSharedPreference.edit().putString(keyname, mobile).apply();
        return false;
    }

    public String getKey(String keyname) {
        return mSharedPreference.getString(keyname, KEY_DEFAULT);
    }

    public int getInt(String key) {
        return mSharedPreference.getInt(key, 0);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.remove(key);
        editor.apply();
    }
}