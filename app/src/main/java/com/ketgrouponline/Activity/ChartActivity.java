package com.ketgrouponline.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ketgrouponline.Adapter.MarketAdapter;
import com.ketgrouponline.Bean.MarketBean;
import com.ketgrouponline.Network.MyApplication;
import com.ketgrouponline.R;
import com.ketgrouponline.Storage.SPCsnstants;
import com.ketgrouponline.Utils.Endpoints;
import com.ketgrouponline.Utils.MyUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartActivity extends AppCompatActivity {
    private static final String TAG = "ChartActivity";
    RecyclerView recyclerView;
    MarketAdapter adapter;
    List<MarketBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        initToolBar();
        initView();
    }

    public void initView() {
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new MarketAdapter(list, ChartActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(ChartActivity.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        getMarket();
    }


    private void getMarket() {
        MyUtils.showProgressDialog(ChartActivity.this, false);
        StringRequest request = new StringRequest(Request.Method.GET, "http://sattamatkatv.com/index.php/api/getPanelVendorsList", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MyUtils.dismisProgressDialog();
                Log.i(TAG, "getMarket " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        JSONArray data = object.getJSONArray("data");
                        if (data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject object1 = data.getJSONObject(i);
                                MarketBean bean = new MarketBean();
                                bean.setVendor_id(object1.getString("vendor_id"));
                                bean.setVendor_name(object1.getString("vendor_name"));
                                list.add(bean);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.dismisProgressDialog();
                MyUtils.showVolleyError(error, TAG, ChartActivity.this);
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
        titleTv.setText("Chart");


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
                MyUtils.showVolleyError(error, TAG, ChartActivity.this);
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

    public void getContactData() {
        StringRequest request = new StringRequest(Request.Method.GET, Endpoints.getContactDetails, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "getData " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("return")) {
                        JSONObject data = object.getJSONObject("data");
                        TextView numberTv = findViewById(R.id.numberTv);
                        numberTv.setText(data.getString("mobile"));
                        String name = data.getString("name");
                        String mobile = data.getString("mobile");

                        TextView nameTv = findViewById(R.id.nameTv);
                        nameTv.setText(name);

                        ImageView whatsAppIv = findViewById(R.id.whatsAppIv1);
                        ImageView callIv = findViewById(R.id.callIv);
                        whatsAppIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + mobile + "&text=" + "Hello"));
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
    }
}