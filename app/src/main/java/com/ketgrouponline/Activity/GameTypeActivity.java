package com.ketgrouponline.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.R;
import com.ketgrouponline.Storage.SPCsnstants;
import com.ketgrouponline.Utils.Endpoints;
import com.ketgrouponline.Utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GameTypeActivity extends AppCompatActivity {
    private static final String TAG = "GameTypeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_type);
        TextView titleTv2 = findViewById(R.id.titleTv2);
        titleTv2.setVisibility(View.VISIBLE);
        titleTv2.setText("Market Name - "+getIntent().getStringExtra("market"));

        findViewById(R.id.singleDigitLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameTypeActivity.this, SingleDigitActivity.class);
                intent.putExtra("vendor_id", getIntent().getStringExtra("vendor_id"));
                intent.putExtra("market", getIntent().getStringExtra("market"));
                startActivity(intent);
            }
        });
        findViewById(R.id.layout2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameTypeActivity.this, JodiDigitActivity.class);
                intent.putExtra("vendor_id", getIntent().getStringExtra("vendor_id"));
                intent.putExtra("type", "jodi");
                intent.putExtra("name", "Jodi Digit");
                intent.putExtra("market", getIntent().getStringExtra("market"));
                startActivity(intent);
            }
        });

        findViewById(R.id.layout3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameTypeActivity.this, JodiDigitActivity.class);
                intent.putExtra("vendor_id", getIntent().getStringExtra("vendor_id"));
                intent.putExtra("type", "single_pana");
                intent.putExtra("name", "Single Pana");
                intent.putExtra("market", getIntent().getStringExtra("market"));
                startActivity(intent);
            }
        });

        findViewById(R.id.layout4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameTypeActivity.this, JodiDigitActivity.class);
                intent.putExtra("vendor_id", getIntent().getStringExtra("vendor_id"));
                intent.putExtra("type", "double_pana");
                intent.putExtra("name", "Double Pana");
                intent.putExtra("market", getIntent().getStringExtra("market"));
                startActivity(intent);
            }
        });

        findViewById(R.id.layout5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameTypeActivity.this, JodiDigitActivity.class);
                intent.putExtra("vendor_id", getIntent().getStringExtra("vendor_id"));
                intent.putExtra("type", "triple_pana");
                intent.putExtra("name", "Triple Pana");
                intent.putExtra("market", getIntent().getStringExtra("market"));
                startActivity(intent);
            }
        });
        initToolBar();
    }

    public void initToolBar() {
        TextView titleTv = findViewById(R.id.titleTv);
        TextView walletTv = findViewById(R.id.walletTv);
        wallet(walletTv);
        titleTv.setText("Game Type");


        ImageView menuIv = findViewById(R.id.backIv);
        menuIv.setVisibility(View.VISIBLE);
        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //drawer.openDrawer(Gravity.LEFT | GravityCompat.START);
            }
        });
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, GameTypeActivity.this);
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

    @Override
    protected void onResume() {
        super.onResume();
        getContactData();
        TextView walletTv = findViewById(R.id.walletTv);
        wallet(walletTv);
    }

    String name = "", mobile = "";

    public void getContactData() {
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.getContactDetails, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "getContactData " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        JSONObject data = object.getJSONObject("data");
                        TextView numberTv = findViewById(R.id.numberTv);
                        numberTv.setText(data.getString("mobile"));
                        name = data.getString("name");
                        mobile = data.getString("mobile");

                        TextView nameTv = findViewById(R.id.nameTv);
                        nameTv.setText(name);
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
}