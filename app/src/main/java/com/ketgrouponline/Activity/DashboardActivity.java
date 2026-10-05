package com.ketgrouponline.Activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ketgrouponline.Fragment.ChangePasswordFragment;
import com.ketgrouponline.Fragment.HomeFragment;
import com.ketgrouponline.Fragment.ProfileFragment;
import com.ketgrouponline.Fragment.WalletFragment;
import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.R;
import com.ketgrouponline.Storage.MySharedPreferences;
import com.ketgrouponline.Storage.SPCsnstants;
import com.ketgrouponline.Utils.Endpoints;
import com.ketgrouponline.Utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    private NavigationView navigationView;
    private DrawerLayout drawer;
    TextView name, email;
    ImageView menuIv;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        View header = navigationView.getHeaderView(0);
        name = header.findViewById(R.id.name);
        email = header.findViewById(R.id.email);

        String userName = MySharedPreferences.getInstance(DashboardActivity.this).getKey(SPCsnstants.NAME_KEY);
        if (userName != null && !userName.isEmpty() && !userName.equalsIgnoreCase("null")) {
            String firstLetter = userName.substring(0, 1).toUpperCase();
            String remainingLetters = userName.substring(1);
            name.setText(firstLetter + remainingLetters);
        } else {
            name.setText("");
        }
        String userEmail = MySharedPreferences.getInstance(DashboardActivity.this).getKey(SPCsnstants.EMAIL_KEY);
        if (userEmail != null && !userEmail.isEmpty() && !userEmail.equalsIgnoreCase("null")) {
            email.setText(userEmail);
        } else {
            email.setText("");
        }
        Fragment fragment;
        fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "home").commit();

        setUpNavigationView();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initToolBar();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextView walletTv = findViewById(R.id.walletTv);
                wallet(walletTv);
            }
        };
    }

    public void updateFcmToken(final String fcm_token) {
        Log.e(TAG, "updateFcmToken: " + fcm_token);
        StringRequest request = new StringRequest(Request.Method.POST, Endpoints.updateFcm, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "updateFcmToken : " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fcm_token", fcm_token);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", MyApplication.sharedPreferences.getKey(SPCsnstants.api_token));
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.home) {
                    Fragment fragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "home").commit();
                } else if (id == R.id.ticket) {
                    startActivity(new Intent(DashboardActivity.this, TicketActivity.class));
                } else if (id == R.id.chart) {
                    startActivity(new Intent(DashboardActivity.this, ChartActivity.class));
                } else if (id == R.id.game_rate) {
                    startActivity(new Intent(DashboardActivity.this, GameRateActivity.class));
                } else if (id == R.id.help) {
                    getContactData();
                } else if (id == R.id.aboutUs) {
                    startActivity(new Intent(DashboardActivity.this, AboutUsActivity.class));
                } else if (id == R.id.logout) {
                    exitConfirmDialog("KET GROUP", "You really want to logout from ketgrouponline ? ");
                } else if (id == R.id.share) {
                    try {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "KET GROUP");
                        String shareMessage = "\nDownload from play store.\n\n";
                        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        startActivity(Intent.createChooser(shareIntent, "choose one"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawers();
                }
                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, null, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.home) {
                fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "home").commit();
                return true;
            } else if (id == R.id.wallet) {
                fragment = new WalletFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "wallet").commit();
                return true;
            } else if (id == R.id.change_password) {
                fragment = new ChangePasswordFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "change_password").commit();
                return true;
            } else if (id == R.id.profile) {
                fragment = new ProfileFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "profile").commit();
                return true;
            }
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawers();
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ContextCompat.registerReceiver(this, broadcastReceiver, new IntentFilter("wallet"), ContextCompat.RECEIVER_NOT_EXPORTED);
        TextView walletTv = findViewById(R.id.walletTv);
        wallet(walletTv);
        checkVersion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            this.unregisterReceiver(broadcastReceiver);
        }
    }

    private void wallet(TextView walletTv) {
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.wallet, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "wallet " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        walletTv.setText(object.getString("wallet"));
                        walletTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Fragment fragment = new WalletFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment, "wallet").commit();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, DashboardActivity.this);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", MyApplication.sharedPreferences.getKey(SPCsnstants.api_token));
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }

    public void initToolBar() {
        TextView titleTv = findViewById(R.id.titleTv);
        TextView walletTv = findViewById(R.id.walletTv);
        wallet(walletTv);
        ImageView whatsAppIv = findViewById(R.id.whatsAppIv);
        titleTv.setText("KET GROUP");
        whatsAppIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + MyApplication.sharedPreferences.getKey(SPCsnstants.whatsAppNumber) + "&text=" + "Hello"));
                startActivity(intent);
            }
        });

        ImageView menuIv = findViewById(R.id.menuIv);
        menuIv.setVisibility(View.VISIBLE);
        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT | GravityCompat.START);
            }
        });
    }

    public void getContactData() {
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.getContactDetails, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "getContactData " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        JSONObject data = object.getJSONObject("data");
                        String youtube_help_video = data.getString("youtube_help_video");
                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtube_help_video));
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + youtube_help_video));
                        try {
                            startActivity(appIntent);
                        } catch (ActivityNotFoundException ex) {
                            startActivity(webIntent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, getApplicationContext());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", MyApplication.sharedPreferences.getKey(SPCsnstants.api_token));
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);


        ImageView whatsAppIv = findViewById(R.id.whatsAppIv1);
        ImageView callIv = findViewById(R.id.callIv);
        whatsAppIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " + mobile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + mobile + "&text=" + "Hi ketgrouponline"));
                startActivity(intent);
            }
        });

        callIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mobile));
                startActivity(intent);
            }
        });
    }

    String mobile;

    private void exitConfirmDialog(String title, String message) {
        Dialog dialogForConfirmation = new Dialog(DashboardActivity.this);
        dialogForConfirmation.setContentView(R.layout.logout_confirm_dialog_layout);
        TextView messageTv = dialogForConfirmation.findViewById(R.id.messageTv);
        TextView titleTv = dialogForConfirmation.findViewById(R.id.titleTv);
        titleTv.setText(title);
        messageTv.setText(message);

        dialogForConfirmation.findViewById(R.id.fbCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForConfirmation.dismiss();
            }
        });

        dialogForConfirmation.findViewById(R.id.yesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForConfirmation.dismiss();
                if (message.contains("logout")) {
                    logout();
                }
            }
        });

        dialogForConfirmation.findViewById(R.id.noButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForConfirmation.dismiss();
            }
        });

        dialogForConfirmation.getWindow().setBackgroundDrawableResource(R.color.blacktrans);
        Window window = dialogForConfirmation.getWindow();
        window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        dialogForConfirmation.show();
    }

    public void logout() {
        MyApplication.sharedPreferences.setKey(SPCsnstants.api_token, null);
        MyApplication.sharedPreferences.setKey(SPCsnstants.ID_KEY, null);
        MyApplication.sharedPreferences.setKey(SPCsnstants.NAME_KEY, null);
        MyApplication.sharedPreferences.setKey(SPCsnstants.EMAIL_KEY, null);
        MyApplication.sharedPreferences.setKey(SPCsnstants.MOBILE_KEY, null);
        Intent intent = new Intent(DashboardActivity.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void checkVersion() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null && info.versionName != null) {
            try {
                getVersion(Double.parseDouble(info.versionName));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }


    public void getVersion(double version) {
        Log.e(TAG, "getVersion: " + version);
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.app_version, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "getVersion : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("return")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (data.getDouble("version") > version) {
                            showUpdateDialog(true);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("Authorization", MyApplication.sharedPreferences.getKey(SPCsnstants.api_token));
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }

    Dialog dialog;

    public void showUpdateDialog(boolean isForceUpdate) {
        dialog = new Dialog(DashboardActivity.this);
        TextView textView;
        Button updateButton;
        dialog.setContentView(R.layout.location_dialog_layout);
        textView = dialog.findViewById(R.id.textView);
        updateButton = dialog.findViewById(R.id.updateButton);
        updateButton.setVisibility(View.VISIBLE);
        textView.setText("New Update available now, Please update ketgrouponline App from Play Store.");
        dialog.findViewById(R.id.regisfabcancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.color.blacktrans);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }
}