package com.ketgrouponline.Utils;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.Network.MyVolley;
import com.ketgrouponline.R;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MyUtils {
    private static final String TAG = "MyUtils";
    private static AlertDialog dialog;

    private static final ThreadLocal<SimpleDateFormat> sdfTime = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("hh:mm:ss");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> sdfDateTime = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy | hh:mm aa");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            return sdf;
        }
    };

    private static final ThreadLocal<SimpleDateFormat> sdfDate = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            return sdf;
        }
    };

    private static final ThreadLocal<SimpleDateFormat> sdfTimeDisplay = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            return sdf;
        }
    };

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public static void showVolleyError(VolleyError error, String TAG, Context context) {
        if (context == null) return;
        error.printStackTrace();
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                Log.d(TAG, "simpleVolleyRequestError: " + body);
                if (statusCode == 400 || statusCode == 401) {
                    JSONObject obj = new JSONObject(body);
                    if(!obj.getBoolean("return")){
                        JSONObject errors = obj.getJSONObject("errors");
                        Toast.makeText(context, errors.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorString = MyVolley.handleVolleyError(error);
                    Toast.makeText(context, errorString, Toast.LENGTH_LONG).show();
                }

            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e + "", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void showProgressDialog(Context ctx, boolean cancelable) {
        if (ctx == null) return;
        dismisProgressDialog();
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            View v = LayoutInflater.from(ctx).inflate(R.layout.progresslay, null, false);
            builder.setView(v);
            builder.setCancelable(cancelable);
            dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismisProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception ignored) {}
        }
        dialog = null;
    }

    public static long convertDateTimeToTimeStamp(String onlyDate) {
        try {
            Date date = sdfTime.get().parse(onlyDate);
            return date != null ? date.getTime() / 1000 : 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String convertDateTime(String timestamp) {
        try {
            long unixSeconds = Long.parseLong(timestamp);
            Date date = new Date(unixSeconds * 1000L);
            return sdfDateTime.get().format(date);
        } catch (Exception e) {
            return timestamp;
        }
    }

    public static String convertDate(String timestamp) {
        try {
            long unixSeconds = Long.parseLong(timestamp);
            Date date = new Date(unixSeconds * 1000L);
            return sdfDate.get().format(date);
        } catch (Exception e) {
            return timestamp;
        }
    }

    public static String convertTime(String timestamp) {
        try {
            long unixSeconds = Long.parseLong(timestamp);
            Date date = new Date(unixSeconds * 1000L);
            return sdfTimeDisplay.get().format(date);
        } catch (Exception e) {
            return timestamp;
        }
    }

    public static boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView != null && recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }
}