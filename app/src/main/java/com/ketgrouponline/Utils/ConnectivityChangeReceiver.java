package com.ketgrouponline.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ketgrouponline.Activity.NoInternetActivity;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "ConnectivityChangeRecei";

    @Override
    public void onReceive(Context context, Intent intent) {
        debugIntent(intent, context);
    }

    private void debugIntent(Intent intent, Context context) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            boolean noConnectivity = false;
            for (String key : extras.keySet()) {
                Log.e(TAG, key + " : " +
                        extras.get(key) + "\n");
                if (key.equals("noConnectivity")) {
                    noConnectivity = true;
                }
            }
            if (noConnectivity) {
                Intent intent1 = new Intent(context, NoInternetActivity.class);
                context.startActivity(intent1);
            }
        }
    }

}

