package com.ketgrouponline.Network;

import android.app.Application;
import android.content.Context;

import com.ketgrouponline.Storage.MySharedPreferences;
import com.android.volley.RequestQueue;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static MyApplication mInstance;
    public static RequestQueue mRequestQue;
    public static MySharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPreferences = MySharedPreferences.getInstance(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }
}